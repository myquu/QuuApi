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
	
	private static final int SKY_NETWORK_ID = 2;
	
	public List<Campaign> getAll()
    {
    	try(
				Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("select c.id, c.name, r.rt1, r.rt2, r.logo, r.dps1, r.dps2, r.dps3, r.dps4, r.dps5, r.dps6, r.dps7, r.dps8 "
						+ "from qb_network_campaigns c "
						+ "join qb_network_campaign_rds r on(c.id = r.campaign_id) "
						+ "where c.network_id = ? order by c.id desc");
			)
		{
    		st.setInt(1, SKY_NETWORK_ID);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<Campaign> list = new ArrayList<>();
		        	
		            do {
		            	int id = rs.getInt(1);
		            	String logo = rs.getString(5);
		            	
		            	if(logo != null)
		            	{
		            		logo = Constant.IMAGES_DIR_URL + "/networkcampaign_images/" + id + "/logo/" + logo;
		            	}
		            	
		            	list.add(new Campaign(id, rs.getString(2), rs.getString(3), rs.getString(4), null, logo,
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
	
	public Campaign get(int id)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("select c.id, c.name, r.rt1, r.rt2, r.logo, r.dps1, r.dps2, r.dps3, r.dps4, r.dps5, r.dps6, r.dps7, r.dps8 "
						+ "from qb_network_campaigns c "
						+ "join qb_network_campaign_rds r on(c.id = r.campaign_id) "
						+ "where c.network_id = ? and c.id = ? order by c.id desc");
			)
		{
    		st.setInt(1, SKY_NETWORK_ID);
    		st.setInt(2, id);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	String logo = rs.getString(5);
		        	
		        	if(logo != null)
	            	{
	            		logo = Constant.IMAGES_DIR_URL + "/networkcampaign_images/" + id + "/logo/" + logo;
	            	}
		        	
		        	return new Campaign(id, rs.getString(2), rs.getString(3), rs.getString(4), null, logo,
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
	
	//Checks if campaign is a Sky (network) campaign.
	public int campaignExists(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("select 1 from qb_network_campaigns where network_id = ? and id = ?");
			)
        {
			st.setInt(1, SKY_NETWORK_ID);
        	st.setInt(2, id);
        	        	
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
	public int[] save(Campaign campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveNetworkCampaignSV(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setInt(1, SKY_NETWORK_ID);
    		st.setString(2, campaign.getName());
        	st.setString(3, campaign.getLine1());
            st.setString(4, campaign.getLine2());
            st.setString(5, campaign.getDps1());
            st.setString(6, campaign.getDps2());
            st.setString(7, campaign.getDps3());
            st.setString(8, campaign.getDps4());
            st.setString(9, campaign.getDps5());
            st.setString(10, campaign.getDps6());
            st.setString(11, campaign.getDps7());
            st.setString(12, campaign.getDps8());
            st.setString(13, campaign.getImageName());
            
            st.registerOutParameter(14, Types.INTEGER);
            st.setInt(14, campaign.getId());
            
            st.registerOutParameter(15, Types.INTEGER);
            
            st.executeUpdate();
	        
            return new int[] {st.getInt(14), st.getInt(15)}; 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO save " + ex.getMessage());
        }
        
    	return null;
    }
	
	/**
     * Returns 1 - updated rows (OK), -1 - Nothing got updated, -2 - DB error.
     */
	public int deactivate(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeactivateNetworkCampaignSV(?,?)");
			)
        {
        	st.setInt(1, id);
        	st.registerOutParameter(2, Types.INTEGER);
        	
            st.executeUpdate();
            
            return st.getInt(2);
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO deactivate " + ex.getMessage());
        }
		
		return -2;
	}
	
	/**
     * Returns 1 - delete successful (OK), -1 - Nothing got deleted, -2 - DB error.
     */
	public int delete(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeleteNetworkCampaignSV(?,?)");
			)
        {
        	st.setInt(1, id);
        	st.registerOutParameter(2, Types.INTEGER);
        	
            st.executeUpdate();
            
            return st.getInt(2);
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "SkyviewApp:CampaignDAO delete " + ex.getMessage());
        }
		
		return -2;
	}
}
