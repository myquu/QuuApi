package com.quu.skyview.dao;

import java.util.HashMap;
import java.util.List;

import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Segment;
import com.quu.skyview.model.StationCart;


public interface ICartDAO {

	public HashMap<String, List<Segment>> getCartSchedules(int stationId, String date);
	
	//public int saveStationCart(int stationId, String cart, String startDate, String endDate);
	public int saveStationCartDate(int stationId, String cart, String date);
	
	public int updateStationCartDate(int stationId, String cart, String date);
	
	public int saveStationCartSegment(int stationCartId, int order, int duration, int campaignId, String reportingId);
	    
}
