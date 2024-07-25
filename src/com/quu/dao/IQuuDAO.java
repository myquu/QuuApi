package com.quu.dao;

import java.util.List;
import java.util.Map;

import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;

public interface IQuuDAO {

	public StationMaps getStations();
	
	public List<RTLog> getStationRTLogs(int stationId, String stationCurrentDate);
	
}
