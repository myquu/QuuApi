package com.quu.network.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

import org.json.JSONObject;

import com.quu.dao.BaseDAO;
import com.quu.model.Station;
import com.quu.network.model.Advertisement;
import com.quu.network.model.Break;
import com.quu.network.model.Campaign;
import com.quu.network.model.Event;
import com.quu.network.model.Schedule;


@RequestScoped
public class ScheduleDAO extends BaseDAO implements IScheduleDAO{

	private static final int ITEM_ID = 9111;
	private static final int USER_ID = 1197;
	
	public List<Schedule> getAll()
    {
    	try(
				Connection conn = getNetworkDBConnection();
				PreparedStatement st = conn.prepareStatement("select id, value from schedules");
			)
		{
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<Schedule> list = new ArrayList<>();
		        	
		            do
		            	list.add(new Schedule(rs.getInt(1), rs.getString(2))); 
		            while(rs.next());
		            
		            return list;
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO getAll " + ex.getMessage());
		}
		
    	return null;
    }
	
	public Schedule get(int id)
    {
    	try(
    			Connection conn = getNetworkDBConnection();
				PreparedStatement st = conn.prepareStatement("select value from schedules where id = ?");
			)
		{
    		st.setInt(1, id);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	return new Schedule(id, rs.getString(1)); 
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO get " + ex.getMessage());
		}
		
    	return null;
    }
	
	
	/*
	public int save(Schedule schedule)
    {
		try(
    			Connection conn = getSkyviewDBConnection();
    			//PreparedStatement st = conn.prepareStatement("insert into schedule(reportingId, length) values(?,?)", Statement.RETURN_GENERATED_KEYS);
			)
        {
    		for(Event event : schedule.getEventList())
	    	{
    			int eventId = saveEvent(event);
				
				PreparedStatement st = conn.prepareStatement("insert into schedule(call_letters, eventId) values(?,?)");
				st.setString(1, schedule.getCallLetters());
				st.setInt(2, eventId);
	                	                        
	            st.executeUpdate();
    			st.close();
    			    			
	    		for(Break break1 : event.getBreakList())
	    		{
	    			int breakId = saveBreak(break1);
    				
    				st = conn.prepareStatement("insert into event_breaks(eventId, breakId) values(?,?)");
    				st.setInt(1, eventId);
    				st.setInt(2, breakId);
    	                	                        
    	            st.executeUpdate();
	    			st.close();
    	            
	    			for(Advertisement advertisement : break1.getAdvertisementList())
	    			{
	    				int advertisementId = saveAdvertisement(advertisement);
	    					    				
	    				st = conn.prepareStatement("insert into break_advertisements(breakId, advertisementId) values(?,?)");
	    				st.setInt(1, breakId);
	    	            st.setInt(2, advertisementId);
	    	                        
	    	            st.executeUpdate();
	    	            st.close();
	    		    }
	    				    			
	    		}
	    	}
    	}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO save " + ex.getMessage());
        }
		
		
    	return -1;
    }
	*/
	
	public int add(String jsonS, int StationID, String CampaignIDs)
    {
    	try(
    			Connection conn = getNetworkDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveSchedule(?,?,?,?,?,?,?)");
			)
        {
    		st.setInt(1, -1);
    		st.setString(2, jsonS);
    		st.setInt(3, ITEM_ID);
    		st.setString(4, CampaignIDs);
    		st.setInt(5, StationID);
    		st.setInt(6, USER_ID);
    		st.registerOutParameter(7, Types.INTEGER);
    		
            st.executeUpdate();
	        
            return st.getInt(7); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:ScheduleDAO add " + ex.getMessage());
        }
        
    	return -1;
    }
	
	public int update(int id, String jsonS, int StationID, String CampaignIDs)
    {
    	try(
    			Connection conn = getNetworkDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveSchedule(?,?,?,?,?,?,?)");
			)
        {
    		st.setInt(1, id);
    		st.setString(2, jsonS);
    		st.setInt(3, ITEM_ID);
    		st.setString(4, CampaignIDs);
    		st.setInt(5, StationID);
    		st.setInt(6, USER_ID);
    		st.registerOutParameter(7, Types.INTEGER);
    		
            st.executeUpdate();
	        
            return st.getInt(7);
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:ScheduleDAO update " + ex.getMessage());
        }
        
    	return -1;
    }
	
	public int delete(int id)
	{
		try(
				Connection conn = getNetworkDBConnection();
				CallableStatement st = conn.prepareCall("delete from schedules where id = ?");
			)
        {
        	st.setInt(1, id);
                                    
        	return st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO delete " + ex.getMessage());
        }
		
		return -1;
	}
		
}
