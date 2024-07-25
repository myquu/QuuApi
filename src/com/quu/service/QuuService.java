package com.quu.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.quu.dao.IQuuDAO;
import com.quu.util.Constant;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;
import com.quu.util.Scheduler;
import com.quu.util.Util;


@ApplicationScoped
public class QuuService implements IQuuService{

	@Inject
    private IQuuDAO quuDAO;
	
	public StationMaps getStations()
	{
		return quuDAO.getStations();
	}
	
	public List<RTLog> getStationRTLogs(String sid)
	{
		Map<String, Station> stationSidMap = Scheduler.stationMaps.getStationSidMap();
		
		Station station = stationSidMap.get(sid);
		
		final ZonedDateTime gmtCurrentDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(System.currentTimeMillis()/1000), ZoneId.of("GMT")),
        		stationCurrentDateTime = Util.getDateDetail(gmtCurrentDateTime.toLocalDateTime(), "GMT", station.getTzName());
		
		return quuDAO.getStationRTLogs(station.getId(), stationCurrentDateTime.format(Constant.dateFormatter));
	}
}
