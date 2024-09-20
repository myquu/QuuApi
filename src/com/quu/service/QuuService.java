package com.quu.service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.quu.dao.IQuuDAO;
import com.quu.util.Constant;
import com.quu.model.BBCampaign;
import com.quu.model.BBSchedule;
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
	
	public int createBillboardsAndDependants(List<BBCampaign> campaignList)
	{
		int status = 1;  //Change status to -1 only in case of an error
		
		for(BBCampaign campaign : campaignList)
		{
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
			
			int id = quuDAO.createBillboardAndRDSFields(campaign);
			
			if(id != -1)
			{
				for(BBSchedule schedule : campaign.getScheduleList())
				{
					int scheduleId = quuDAO.createBillboardSchedules(id, schedule);
					
					if(scheduleId == -1)
					{
						status = -1;
					}
				}
			}
			else
			{
				status = -1;
			}
		}
		
		return status;
	}
}
