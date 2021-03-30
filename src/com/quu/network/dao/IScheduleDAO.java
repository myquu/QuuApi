package com.quu.network.dao;

import java.util.List;

import org.json.JSONObject;

import com.quu.network.model.Schedule;


public interface IScheduleDAO {

	public List<Schedule> getAll();
	
	public Schedule get(int id);
	
	public int add(String jsonS, int StationID, String CampaignIDs);

    public int update(int id, String jsonS, int StationID, String CampaignIDs);

    public int delete(int id);
	
}
