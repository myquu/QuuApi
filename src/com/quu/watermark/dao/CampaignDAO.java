package com.quu.watermark.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.enterprise.context.RequestScoped;

import com.quu.dao.BaseDAO;
import com.quu.watermark.model.CampaignIn;
import com.quu.watermark.model.CampaignQId;

//TBD: Add a way to return error messages to the controller.

@RequestScoped
public class CampaignDAO extends BaseDAO implements ICampaignDAO{
	
	public int[] saveCampaign(CampaignIn campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveNetworkCampaignQID(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setString(1, campaign.getName());
            st.setString(2, campaign.getStartDate());
            st.setString(3, campaign.getEndDate());
            st.setString(4, campaign.getLine1());
            st.setString(5, campaign.getLine2());
            st.setString(6, campaign.getDps1());
            st.setString(7, campaign.getDps2());
            st.setString(8, campaign.getDps3());
            st.setString(9, campaign.getDps4());
            st.setString(10, campaign.getDps5());
            st.setString(11, campaign.getDps6());
            st.setString(12, campaign.getDps7());
            st.setString(13, campaign.getDps8());
            st.setString(14, campaign.getImageName());
                        
            st.registerOutParameter(15, Types.INTEGER);
            st.setInt(15, campaign.getId() != 0 ? campaign.getId() : -1);
            
            st.registerOutParameter(16, Types.INTEGER);
                        
            st.executeUpdate();
	        
            return new int[] {st.getInt(15), st.getInt(16)}; 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "Watermark:CampaignDAO saveCampaign " + ex.getMessage());
        }
        
    	return null;
    }
	
	public int assignQIds(int campaignId, String watermark_ids)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignWatermarkIdsToCampaignQID(?,?,?)");
			)
        {
			st.setInt(1, campaignId);
			st.setString(2, watermark_ids);
        	st.registerOutParameter(3, Types.INTEGER);  //1 - exists. 0 - Does not exist
        	
            st.executeUpdate();
            
            return st.getInt(3);
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "Watermark:CampaignDAO assignQIds " + ex.getMessage());
        }
		
		return -1;
	}
	
	public int unassignQIds(int campaignId, String watermark_ids)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call UnassignWatermarkIdsFromCampaignQID(?,?,?)");
			)
        {
			st.setInt(1, campaignId);
			st.setString(2, watermark_ids);
        	st.registerOutParameter(3, Types.INTEGER);  //1 - exists. 0 - Does not exist
        	
            st.executeUpdate();
            
            return st.getInt(3);
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "Watermark:CampaignDAO unassignQIds " + ex.getMessage());
        }
		
		return -1;
	}
	
	public int deactivate(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeactivateRDSCampaignQID(?,?)");
			)
        {
			st.setInt(1, id);
        	st.registerOutParameter(2, Types.INTEGER);  //1 - exists. 0 - Does not exist
        	
            st.executeUpdate();
            
            return st.getInt(2);
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "Watermark:CampaignDAO deactivate " + ex.getMessage());
        }
		
		return -1;
	}
		
}
