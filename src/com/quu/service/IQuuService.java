package com.quu.service;

import java.util.List;
import java.util.Map;

import com.quu.model.BBCampaignIn;
import com.quu.model.BBCampaignOut;
import com.quu.model.BBSchedulesIn;
import com.quu.model.BBCampaignsIn;
import com.quu.model.CartAssignment;
import com.quu.model.EmergencyBlastHotkey;
import com.quu.model.RTLog;
import com.quu.model.StationMaps;
import com.quu.model.TimeAvailablePerHour;
import com.quu.model.TimeAvailablePerHourOutput;

public interface IQuuService {

	public StationMaps getStations();
	
	public List<RTLog> getStationRTLogs(String sid);
	
	
	public List<BBCampaignOut> saveBillboardsAndDependants(BBCampaignsIn billboardsWithActivateValue);
	
	public int createBillboardSchedules(BBSchedulesIn schedules, boolean takeStatusAction);
	
	public int activateBillboard(int id);
	
	public int deactivateBillboard(int id);
	
	
	public List<EmergencyBlastHotkey> getEmergencyblastHotkeys(int stationId);
	
	public int activateEmergencyblastHotkey(int id);
	
	
	public List<CartAssignment> getCartAssignmentHistory(int campaignId);
	
	
	public TimeAvailablePerHourOutput getTimeAvailablePerHour(String sid, String date);
}
