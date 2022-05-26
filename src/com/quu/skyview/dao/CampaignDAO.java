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
import com.quu.skyview.model.Campaign;
import com.quu.util.Constant;


//TBD: Add a way to return error messages to the controller.

@RequestScoped
public class CampaignDAO extends BaseDAO implements ICampaignDAO{
	
	//Now the campaigns can be linked with both stations and watermarks.
	//private static final int SKY_NETWORK_ID = 2;
	
	//Returns all campaigns that were never activated or those that are currently active.
	public List<Campaign> getAll(String IMAGENAME)
    {
    	try(
				Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("select c.id, c.name, r.rt1, r.rt2, r.image_hash, r.dps1, r.dps2, r.dps3, r.dps4, r.dps5, r.dps6, r.dps7, r.dps8 "
						+ "from qb_network_campaigns c "
						+ "join qb_network_campaign_rds r on(c.id = r.campaign_id) "
						+ "where ((c.is_active = 0 and c.indelible = 0) or c.is_active = 1) order by c.id desc");
			)
		{
    		//st.setInt(1, SKY_NETWORK_ID);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<Campaign> list = new ArrayList<>();
		        	
		            do {
		            	int id = rs.getInt(1);
		            	String image_hash = rs.getString(5),
	            			imageUrl = null;
		            	
		            	//If image hash is present, we would have created the image on image server. Return its URL.
		            	if(image_hash != null)
		            	{
		            		imageUrl = Constant.IMAGES_DIR_URL + "/networkcampaign_images/" + id + "/logo/" + IMAGENAME;
		            	}
		            	
		            	list.add(new Campaign(id, rs.getString(2), rs.getString(3), rs.getString(4), null, image_hash, imageUrl, IMAGENAME,
		            			rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13))); 
		            }while(rs.next());
		            
		            return list;
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO getAll " + ex.getMessage());
		}
		
    	return null;
    }
	
	//Returns the campaign if it was never activated or if it is currently active.
	public Campaign get(int id, String IMAGENAME)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("select c.id, c.name, r.rt1, r.rt2, r.image_hash, r.dps1, r.dps2, r.dps3, r.dps4, r.dps5, r.dps6, r.dps7, r.dps8 "
						+ "from qb_network_campaigns c "
						+ "join qb_network_campaign_rds r on(c.id = r.campaign_id) "
						+ "where ((c.is_active = 0 and c.indelible = 0) or c.is_active = 1) and c.id = ?");
			)
		{
    		//st.setInt(1, SKY_NETWORK_ID);
    		st.setInt(1, id);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	String image_hash = rs.getString(5),
	        			imageUrl = null;
	            	
	            	//If image hash is present, we would have created the image on image server. Return its URL.
	            	if(image_hash != null)
	            	{
	            		imageUrl = Constant.IMAGES_DIR_URL + "/networkcampaign_images/" + id + "/logo/" + IMAGENAME;
	            	}
		        	
		        	return new Campaign(id, rs.getString(2), rs.getString(3), rs.getString(4), null, image_hash, imageUrl, IMAGENAME,
	            			rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13));
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO get " + ex.getMessage());
		}
		
    	return null;
    }
	
	/**
	 * Checks if the campaign is a Sky (network) campaign. 
	 * This called before add, update, delete, deactivate. For the benefit of "update" we do not put the is_active = 1 check.
	 */
	public int campaignExists(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("select 1 from qb_network_campaigns where id = ?");
			)
        {
			//st.setInt(1, SKY_NETWORK_ID);
        	st.setInt(1, id);
        	        	
        	try(ResultSet rs = st.executeQuery();)
	        {
		        if(rs.next())
		            return 1;
	        }
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO campaignExists " + ex.getMessage());
        }
		
		return -1;
	}
	
	/**
	 * Inserts/Updates a campaign.
	 * @param campaign
	 * @return Returns the id of the campaign and the active status.
	 */
	public int[] save(Campaign campaign, String IMAGENAME)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveNetworkCampaignSV(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		//st.setInt(1, SKY_NETWORK_ID);
    		st.setString(1, campaign.getName());
        	st.setString(2, campaign.getLine1());
            st.setString(3, campaign.getLine2());
            st.setString(4, null);
            st.setString(5, campaign.getDps1());
            st.setString(6, campaign.getDps2());
            st.setString(7, campaign.getDps3());
            st.setString(8, campaign.getDps4());
            st.setString(9, campaign.getDps5());
            st.setString(10, campaign.getDps6());
            st.setString(11, campaign.getDps7());
            st.setString(12, campaign.getDps8());
            st.setString(13, IMAGENAME);
            st.setString(14, campaign.getImageHash());
            
            st.registerOutParameter(15, Types.INTEGER);
            st.setInt(15, campaign.getId());
            
            st.registerOutParameter(16, Types.INTEGER);
            
            st.executeUpdate();
	        
            return new int[] {st.getInt(15), st.getInt(16)}; 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO save " + ex.getMessage());
        }
        
    	return null;
    }
	
	public void active(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("update qb_network_campaigns set is_active = 1, indelible = 1 where id = ?");
			)
        {
        	st.setInt(1, id);
        	       	
            st.executeUpdate();
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO active " + ex.getMessage());
        }
	}
	
	/**
	 * Deletes or deactivates.
     * Returns 1 - delete successful (OK), -1 - Nothing got deleted.
     */
	public void delete(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeleteNetworkCampaignSV(?)");
			)
        {
        	st.setInt(1, id);
        	       	
            st.executeUpdate();
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO delete " + ex.getMessage());
        }
	}
	
}
