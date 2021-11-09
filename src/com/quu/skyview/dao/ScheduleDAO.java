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

	@Override
	public Schedule get(int station_id)
    {
    	try(
    			Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("select e.id event_id, e.eventId, e.campaignId, "
						+ "b.id break_id, b.breakId, "
						+ "a.id advertisement_id, a.campaign_id, a.reportingId, a.length "
						+ "from event e "
						+ "join break b on(e.id = b.event_id) "
						+ "join advertisement a on(b.id = a.break_id) "
						+ "where e.station_id = ? "
						+ "order by 1,5,8");
			)
		{
    		st.setInt(1, station_id);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<Event> eventList = new ArrayList<>();
		        	
		        	do {
		        		
		        		Event event = new Event(rs.getInt(2), rs.getInt(3), null, rs.getInt(1));
		        		Break break1 = new Break(rs.getInt(5), null,  rs.getInt(4));		        		
		        		Advertisement advertisement = new Advertisement(rs.getInt(7), rs.getInt(9), rs.getString(8), rs.getInt(6));
		        		
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
	
	@Override
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
		
	@Override
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
	
	@Override
	public int saveEvent(int station_id, Event event)
	{
		try(
				Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("insert into event(station_id, eventId, campaignId) values(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			)
        {
			st.setInt(1, station_id);
        	st.setInt(2, event.getEventId());
        	st.setInt(3, event.getCampaignId());
        	                                    
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
	@Override
	public int saveBreak(int event_id, Break break1)
	{
		try(
				Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("insert into break(event_id, breakId) values(?,?)", Statement.RETURN_GENERATED_KEYS);
			)
        {
			st.setInt(1, event_id);
			st.setInt(2, break1.getBreakId());
        	                                    
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
	
	@Override
	public int saveAdvertisement(int break_id, Advertisement advertisement)
	{
		try(
				Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("insert into advertisement(break_id, campaign_id, length, reportingId) values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			)
        {
			st.setInt(1, break_id);
        	st.setInt(2, advertisement.getCampaignId());
        	st.setInt(3, advertisement.getLength());
        	st.setString(4, advertisement.getReportingId());                                    
        	
        	st.executeUpdate();
        	
        	//The generated id is of no use 
        	        	
        	return 1;
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO saveAdvertisement " + ex.getMessage());
        }
		
		return -1;
	}
	
	@Override
	public List<Integer> getStationsForEventId(int eventId)
	{
		try(
    			Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("select distinct station_id from event where eventId = ?");
			)
		{
    		st.setInt(1, eventId);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<Integer> stationIdList = new ArrayList<>();
		        	
		        	do {
		        		
		        		stationIdList.add(rs.getInt(1));
		        		
		        	}while(rs.next());
		        	
		        	return stationIdList;
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "SkyviewApp:ScheduleDAO getStationsForEventId " + ex.getMessage());
		}
		
    	return null;
	}
}
