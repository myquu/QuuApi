package com.quu.vcreative.dao;

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
import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignStation;
import com.quu.util.Constant;


//TBD: Add a way to return error messages to the controller.

@RequestScoped
public class CampaignDAO extends BaseDAO implements ICampaignDAO{
	
	/**
	 * Inserts a campaign.
	 * id is -1.
	 * @param campaign
	 * @return
	 */
	public int add(CampaignIn campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveOrderAndRDSCampaignVC(?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setString(1, campaign.getAdvertiserName());
        	st.setString(2, campaign.getOrderName());
        	st.setInt(3, -1);
        	st.setString(4, campaign.getName());
            st.setString(5, campaign.getStartDate());
            st.setString(6, campaign.getEndDate());
            st.setString(7, campaign.getLine1());
            st.setString(8, campaign.getLine2());
            st.setString(9, campaign.getImageUrl());
            st.setString(10, campaign.getEmail());
            st.registerOutParameter(11, Types.INTEGER);
            st.registerOutParameter(12, Types.INTEGER);
            
            st.executeUpdate();
	        
            return st.getInt(11); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO add " + ex.getMessage());
        }
        
    	return -1;
    }
	
	public int[] update(CampaignIn campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveOrderAndRDSCampaignVC(?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setString(1, campaign.getAdvertiserName());
        	st.setString(2, campaign.getOrderName());
        	st.setInt(3, campaign.getId());
        	st.setString(4, campaign.getName());
            st.setString(5, campaign.getStartDate());
            st.setString(6, campaign.getEndDate());
            st.setString(7, campaign.getLine1());
            st.setString(8, campaign.getLine2());
            st.setString(9, campaign.getImageUrl());
            st.setString(10, campaign.getEmail());
            st.registerOutParameter(11, Types.INTEGER);  //This is the updated row count
            st.registerOutParameter(12, Types.INTEGER);
            
            st.executeUpdate();
	        
            return new int[] {st.getInt(11), st.getInt(12)}; 
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO update " + ex.getMessage());
        }
        
    	return null;
    }
	
	public void assignStations(int id, String station_ids)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignStationsToCampaignVC(?,?)");
			)
        {
        	st.setInt(1, id);
        	st.setString(2, station_ids);
        	
            st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO assignStations " + ex.getMessage());
        }
	}
	
	public int deactivate(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeactivateOrderAndRDSCampaignVC(?)");
			)
        {
        	st.setInt(1, id);
        	        	
            return st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO delete " + ex.getMessage());
        }
		
		return -1;
	}
	
	public void saveTraffic(int id, int station_id, String carts)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignStationsToCampaignVC(?,?)");
			)
        {
        	st.setInt(1, id);
        	st.setInt(2, station_id);
        	st.setString(3, carts);
        	
            st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO assignStations " + ex.getMessage());
        }
	}
}
