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
import com.quu.model.BBCampaignIn;
import com.quu.model.BBCampaignOut;
import com.quu.model.BBScheduleIn;
import com.quu.model.BBSchedulesIn;
import com.quu.model.BBCampaignsIn;
import com.quu.model.CartAssignment;
import com.quu.model.EmergencyBlastHotkey;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;
import com.quu.model.TimeAvailablePerHour;
import com.quu.model.TimeAvailablePerHourOutput;
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
	
	//Returns a map of campaign names and their generated campaign Ids or -1 in case of error.
	public List<BBCampaignOut> saveBillboardsAndDependants(BBCampaignsIn billboardsWithActivateValue)
	{
		List<BBCampaignOut> campaignOutList = new ArrayList<>();		
		List<String> errantCampaignNames = new ArrayList<>();
		
		int activate = billboardsWithActivateValue.getActivate();  //0 or 1. Tells if the camp needs to be activated or not.
		
		List<BBCampaignIn> campaignList = billboardsWithActivateValue.getBillboardList();
		
		for(BBCampaignIn campaign : campaignList)
		{
			int status = 1;  //Reset for each campaign. Change status to -1 only in case of an error during creation of the campaign or its schedule.
			
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
		    			//Call setter on specified property (setDps1()) passing the value to set
		    			pd.getWriteMethod().invoke(campaign, list.get(i));
	        		}
			    } 
	    		catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
	    	}
			
	    	int campaignId = quuDAO.saveBillboardAndRDSFields(campaign);  //The campaign is created deactive.
			
			if(campaignId != -1)
			{
				//Copy the image to our imageserver
				if(imageName != null)
    	    	{
    	    		final String imageNameF = imageName;
    	    		
    	    		new Thread(() -> saveImageOnImageserver(imageUrl, campaignId, imageNameF)).start();
    	    	}
				
				if(campaign.getScheduleList() != null)
				{
					status = createBillboardSchedules(new BBSchedulesIn(campaignId, campaign.getScheduleList()), false);
				}
			}
			else
			{
				status = -1;
			}
			
			//If the BB and its schedules were created successfully, status will be 1. 
			if(status == 1)
			{	
				int active = 0;
				
				if(activate == 1)
				{
					active = quuDAO.activateBillboard(campaignId);  //status says successful activation or not. If the camp is incomplete, the P will not activate it.
				}
				
				campaignOutList.add(new BBCampaignOut(campaignId, campaign.getName(), active));
			}
			//If there was an error in creating the campaign or any of its schedules or any schedule had times in wrong format then deactivate the campaign.
			else
			{
				quuDAO.deactivateBillboard(campaignId);
				
				errantCampaignNames.add(campaign.getName());
				campaignOutList.add(new BBCampaignOut(campaignId, campaign.getName(), 0));
			}
		}
			
		//If any campaign was activated or deactivated, clear the cache. Ordering of campaigns is set in the SP that activates the camp.
		//Exception: if activate was 1 but there was an error in creating the camp or its schedules then we will not activate so then the cache need not be cleared.
		if(activate == 1 || !errantCampaignNames.isEmpty())  //if(activate == 1 && (errantCampaignNames.isEmpty() || errantCampaignNames.size() < campaignList.size()))
		{
			new Thread(() -> {
				
				Util.clearQuuRDSCache();				
				//quuDAO.orderActiveRTCampaigns();
								
			}).start();
		}
		
		return campaignOutList;
	}
	
	//We only add schedules, not delete or edit existing ones.
	public int createBillboardSchedules(BBSchedulesIn schedules, boolean takeStatusAction)
	{
		int status = 1;  //Status changes to -1 if there is an error during creation of ANY schedule.
		
		int campaignId = schedules.getCampaignID();
				
		for(BBScheduleIn schedule : schedules.getScheduleList())
		{
			//If the times are not in mm:ss format then don't create the schedule. Activating a campaign with such a schedule will make GetJ2GCampaignData() throw error and bring the entire system down.
			if(!(schedule.getStart_time().matches("^[0-9]{2}:[0-9]{2}$") && schedule.getEnd_time().matches("^[0-9]{2}:[0-9]{2}$")))
			{
				status = -1;
			}
			else
			{
				int scheduleId = quuDAO.createBillboardSchedule(campaignId, schedule);
				
				//If there was an error in creating the schedule
				if(scheduleId == -1)
				{
					status = -1;
				}				
			}
		}
		
		//If it goes in the below block then we are adding schedules to an existing campaign which may or may not be active.
		if(takeStatusAction)
		{
			if(status == 1)
			{
				status = quuDAO.activateBillboard(campaignId);  //successful activation or not								
			}
			//If there was an error in creating any of the schedules or any schedule had times in wrong format then deactivate the campaign.
			else
			{
				quuDAO.deactivateBillboard(campaignId);
			}
			
			new Thread(() -> {
				
				Util.clearQuuRDSCache();
				//quuDAO.orderActiveRTCampaigns();
				
			}).start();
		}
		
		return status;
	}
	
	public int activateBillboard(int id)
	{
		int status = quuDAO.activateBillboard(id);
		
		if(status == 1)
		{
			new Thread(() -> Util.clearQuuRDSCache()).start();
		}
		
		return status;
	}
	
	public int deactivateBillboard(int id)
	{
		int status = quuDAO.deactivateBillboard(id);
		
		if(status == 1)
		{
			new Thread(() -> Util.clearQuuRDSCache()).start();
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
	
	
	public List<EmergencyBlastHotkey> getEmergencyblastHotkeys(int stationId)
	{
		return quuDAO.getEmergencyblastHotkeys(stationId);
	}
	
	public int activateEmergencyblastHotkey(int id)
	{
		int status = quuDAO.activateEmergencyblastHotkey(id);
		
		if(status == 1)
		{
			new Thread(() -> Util.clearQuuRDSCache()).start();
		}
		
		return status;
	}
	
	
	public List<CartAssignment> getCartAssignmentHistory(int campaignId)
	{
		return quuDAO.getCartAssignmentHistory(campaignId);
	}
	
	
	//If the SID is invalid only then will the below method return null.
	public TimeAvailablePerHourOutput getTimeAvailablePerHour(String sid, String date)
	{
		Map<String, Station> stationSidMap = Scheduler.stationMaps.getStationSidMap();
		
		Station station = stationSidMap.get(sid);
		
		if(station != null)
		{					
			Map<Integer, TimeAvailablePerHour> hourMap = quuDAO.getTimeAvailablePerHour(station.getId(), date);
			
			List<TimeAvailablePerHour> hourArr = new ArrayList<>();
			
			//The map fetched above could be missing some hours of the day (if there was no data for that hour) but we want to include all hours in the response so we insert the missing hours below.
			//Loop over all hours of the day and insert the missing hours
			for(int hour=0; hour<=23; hour++)
			{
				TimeAvailablePerHour timeAvailable = hourMap.get(hour);
				
				if(timeAvailable != null)
				{
					hourArr.add(timeAvailable);
				}	
				//If there is no data for this hour then create an empty obj (having 0  durations) and insert it for that hour.
				else
				{
					hourArr.add(new TimeAvailablePerHour(hour));
				}
			}
			
			return new TimeAvailablePerHourOutput(sid, station.getCallLetters(), date, hourArr);
		}
		
		return null;
	}
}
