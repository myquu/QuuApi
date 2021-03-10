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
	
    public int add(Schedule schedule);

    public int update(Schedule schedule);
    
    public boolean delete(int id);
	
}
