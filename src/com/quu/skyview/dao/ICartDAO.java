package com.quu.skyview.dao;

import java.util.List;

import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.StationCart;


public interface ICartDAO {

	public List<StationCart> getSchedules(int stationId);
	
	//public int saveStationCart(int stationId, String cart, String startDate, String endDate);
	public int saveStationCartDate(int stationId, String cart, String date);
	
	public int updateStationCartDate(int stationId, String cart, String date);
	
	public int saveStationCartSegment(int stationCartId, int order, int campaignId, int duration);
	    
}
