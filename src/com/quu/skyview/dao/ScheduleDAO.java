package com.quu.skyview.dao;

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
import com.quu.skyview.model.Advertisement;
import com.quu.skyview.model.Break;
import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Event;
import com.quu.skyview.model.Schedule;


@RequestScoped
public class ScheduleDAO extends BaseDAO implements IScheduleDAO{

	public Schedule get(int station_id)
    {
    	try(
    			Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("select e.id event_id, e.eventId, e.campaignId, e.date, "
						+ "b.id break_id, b.breakId, b.time, "
						+ "a.id advertisement_id, a.campaign_id, a.length "
						+ "from skyview.event e "
						+ "join skyview.break b on(e.id = b.event_id) "
						+ "join skyview.advertisement a on(b.id = a.break_id) "
						+ "where e.station_id = ? "
						+ "order by 1,5");
			)
		{
    		st.setInt(1, station_id);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<Event> eventList = new ArrayList<>();
		        	
		        	do {
		        		
		        		Event event = new Event(rs.getInt(2), rs.getInt(3), rs.getString(4), null, rs.getInt(1));
		        		Break break1 = new Break(rs.getInt(6), rs.getString(7), null,  rs.getInt(5));		        		
		        		Advertisement advertisement = new Advertisement(rs.getInt(9), rs.getInt(10), rs.getInt(8));
		        		
		        		if(!eventList.contains(event))
		        		{
		        			List<Advertisement> advertisementList = new ArrayList<>();
			        		advertisementList.add(advertisement);
			        		
			        		List<Break> breakList = new ArrayList<>();
			        		break1.setAdvertisementList(advertisementList);
			        		breakList.add(break1);
			        		
			        		event.setBreakList(breakList);
			        		eventList.add(event);
		        		}
		        		else
		        		{
		        			int indexE = eventList.indexOf(event);
		        			event = eventList.get(indexE);
		        			
		        			List<Break> breakList = event.getBreakList();
		        			
		        			//If the breakList doesn't contain this break
		        			if(!breakList.contains(break1))
		        			{
			        			List<Advertisement> advertisementList = new ArrayList<>();
			        			advertisementList.add(advertisement);
			        			
			        			break1.setAdvertisementList(advertisementList);
				        		breakList.add(break1);
			        		}
			        		else
			        		{
			        			int indexB = breakList.indexOf(break1);
			        			break1 = breakList.get(indexB);
			        				
			        			List<Advertisement> advertisementList = break1.getAdvertisementList();
			        			advertisementList.add(advertisement);
			        		}
		        		}
		        		
		        	}while(rs.next());
		        	
		        	return new Schedule(station_id, eventList);
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO get " + ex.getMessage());
		}
		
    	return null;
    }
	
	public int deleteStationFromNetworkCampaigns(int station_id, int eventId)
	{
		try(
				Connection conn = getBusinessDBConnection();
				CallableStatement st = conn.prepareCall("call DeleteStationFromNetworkCampaignsSV(?,?)");
			)
        {
			st.setInt(1, station_id);
        	st.setInt(2, eventId);
        	
        	st.executeUpdate();
        	
        	return 1;
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO deleteStationFromNetworkCampaigns " + ex.getMessage());
        }
		
		return -1;
	}
		
	public int assignStationToNetworkCampaigns(int stationId, String campaignIds)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignStationToNetworkCampaignsSV(?,?)");
			)
        {
    		st.setInt(1, stationId);
    		st.setString(2, campaignIds);
    		    		    		
    		st.executeUpdate();
    		
    		return 1;
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO assignStationToNetworkCampaigns " + ex.getMessage());
        }
    	
    	return -1;
    }
	
	
	public int saveEvent(int station_id, Event event)
	{
		try(
				Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("insert into event(station_id, eventId, campaignId, date) values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			)
        {
			st.setInt(1, station_id);
        	st.setInt(2, event.getEventId());
        	st.setInt(3, event.getCampaignId());
        	st.setString(4, event.getDate());
                                    
        	st.executeUpdate();
        	
        	try(ResultSet rs = st.getGeneratedKeys();)
    		{
		        if(rs.next())
		        {
		        	return rs.getInt(1); 
		        }
    		}
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO saveEvent " + ex.getMessage());
        }
		
		return -1;
	}
	
	
	//The below insert data in Skyview tables.
	
	public int saveBreak(int event_id, Break break1)
	{
		try(
				Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("insert into break(event_id, breakId, time) values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			)
        {
			st.setInt(1, event_id);
			st.setInt(2, break1.getBreakId());
        	st.setString(3, break1.getTime());
                                    
        	st.executeUpdate();
        	
        	try(ResultSet rs = st.getGeneratedKeys();)
    		{
		        if(rs.next())
		        {
		        	return rs.getInt(1); 
		        }
    		}
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO saveBreak " + ex.getMessage());
        }
		
		return -1;
	}
	
	public int saveAdvertisement(int break_id, Advertisement advertisement)
	{
		try(
				Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("insert into advertisement(break_id, campaign_id, length) values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			)
        {
			st.setInt(1, break_id);
        	st.setInt(2, advertisement.getCampaignId());
        	st.setInt(3, advertisement.getLength());
        	                                    
        	st.executeUpdate();
        	
        	/* This generated id is of no use
        	try(ResultSet rs = st.getGeneratedKeys();)
    		{
		        if(rs.next())
		        {
		        	return rs.getInt(1); 
		        }
    		}
    		*/
        	
        	return 1;
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO saveAdvertisement " + ex.getMessage());
        }
		
		return -1;
	}
	
}
