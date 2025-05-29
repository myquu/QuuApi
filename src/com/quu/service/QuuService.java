package com.quu.service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.quu.dao.IQuuDAO;
import com.quu.util.Constant;
import com.quu.model.BBCampaign;
import com.quu.model.BBSchedule;
import com.quu.model.BBSchedules;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;
import com.quu.util.Scheduler;
import com.quu.util.Util;


@ApplicationScoped
public class QuuService implements IQuuService{

	@Inject
    private IQuuDAO quuDAO;
	
	public StationMaps getStations()
	{
		return quuDAO.getStations();
	}
	
	public List<RTLog> getStationRTLogs(String sid)
	{
		Map<String, Station> stationSidMap = Scheduler.stationMaps.getStationSidMap();
		
		Station station = stationSidMap.get(sid);
		
		final ZonedDateTime gmtCurrentDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(System.currentTimeMillis()/1000), ZoneId.of("GMT")),
        		stationCurrentDateTime = Util.getDateDetail(gmtCurrentDateTime.toLocalDateTime(), "GMT", station.getTzName());
		
		return quuDAO.getStationRTLogs(station.getId(), stationCurrentDateTime.format(Constant.dateFormatter));
	}
	
	public List<String> createBillboardsAndDependants(List<BBCampaign> campaignList)
	{
		List<String> errantCampaignNames = new ArrayList<String>();
		
		for(BBCampaign campaign : campaignList)
		{
			int status = 1;  //Change status to -1 only in case of an error during creation of the campaign or its schedule.
			
			String imageUrl = campaign.getImageUrl(),
				imageName = null;
			
			if(imageUrl != null)
        	{
        		imageName = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        		campaign.setImageName(imageName);
        	}
			
			List<String> list = Util.setDPSFields(campaign.getRt1(), campaign.getRt2());
	    	
	    	if(list != null)
	    	{
	    		//Populate DPS fields using reflection
	    		try 
	    		{
	    			for(int i=0; i<list.size(); i++)
	        		{
		    			PropertyDescriptor pd = new PropertyDescriptor("dps"+(i+1), campaign.getClass());
		    			// Call setter on specified property (setDps1())
		    			pd.getWriteMethod().invoke(campaign, list.get(i));
	        		}
			    } 
	    		catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
	    	}
			
			int campaignId = quuDAO.createBillboardAndRDSFields(campaign);
			
			if(campaignId != -1)
			{
				status = createBillboardSchedules(new BBSchedules(campaignId, campaign.getScheduleList()), false);
			}
			else
			{
				status = -1;
			}
			
			
			if(status == 1)
			{
				//Copy the image to our imageserver
				if(imageName != null)
    	    	{
    	    		final String imageNameF = imageName;
    	    		
    	    		new Thread(() -> saveImageOnImageserver(imageUrl, campaignId, imageNameF)).start();
    	    	}
				
				new Thread(() -> Util.clearQuuRDSCache()).start();
			}
			//If there was an error in creating the campaign or any of its schedules or any schedule had times in wrong format then deactivate the campaign. This is a new campaign being created.
			else
			{
				quuDAO.deactivateBillboard(campaignId);
				
				errantCampaignNames.add(campaign.getName());
			}
		}
				
		//If all campaigns were successfully created (and activated) OR if some of the campaigns were created (and activated) then create the ordering of the (active) campaigns so they can be reordered from the UI later.  
		new Thread(() -> {
			if(errantCampaignNames.isEmpty() || errantCampaignNames.size() < campaignList.size())
			{
				quuDAO.orderActiveRTCampaigns();
			}
		}).start();
		
		return errantCampaignNames;
	}
	
	public int createBillboardSchedules(BBSchedules schedules, boolean takeStatusAction)
	{
		int status = 1;  //Change status to -1 only in case of an error during creation of the schedule.
		
		int campaignId = schedules.getCampaignID();
				
		for(BBSchedule schedule : schedules.getScheduleList())
		{
			int scheduleId = quuDAO.createBillboardSchedule(campaignId, schedule);
			
			//If the times are not in mm:ss format then don't activate the campaign. Else GetJ2GCampaigns() data will throw error and bring the entire system down.
			if(scheduleId == -1 || !(schedule.getStart_time().matches("^[0-9]{2}:[0-9]{2}$") && schedule.getEnd_time().matches("^[0-9]{2}:[0-9]{2}$")))
			{
				status = -1;
			}
			//If this schedule has the ignore_automation flag set and there are ignore_automation stations in the schedule then for those stations copy the date and start and end times to another table. 
			else if(schedule.getBlock_automation() == 1)
			{
				Map<Integer, Station> stationIdMap = Scheduler.stationMaps.getStationIdMap();
				
				List<String> stationIds = schedule.getRadio_station_ids();
				
				for(String stationId : stationIds)  //We assume these station Ids exist in our DB
				{
					Station station = stationIdMap.get(Integer.valueOf(stationId));
					
					if(station.getIgnoreAutomation() == 1)
					{
						quuDAO.addStationNoAutomationSchedule(campaignId);
					}
				}
			}
		}
		
		//If it goes in the below block then we are adding schedules to an existing campaign which may or may not be active.
		if(takeStatusAction)
		{
			//If there was an error in creating any of the schedules or any schedule had times in wrong format then deactivate the campaign.
			if(status == -1)
			{
				quuDAO.deactivateBillboard(campaignId);
			}
			else
			{
				//quuDAO.activateBillboard(campaignId);
				
				//If all schedules were successfully created and the campaign was activated then create the ordering of the (active) campaigns so they can be reordered from the UI later.
				new Thread(() -> {
					quuDAO.orderActiveRTCampaigns();
					Util.clearQuuRDSCache();
				}).start();
			}
		}
		
		return status;
	}
	
	
	private void saveImageOnImageserver(String imageUrl, int id, String imageName)
    {
    	Map<String, String> params = new HashMap<String, String>();
		params.put("downloadFile", imageUrl);
		params.put("imagePath", "Campaigns/" + id);
		params.put("fileName", imageName);
		params.put("requestFrom", "QuuAPI");
		
		Util.getWebResponse(Constant.SAVEIMAGESERVICE_URL, params, false);
    }
}
