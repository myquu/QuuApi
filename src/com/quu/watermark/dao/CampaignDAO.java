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

//TBD: Add a way to return error messages to the controller.

@RequestScoped
public class CampaignDAO extends BaseDAO implements ICampaignDAO{
	
	public int[] saveCampaign(CampaignIn campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveRDSCampaignQID(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setString(1, campaign.getQid());
    		st.setString(2, campaign.getSource());
    		st.setString(3, campaign.getName());
            st.setString(4, campaign.getStartDate());
            st.setString(5, campaign.getEndDate());
            st.setString(6, campaign.getLine1());
            st.setString(7, campaign.getLine2());
            st.setString(8, campaign.getDps1());
            st.setString(9, campaign.getDps2());
            st.setString(10, campaign.getDps3());
            st.setString(11, campaign.getDps4());
            st.setString(12, campaign.getDps5());
            st.setString(13, campaign.getDps6());
            st.setString(14, campaign.getDps7());
            st.setString(15, campaign.getDps8());
            st.setString(16, campaign.getImageName());
            st.setString(17, campaign.getEmail());
            
            st.registerOutParameter(18, Types.INTEGER);
            st.setInt(18, campaign.getId() != 0 ? campaign.getId() : -1);
            
            st.registerOutParameter(19, Types.INTEGER);
                        
            st.executeUpdate();
	        
            return new int[] {st.getInt(18), st.getInt(19)}; 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO saveOrder " + ex.getMessage());
        }
        
    	return null;
    }
	
	public int deactivate(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeactivateOrderAndRDSCampaignVC(?,?,?)");
			)
        {
			st.setInt(1, id);
        	st.registerOutParameter(2, Types.INTEGER);  //1 - exists. 0 - Does not exist
        	
            st.executeUpdate();
            
            return st.getInt(3);
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO deactivate " + ex.getMessage());
        }
		
		return -1;
	}
		
}
