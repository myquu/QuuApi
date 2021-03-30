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
				PreparedStatement st = conn.prepareCall("call GetNetworkStaticData()");
			)
		{
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	Map<String, Station> stationMap = new HashMap<>();
		        	
		            do {
		            	String callLetters = rs.getString(2);
		            	StationInput input = new StationInput(rs.getString(5), rs.getString(6), rs.getInt(7));
		            	
		            	Station station = stationMap.get(callLetters);
		            	
		            	//Add a new station and one of its inputs 
		            	if(station == null)
		            	{
			            	List<StationInput> inputList = new ArrayList<>();
			            	inputList.add(input);
			            	stationMap.put(callLetters, new Station(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), inputList));
		            	}
		            	//Add additional input to existing station
		            	else
		            	{
		            		station.getInputList().add(input);
		            	}
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
