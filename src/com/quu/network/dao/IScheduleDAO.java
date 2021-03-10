package com.quu.network.dao;

import java.util.List;

import org.json.JSONObject;

import com.quu.network.model.Schedule;


public interface IScheduleDAO {

	public List<Schedule> getAll();
	
	public Schedule get(int id);
	
	public int add(Schedule schedule);

    public int update(Schedule schedule);

    public boolean delete(int id);
	
}
