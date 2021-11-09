package com.quu.skyview.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.quu.model.Station;
import com.quu.skyview.model.Advertisement;
import com.quu.skyview.model.Break;
import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Event;
import com.quu.skyview.model.Schedule;

public interface IScheduleService {

	public Schedule get(int station_id);	
	
    public int add(Schedule schedule);

    public int update(Schedule schedule);
    
    public int deleteEventCascade(int station_id, int eventId);
        
}
