package com.quu.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.quu.model.BBCampaign;
import com.quu.model.BBSchedule;
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
				PreparedStatement st = conn.prepareStatement("SELECT rs.id, rs.sid, rs.call_letters, rs.package, rs.hd, rs.block_automation, rss.name, rsg.name, rsg.code, tz.Name "
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
		            	stationIdMap.put(id, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)));
		            	
		            	sidMap.put(sid, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)));
		            	
		            	callLettersMap.put(callLetters, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)));
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
	
	public int createBillboardAndRDSFields(BBCampaign campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call CreateBillboardAndRDSFields(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		//Basic campaign
    		st.setString(1, campaign.getName());
    		st.setString(2, campaign.getAdvertiser());  //st.setNull(2, Types.VARCHAR);
    		st.setString(3, campaign.getMessage_type());
    		st.setString(4, campaign.getStart_date());
            st.setString(5, campaign.getEnd_date());
            st.setString(6, campaign.getRemarks());
            st.setInt(7, 2423);  //Hard coded the id of quu_ingest@myquu.com
            //RDS fields (no image here)
            st.setString(8, campaign.getRt1());
            st.setString(9, campaign.getRt2());
            st.setString(10, campaign.getDps1());
            st.setString(11, campaign.getDps2());
            st.setString(12, campaign.getDps3());
            st.setString(13, campaign.getDps4());
            st.setString(14, campaign.getDps5());
            st.setString(15, campaign.getDps6());
            st.setString(16, campaign.getDps7());
            st.setString(17, campaign.getDps8());
            st.setString(18, campaign.getImageName());
            
            st.registerOutParameter(19, Types.INTEGER);
            
            st.executeUpdate();
	        
            return st.getInt(19); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO createBillboardAndRDSFields " + ex.getMessage());
        }
        
    	return -1;
    }
	
	
	public int createBillboardSchedule(int campaignId, BBSchedule schedule)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call qrt_sproc_save_time_and_dependants_NG1(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		//Basic campaign
    		st.setInt(1, campaignId);
    		st.setNull(2, Types.INTEGER);  //This is how we set an int column to null
    		st.setString(3, schedule.getName());
    		st.setString(4, schedule.getStart_time());
            st.setString(5, schedule.getEnd_time());
            st.setInt(6, schedule.getDuration());
            st.setString(7, String.join(",", schedule.getDay_ids()));
            st.setString(8, String.join(",", schedule.getRadio_station_ids()));
            st.setString(9, String.join(",", schedule.getOption_ids()));
            st.setInt(10, 0);
            st.setNull(11, Types.VARCHAR);
            st.setInt(12, schedule.getShow_logo_mus());
            st.setNull(13, Types.VARCHAR);
            st.setInt(14, 0);
            st.setInt(15, 0);
            st.setInt(16, schedule.getBlock_automation());
            st.setNull(17, Types.VARCHAR);
                                    
            st.registerOutParameter(18, Types.VARCHAR);
            st.registerOutParameter(19, Types.INTEGER);
            st.registerOutParameter(20, Types.INTEGER);
            
            st.executeUpdate();
	        
            return st.getInt(20); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO createBillboardSchedules " + ex.getMessage());
        }
        
    	return -1;
    }
	
	public int deactivateBillboard(int campaignId)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("update qrt_campaigns set is_active = 0 where id = ?");
			)
        {
    		st.setInt(1, campaignId);
    		st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO deactivateBillboard " + ex.getMessage());
        }
        
    	return -1;
    }
	
	public int activateBillboard(int campaignId)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("update qrt_campaigns set is_active = 1, indelible = 1 where id = ?");
			)
        {
    		st.setInt(1, campaignId);
    		st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO activateBillboard " + ex.getMessage());
        }
        
    	return -1;
    }
	
	//This calls a "fix" SP that runs over all active campaigns in the database and adds them to the ordering table if not already there.
	public void orderActiveRTCampaigns()
	{
		try(
    			Connection conn = getBusinessDBConnection();
				CallableStatement st = conn.prepareCall("call fix_OrderActiveRTCampaigns()");
			)
        {
    		st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO orderActiveRTCampaigns " + ex.getMessage());
        }
	}
	
	public void addStationNoAutomationSchedule(int campaignId)
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call CreateBlockAutomationScheduleFromBillboard(?)");
			)
        {
    		st.setInt(1, campaignId);
    		                        
            st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO addStationNoAutomationSchedule " + ex.getMessage());
        }
	}
	
}
