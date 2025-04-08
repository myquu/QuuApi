package com.quu.dao;

import java.util.List;
import java.util.Map;

import com.quu.model.BBCampaign;
import com.quu.model.BBSchedule;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;

public interface IQuuDAO {

	public StationMaps getStations();
	
	public List<RTLog> getStationRTLogs(int stationId, String stationCurrentDate);
	
	public int createBillboardAndRDSFields(BBCampaign campaign);
	
	public int createBillboardSchedule(int campaignId, BBSchedule schedule);
	
	public int deactivateBillboard(int campaignId);
	
	public int activateBillboard(int campaignId);
	
	public void orderActiveRTCampaigns();
}
