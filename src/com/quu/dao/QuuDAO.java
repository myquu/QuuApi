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

import com.quu.model.Station;
import com.quu.model.StationInput;
import com.quu.model.StationMaps;


@ApplicationScoped
public class QuuDAO extends BaseDAO implements IQuuDAO{

	public StationMaps getStations()
    {
    	try(
				Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("SELECT rs.id, rs.call_letters, rs.package, tz.Name, rsg.code "
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
		        	Map<String, Station> stationMap = new HashMap<>();
		        	Map<Integer, Station> stationIdMap = new HashMap<>();
		        	
		            do {
		            	int id = rs.getInt(1);
		            	String callLetters = rs.getString(2);
		            	
		            	//Unique station per row
		            	stationMap.put(callLetters, new Station(id, callLetters, rs.getInt(3), rs.getString(4), rs.getString(5)));
		            	
		            	stationIdMap.put(id, new Station(id, callLetters, rs.getInt(3), rs.getString(4), rs.getString(5)));
		            }while(rs.next());
		            
		            return new StationMaps(stationMap, stationIdMap);
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "NetworkApp:QuuDAO getStations " + ex.getMessage());
		}
		
    	return null;
    }
	
}
