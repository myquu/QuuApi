package com.quu.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.quu.model.BBCampaignIn;
import com.quu.model.BBScheduleIn;
import com.quu.model.CartAssignment;
import com.quu.model.EmergencyBlastHotkey;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationInput;
import com.quu.model.StationMaps;
import com.quu.model.TimeAvailablePerHour;


@ApplicationScoped
public class QuuDAO extends BaseDAO implements IQuuDAO{

	public StationMaps getStations()
    {
		/* This needs a cart key in jg_log table. See if it can be pulled from the reports DB. Discuss if we still need to insert log data in 2 databases.
		 SELECT rs.id, rs.sid, rs.call_letters, rs.package, rs.hd, rs.block_automation, rs.no_scrub_cart_no, rss.name, rsg.name, rsg.code, tz.Name, DATE_FORMAT(l.lastActive, '%Y-%m-%d %H:%i:%s')
FROM qb_radio_stations rs 
JOIN qb_radio_station_subgroups rss on(rs.radio_station_subgroup_id = rss.id) 
JOIN qb_radio_station_groups rsg on(rss.radio_station_group_id = rsg.id) 
JOIN mysql.time_zone_name tz ON(rs.mysql_time_zone_id = tz.Time_zone_id) 
LEFT JOIN ( 
	select radio_station_id, max(timestamp) lastActive from quu_reports.jg_log 
	where cart_no not regexp '^999(8|9)'
    group by radio_station_id
) l
ON(rs.id = l.radio_station_id)
WHERE rs.status = 1; 
		 */
    	try(
				Connection conn = getBusinessDBConnection();
				/*PreparedStatement st = conn.prepareStatement("SELECT rs.id, rs.sid, rs.call_letters, rs.package, rs.hd, rs.block_automation, rs.no_scrub_cart_no, rss.name, rsg.name, rsg.code, tz.Name, DATE_FORMAT(l.lastActive, '%Y-%m-%d %H:%i:%s') "
						+ "FROM qb_radio_stations rs  "
						+ "JOIN qb_radio_station_subgroups rss on(rs.radio_station_subgroup_id = rss.id)  "
						+ "JOIN qb_radio_station_groups rsg on(rss.radio_station_group_id = rsg.id)  "
						+ "JOIN mysql.time_zone_name tz ON(rs.mysql_time_zone_id = tz.Time_zone_id)  "
						+ "LEFT JOIN (  "
						+ "	select radio_station_id, max(timestamp) lastActive from quu_reports.jg_log  "
						+ "	where cart_no not regexp '^999(8|9)' "
						+ "    group by radio_station_id "
						+ ") l "
						+ "ON(rs.id = l.radio_station_id) "
						+ "WHERE rs.status = 1");*/
    			PreparedStatement st = conn.prepareStatement("SELECT rs.id, rs.sid, rs.call_letters, rs.package, rs.hd, rs.block_automation, rs.no_scrub_cart_no, rss.name, rsg.name, rsg.code, tz.Name "
						+ "FROM qb_radio_stations rs "
						+ "JOIN qb_radio_station_subgroups rss on(rs.radio_station_subgroup_id = rss.id) "
						+ "JOIN qb_radio_station_groups rsg on(rss.radio_station_group_id = rsg.id) "
						+ "JOIN mysql.time_zone_name tz ON(rs.mysql_time_zone_id = tz.Time_zone_id) "
						+ "WHERE rs.status = 1");
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
		            	stationIdMap.put(id, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11)));
		            	
