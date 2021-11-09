package com.quu.skyview.dao;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.quu.skyview.model.Advertisement;
import com.quu.skyview.model.Break;
import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Event;
import com.quu.skyview.model.Schedule;


public interface IScheduleDAO {

	//public List<Schedule> getAll();
	
	public Schedule get(int station_id);
	
	public int saveEvent(int station_id, Event event);
	
	public int saveBreak(int event_id, Break break1);
	
	public int saveAdvertisement(int break_id, Advertisement advertisement);
	
	public int assignStationToNetworkCampaigns(int stationId, String campaignIds);
	
    public int deleteStationFromNetworkCampaigns(int station_id, int eventId);
	
    
    public List<Integer> getStationsForEventId(int eventId);
}
