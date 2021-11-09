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


@ApplicationScoped
public class QuuDAO extends BaseDAO implements IQuuDAO{

	public Map<String, Station> getStations()
    {
    	try(
				Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("SELECT rs.id, rs.call_letters, tz.Name "
						+ "FROM qb_radio_stations rs "
						+ "JOIN mysql.time_zone_name tz ON(rs.mysql_time_zone_id = tz.Time_zone_id)");
			)
		{
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	Map<String, Station> stationMap = new HashMap<>();
		        	
		            do {
		            	String callLetters = rs.getString(2);
		            	
		            	//Unique station per row
		            	stationMap.put(callLetters, new Station(rs.getInt(1), callLetters, rs.getString(3)));
		            }while(rs.next());
		            
		            return stationMap;
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