		            	sidMap.put(sid, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11)));
		            	
		            	callLettersMap.put(callLetters, new Station(id, sid, callLetters, rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11)));
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
	
	//The campaign is created deactive.
	public int saveBillboardAndRDSFields(BBCampaignIn campaign)
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
            st.setInt(19, campaign.getId() != 0 ? campaign.getId() : -1);
                      
            st.executeUpdate();
	        
            return st.getInt(19); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO createBillboardAndRDSFields " + ex.getMessage());
        }
        
    	return -1;
    }
	
	
	public int createBillboardSchedule(int campaignId, BBScheduleIn schedule)
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
            st.setInt(10, schedule.getOverride());
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
	
	//Activate and add to station order table. This SP is different from the Quu2Go SP in that it does not do the conflict check. It returns -1 if the campaign is incomplete or there is an error.
	public int activateBillboard(int campaignId)
    {
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call ActivateBillboard(?,?,?,?)");
			)
        {
			st.setInt(1, 2423);
    		st.setInt(2, campaignId);
    		    		                        
    		st.registerOutParameter(3, Types.INTEGER);
            st.registerOutParameter(4, Types.INTEGER);
            
            st.executeUpdate();
	        
            return st.getInt(4); 
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO activateBillboard " + ex.getMessage());
        }
        
    	return -1;
    }
		
	//The SP deletes from the order table and deletes any automation block schedule generated from this campaign.
	public int deactivateBillboard(int campaignId)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			//PreparedStatement st = conn.prepareStatement("update qrt_campaigns set is_active = 0 where id = ?");
    			CallableStatement st = conn.prepareCall("call qrt_sproc_deactivate_campaign(?)");
			)
        {
    		st.setInt(1, campaignId);
    		st.executeUpdate();
    		
    		return 1;
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO deactivateBillboard " + ex.getMessage());
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
	
	
	public List<EmergencyBlastHotkey> getEmergencyblastHotkeys(int stationId)
    {
    	try(
				Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("SELECT distinct c.id, c.name, c.type, 'Misc' `category`, c.minutes, c.duration, c.rt1, c.rt2, c.logo, "
						+ "group_concat(distinct o.option_id), group_concat(distinct rs.call_letters) "
						+ "from qrt_eb_campaigns c "
						+ "join qrt_eb_options o on(c.id = o.campaign_id) "
						+ "join qrt_eb_stations s on(c.id = s.campaign_id) "
						+ "join qb_radio_stations rs on(s.radio_station_id = rs.id) "
						+ "where rs.id = ? "
    					+ "group by 1,2,3,4,5,6,7,8,9 "
    					+ "order by 1 desc limit 10");
			)
		{
    		st.setInt(1, stationId);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<EmergencyBlastHotkey> list = new ArrayList<>();
		        			        	
		            do {
		            	list.add(new EmergencyBlastHotkey(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getString(8), rs.getString(9),
		            			Arrays.asList(rs.getString(10).split(",")), Arrays.asList(rs.getString(11).split(","))));
		            }while(rs.next());
		            
		            return list;
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "NetworkApp:QuuDAO getEmergencyblastHotkeys " + ex.getMessage());
		}
		
    	return null;
    }
	
	public int activateEmergencyblastHotkey(int id)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call ActivateEmergencyBlastHotkeyCampaign(?)");
			)
        {
    		st.setInt(1, id);
    		st.registerOutParameter(2, Types.INTEGER);
    		
    		st.executeUpdate();
    		
    		return st.getInt(2);
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:QuuDAO activateEmergencyblastHotkey " + ex.getMessage());
        }
        
    	return -1;
    }
	
	
	//Get the history of cart assignments for this campaign from the latest to the oldest.
	public List<CartAssignment> getCartAssignmentHistory(int campaignId)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("select h.timestamp, c.id, rs.sid, rs.call_letters, h.carts, h.user_id " +
															"from qb_campaigns_adsync_edit_history h " +
    														"join qb_campaigns_new c on(h.campaign_id = c.id) " +
															"join qb_campaigns_times_new t on(c.id = t.campaign_id) " +
    														"join qb_campaigns_stations s on(t.id = s.time_id) " +
															"join qb_radio_stations rs on(s.radio_station_id = rs.id) " +
															"where h.campaign_id = ? " +
															"order by 1 desc, 3");
			)
		{
    		st.setInt(1, campaignId);
	            		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<CartAssignment> detailList = new ArrayList<>();
		        	
		        	do {
		        		
		            	detailList.add(new CartAssignment(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
		                   	
		            }while(rs.next());
		            
		            return detailList;
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "NetworkApp:QuuDAO getCartAssignmentHistory " + ex.getMessage());
		}
		
    	return null;
    }
	
	
	public Map<Integer, TimeAvailablePerHour> getTimeAvailablePerHour(int stationId, String date)
    {
    	try(
    			Connection conn = getReportsDBConnection();
    			PreparedStatement st = conn.prepareStatement("select during, HOUR(timestamp), ROUND(sum(duration_actual)/60, 1) " +
															"from jg_log " +
    														"where radio_station_id = ? and date(timestamp) = ? and during in(2,3) " +
															"and type not in(1,11,12,13, 0, 5) " +  // and NOT(during = 1 and raw_category <> 'INT')...the INT check is needed because the interleaved campaign can be a BB of any type though its mostly 1 or 11
    														"group by 1,2 " +
															"order by 1,2");
			)
		{
    		st.setInt(1, stationId);
    		st.setString(2, date);
	            		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	Map<Integer, TimeAvailablePerHour> hourMap = new HashMap<>();
		        			        	
		        	do {
		        		
		        		int during = rs.getInt(1),  //Can only be Ad(3) or Other(2)
	        				hour = rs.getInt(2);
		        		
		        		TimeAvailablePerHour timeAvailable = null;
		        		
		            	//If it enters "if" it will be the other duration for the hour (because of the order by clause)
		        		if(!hourMap.containsKey(hour))
		            	{
		            		timeAvailable = new TimeAvailablePerHour(hour);		        		
		            	}
		        		//If it enters "else" it will be the ads duration for the hour
		            	else
		            	{
		            		timeAvailable = hourMap.get(hour);
		            	}
		            	
		            	if(during == 2)
	            			timeAvailable.setOther_duration(rs.getFloat(3));  //This is set the first time 
	            		else
	            			timeAvailable.setAds_duration(rs.getFloat(3));  //This is set when the next record is read
		        		
	            		hourMap.put(hour, timeAvailable);  //This step is redundant for the else block on #412 but we do it for the benefit of brevity.
		                   	
		            }while(rs.next());
		            
		            return hourMap;
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "NetworkApp:QuuDAO getCartAssignmentHistory " + ex.getMessage());
		}
		
    	return null;
    }
}
