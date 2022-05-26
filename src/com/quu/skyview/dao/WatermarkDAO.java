package com.quu.skyview.dao;

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

import javax.enterprise.context.RequestScoped;

import com.quu.dao.BaseDAO;
import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Watermark;
import com.quu.util.Constant;

@RequestScoped
public class WatermarkDAO extends BaseDAO implements IWatermarkDAO{

	public void assign(int campaignId, String watermarkId, int duration) 
	{
		try(
    			Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("insert into qb_network_campaigns_watermarks(campaign_id, watermark_id, duration) values(?,?,?)");
			)
        {
    		st.setInt(1, campaignId);
        	st.setString(2, watermarkId);
        	st.setInt(3, duration);
        	
            st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:WatermarkDAO assign " + ex.getMessage());
        }
	}
	
	public void unassign(int campaignId, String watermarkIds)
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call UnassignWatermarkIdsFromCampaignSV(?,?)");
			)
        {
    		st.setInt(1, campaignId);
        	st.setString(2, watermarkIds);
            
            st.executeUpdate();
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:WatermarkDAO unassign " + ex.getMessage());
        }
	}
	
	public Map<Integer, List<Watermark>> audit()
	{
		try(
    			Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("select distinct campaign_id, watermark_id, duration from qb_network_campaigns_watermarks order by 1");
			)
        {
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	Map<Integer, List<Watermark>> map = new HashMap<>();
		        	
		            do {
		            	int campaignId = rs.getInt(1);
		            	
		            	List<Watermark> list = map.get(campaignId);
		            	
		            	if(list == null)
		            	{
		            		list = new ArrayList<>();
		            		list.add(new Watermark(rs.getString(2), rs.getInt(3)));
		            		
		            		map.put(campaignId, list);
		            	}
		            	else
		            	{
		            		list.add(new Watermark(rs.getString(2), rs.getInt(3)));
		            	}
		            }while(rs.next());
		            
		            return map;
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
