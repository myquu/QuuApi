package com.quu.skyview.service;

import java.util.HashMap;
import java.util.List;

import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Schedule;
import com.quu.skyview.model.Segment;
import com.quu.skyview.model.StationCart;


public interface ICartService {

	public HashMap<String, List<Segment>> getCartSchedules(int stationId, String date);
	
	public int assignStationCartDates(StationCart stationCart);
    
	public int updateStationCartDate(StationCart stationCart);
	
}
