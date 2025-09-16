package com.quu.dao;

import java.util.List;
import java.util.Map;

import com.quu.model.BBCampaignIn;
import com.quu.model.BBScheduleIn;
import com.quu.model.CartAssignment;
import com.quu.model.EmergencyBlastHotkey;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;
import com.quu.model.TimeAvailablePerHour;

public interface IQuuDAO {

	public StationMaps getStations();
	
	public List<RTLog> getStationRTLogs(int stationId, String stationCurrentDate);
	
	
	public int saveBillboardAndRDSFields(BBCampaignIn campaign);
	
	public int createBillboardSchedule(int campaignId, BBScheduleIn schedule);
	
	public int deactivateBillboard(int campaignId);
	
	public int activateBillboard(int campaignId);
	
	public void orderActiveRTCampaigns();
	
	public void addStationNoAutomationSchedule(int campaignId);
	
	
	public List<EmergencyBlastHotkey> getEmergencyblastHotkeys(int stationId);
	
	public int activateEmergencyblastHotkey(int id);
	
	
	public List<CartAssignment> getCartAssignmentHistory(int campaignId);
	
	
	public Map<Integer, TimeAvailablePerHour> getTimeAvailablePerHour(int stationId, String date);
}
