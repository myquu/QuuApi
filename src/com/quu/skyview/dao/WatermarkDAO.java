package com.quu.skyview.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

import com.quu.dao.BaseDAO;
import com.quu.skyview.model.Campaign;
import com.quu.util.Constant;

@RequestScoped
public class WatermarkDAO extends BaseDAO implements IWatermarkDAO{

	public int assign(int campaignId, String watermarkIds) 
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignWatermarkIdsToCampaignSV(?,?,?)");
			)
        {
    		st.setInt(1, campaignId);
        	st.setString(2, watermarkIds);
            
            st.registerOutParameter(3, Types.INTEGER);
            
            st.executeUpdate();
	        
            return st.getInt(3); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:WatermarkDAO assign " + ex.getMessage());
        }
		
		return -1;
	}
	
	public int unassign(int campaignId, String watermarkIds)
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call UnassignWatermarkIdsFromCampaignSV(?,?,?)");
			)
        {
    		st.setInt(1, campaignId);
        	st.setString(2, watermarkIds);
            
            st.registerOutParameter(3, Types.INTEGER);
            
            st.executeUpdate();
	        
            return st.getInt(3); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:WatermarkDAO unassign " + ex.getMessage());
        }
		
		return -1;
	}
	
	public List<String> audit(String campaignId)
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("select watermark_id from qb_network_campaigns_watermarks where campaign_id = ?");
			)
        {
    		st.setString(1, campaignId);
        	            
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<String> list = new ArrayList<>();
		        	
		            do {
		            	list.add(rs.getString(1)); 
		            }while(rs.next());
		            
		            return list;
		        }
    		}
	    }
		catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:WatermarkDAO audit " + ex.getMessage());
        }
		
		return null;
	}
	
}
