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

import com.quu.dao.BaseDAO;
import com.quu.model.Station;
import com.quu.skyview.model.Advertisement;
import com.quu.skyview.model.Break;
import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Event;
import com.quu.skyview.model.Schedule;
import com.quu.skyview.model.Segment;
import com.quu.skyview.model.StationCart;
import com.quu.util.Constant;


//TBD: Add a way to return error messages to the controller.

@RequestScoped
public class CartDAO extends BaseDAO implements ICartDAO {
	
	@Override
	public List<StationCart> getSchedules(int stationId)
    {
    	try(
    			Connection conn = getSkyviewDBConnection();
				PreparedStatement st = conn.prepareStatement("select s.id, s.station_id, s.cart, s.date, ss.order, ss.duration, ss.campaign_id "
						+ "from ncc_station_cart_schedule s "
						+ "join ncc_station_cart_schedule_segments ss on(s.id = ss.station_cart_schedule_id) "
						+ "where s.station_id = ? and current_date = s.date "
						+ "order by 2,3,4 desc");
			)
		{
    		st.setInt(1, stationId);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<StationCart> stationCartList = new ArrayList<StationCart>();
		        	
		        	do {
		        		
		        		List<Segment> segmentList = new ArrayList<>();
		        		
		        		Segment segment = new Segment(rs.getInt(5), rs.getInt(6), rs.getInt(7), 0);
		        				        		
		        		segmentList.add(segment);
		        		
		        		
		        		StationCart stationCart = new StationCart(rs.getInt(2), rs.getString(3), rs.getString(4), null, segmentList, rs.getInt(1));  //This is used the first time when adding the unique station to the list. After that its used only to match the id in the list.
		        		
		        		int indexOfStationCart = stationCartList.indexOf(stationCart);
		        		
		        		//If the key exists in the list then this record contains another segment to be added to the station cart in cache.
		        		if(indexOfStationCart != -1)
		        		{
		        			stationCartList.get(indexOfStationCart).getSegmentList().add(segment);
		        		}
		        		//Key doesn't exist in the map
		        		else
		        		{
		        			stationCartList.add(stationCart);
		        		}
		        			        		
		        	}while(rs.next());
		        	
		        	return stationCartList;
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
	public int saveStationCart(int stationId, String cart, String startDate, String endDate)
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveStationCartForNetworkCartCampaign(?,?,?,?,?)");
			)
        {
    		st.setInt(1, stationId);
        	st.setString(2, cart);
        	st.setString(3, startDate);
        	st.setString(4, endDate);
        	
        	st.registerOutParameter(5, Types.INTEGER);
                    	
            st.executeUpdate();
	        
            return st.getInt(5); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO save " + ex.getMessage());
        	
        	return -1;
        }
	}
	*/
	
	//Returns - > 0 ID OR -1 meaning error.
	public int saveStationCartDate(int stationId, String cart, String date)
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveStationCartForNetworkCartCampaign(?,?,?,?,?)");
			)
        {
    		st.setInt(1, stationId);
        	st.setString(2, cart);
        	st.setString(3, date);
        	st.setInt(4, 1); //Create if doesn't exist
        	        	
        	st.registerOutParameter(5, Types.INTEGER);
                    	
            st.executeUpdate();
	        
            return st.getInt(5); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO save " + ex.getMessage());
        	
        	return -1;
        }
	}
	
	//Returns - > 0 ID OR -2 meaning NOT FOUND OR -1 meaning error.
	public int updateStationCartDate(int stationId, String cart, String date)
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveStationCartForNetworkCartCampaign(?,?,?,?,?)");
			)
        {
    		st.setInt(1, stationId);
        	st.setString(2, cart);
        	st.setString(3, date);
        	st.setInt(4, 0); //Do not create if doesn't exist
        	        	
        	st.registerOutParameter(5, Types.INTEGER);
                    	
            st.executeUpdate();
	        
            return st.getInt(5); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO save " + ex.getMessage());
        	
        	return -1;
        }
	}
	
	public int saveStationCartSegment(int stationCartId, int order, int campaignId, int duration)
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveStationCartSegmentsForNetworkCartCampaign(?,?,?,?)");
			)
        {
    		st.setInt(1, stationCartId);
        	st.setInt(2, order);
        	st.setInt(3, campaignId);
        	st.setInt(4, duration);
        	        	
            st.executeUpdate();
	        
            return 1; 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO save " + ex.getMessage());
        	
        	return -1;
        }
	}
}
