package com.quu.service;

import java.util.List;
import java.util.Map;

import com.quu.model.BBCampaign;
import com.quu.model.BBSchedules;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;

public interface IQuuService {

	public StationMaps getStations();
	
	public List<RTLog> getStationRTLogs(String sid);
	
	public List<String> createBillboardsAndDependants(List<BBCampaign> campaignList);
	
	public int createBillboardSchedules(BBSchedules schedules, boolean takeStatusAction); 
}
