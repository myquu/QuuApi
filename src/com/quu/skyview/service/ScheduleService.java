package com.quu.skyview.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quu.skyview.dao.IScheduleDAO;
import com.quu.skyview.model.Advertisement;
import com.quu.skyview.model.Break;
import com.quu.skyview.model.Event;
import com.quu.skyview.model.Schedule;
import com.quu.util.Util;


@RequestScoped
public class ScheduleService implements IScheduleService{

	@Inject
    private IScheduleDAO scheduleDAO;
		
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Override
    public Schedule get(int station_id) {
        
        return scheduleDAO.get(station_id);
    }
    
    /**
	 * This method: 
	 * 1. Adds the schedule for a station in Skyview tables (event, break, advertisements). This is for the benefit of Sky. Meaningless to us.
	 * 2. Assigns the station to the campaigns to complete them then activate them. Meaningful to us.
	 * In the DB, id (represented below as _id) is the auto generated PK column. eventId, breakId etc hold values from the POST.
	 * @param schedule
	 * @return
	 */
	@Override
	public int add(Schedule schedule) {
    	
		Set<String> campaignIdSet = new HashSet<>();  //CampaignIDs could repeat. Set drops duplicates but does not keep insertion order (not a prob).
    	
    	for(Event event : schedule.getEventList())
    	{
    		int event_id = scheduleDAO.saveEvent(schedule.getStationId(), event);
			
			if(event_id != -1)
			{
				for(Break break1 : event.getBreakList())
	    		{
	    			int break_id = scheduleDAO.saveBreak(event_id, break1);
					
	    			if(break_id != -1)
	    			{
						for(Advertisement advertisement : break1.getAdvertisementList())
		    			{
		    				int advertisement_id = scheduleDAO.saveAdvertisement(break_id, advertisement);  
	    					
		    				if(advertisement_id != -1)
		    				{
			    				//advertisement.getCampaignId() is the line item id.
			    				campaignIdSet.add(String.valueOf(advertisement.getCampaignId()));
		    				}
		    			}
	    			}
	    		}
			}
    	}
		
    	
    	int ret = 0;
    	
		//Assign the station to the campaigns to complete them then activate them.
		if(!campaignIdSet.isEmpty())
        {
			//CampaignIDs will either be Quu line items ids or 0. Since 0 is not a line item id we remove it. 0s represent blank out periods.
			campaignIdSet.remove("0");
			
        	String campaignIds = String.join(",", campaignIdSet);
        	
        	ret =  scheduleDAO.assignStationToNetworkCampaigns(schedule.getStationId(), campaignIds);
        }
		
		
		if(ret == 1)
    	{
			new Thread(() -> Util.clearQuuRDSCache()).start();
	    }
		
        return ret;
    }

	/**
	 * Only 1 Event can be updated at a time. Update has 2 steps:
	 * 1. Delete the passed station from all campaigns in that event.
	 * 2. Assign the passed station to the campaigns in the passed event.
	 * 3. Delete the event (and its related records) from the sky schedule tables and add it back. We assume the passed event will be different from the existing one but even if its not it doesn't matter.
	 */
    @Override
    public int update(Schedule schedule) {
        
    	//Extract the event (the first one) to delete and add back.
    	Event event = schedule.getEventList().get(0);
    	
    	int ret = scheduleDAO.deleteStationFromNetworkCampaigns(schedule.getStationId(), event.getEventId());
    	
    	if(ret == 1)
    	{
    		//Guard against adding multiple events by passing only the first one.
    		List<Event> eventList = new ArrayList<>();
    		eventList.add(event);
    		schedule.setEventList(eventList);
    		
    		ret = add(schedule);
    	}
    	    	
    	return ret;
    }
    
    /**
     * This method deletes the passed station from
		1. All network campaigns created for the Skyview network in b2b DB. These will be the campaigns contained within the passed event.
		2. All schedule related tables in skyview DB in the hierarchy of the passed event.
     * station_id is the Quu station id but eventId is Sky's event id.
     */
    @Override
    public int deleteEventCascade(int station_id, int eventId) {
        
    	int ret = scheduleDAO.deleteStationFromNetworkCampaigns(station_id, eventId); 
    	
    	if(ret == 1)
    	{
    		new Thread(() -> Util.clearQuuRDSCache()).start();
    	}
    	
    	return ret;
    }
       
}
