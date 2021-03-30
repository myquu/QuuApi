package com.quu.network.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.quu.model.Station;
import com.quu.network.model.Campaign;
import com.quu.network.model.Schedule;

public interface IScheduleService {

	public List<Schedule> getAll();

	public Schedule get(int id);	
	
    public int add(String jsonS, int StationID, String CampaignIDs);

    public int update(int id, String jsonS, int StationID, String CampaignIDs);
    
    public int delete(int id);
	
}
