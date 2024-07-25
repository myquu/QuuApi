package com.quu.skyview.service;

import java.util.List;

import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Schedule;
import com.quu.skyview.model.StationCart;


public interface ICartService {

	public List<StationCart> getSchedules(int station_id);
	
	public int assignStationCartDates(StationCart stationCart);
    
	public int updateStationCartDate(StationCart stationCart);
	
}
