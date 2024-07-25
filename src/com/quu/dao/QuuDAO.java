package com.quu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationInput;
import com.quu.model.StationMaps;


@ApplicationScoped
public class QuuDAO extends BaseDAO implements IQuuDAO{

	public StationMaps getStations()
    {
    	try(
				Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("SELECT rs.id, rs.sid, rs.call_letters, rs.package, rs.hd, rss.name, rsg.name, rsg.code, tz.Name "
						+ "FROM qb_radio_stations rs "
						+ "JOIN qb_radio_station_subgroups rss on(rs.radio_station_subgroup_id = rss.id) "
						+ "JOIN qb_radio_station_groups rsg on(rss.radio_station_group_id = rsg.id) "
						+ "JOIN mysql.time_zone_name tz ON(rs.mysql_time_zone_id = tz.Time_zone_id)");
			)
		{
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	Map<Integer, Station> stationIdMap = new HashMap<>();
		        	Map<String, Station> sidMap = new HashMap<>(),
	        			callLettersMap = new HashMap<>();
		        			        	
		            do {
		            	int id = rs.getInt(1);
		            	String sid = rs.getString(2),
	            			callLetters = rs.getString(3);
		            	
		            	//Unique station per row
		            	stationIdMap.put(id, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
		            	
		            	sidMap.put(sid, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
		            	
		            	callLettersMap.put(callLetters, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
		            }while(rs.next());
		            
		            return new StationMaps(stationIdMap, sidMap, callLettersMap);
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "NetworkApp:QuuDAO getStations " + ex.getMessage());
		}
		
    	return null;
    }
	
	public List<RTLog> getStationRTLogs(int stationId, String stationCurrentDate)
    {
    	try(
				Connection conn = getReportsDBConnection();
    			PreparedStatement st = conn.prepareStatement("select distinct DATE_FORMAT(l.timestamp, '%Y-%m-%d %H:%i:%s') `timestamp`," +
                        "l.`rt1`, l.`rt2`, l.`cart_no`, l.`raw_artist`, l.`raw_title`, l.`raw_category` "+
                        "from jg_log l "+
                        "where l.radio_station_id = ? "+
                        "and date(l.timestamp) = ? "+
                        "order by 1 desc limit 3");
			)
		{
    		st.setInt(1, stationId);
	        st.setString(2, stationCurrentDate);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<RTLog> detailList = new ArrayList<>();
		        	
		        	do {
		        		
		            	detailList.add(new RTLog(rs.getString(1), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
		            		rs.getString(2) + " " + rs.getString(3)));
		                   	
		            }while(rs.next());
		            
		            return detailList;
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "NetworkApp:QuuDAO getStationRTLogs " + ex.getMessage());
		}
		
    	return null;
    }
	
}
