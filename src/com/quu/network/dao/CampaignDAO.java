package com.quu.network.dao;

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
import com.quu.network.model.Campaign;
import com.quu.util.Constant;


//TBD: Add a way to return error messages to the controller.

@RequestScoped
public class CampaignDAO extends BaseDAO implements ICampaignDAO{
	
	private static final int ITEM_ID = 9111;
	
	public List<Campaign> getAll()
    {
    	try(
				Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("select c.id, s.id, c.name, r.rt1, r.rt2, r.logo from qb_campaigns_new c "
						+ "join qb_campaign_rds r on(c.id = r.campaign_id) "
						+ "join network.campaign_extra s on(c.id = s.campaign_id) "
						+ "where c.item_id = ? order by c.id desc");
			)
		{
    		st.setInt(1, ITEM_ID);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	List<Campaign> list = new ArrayList<>();
		        	
		            do {
		            	int id = rs.getInt(1);
		            	String logo = rs.getString(6);
		            	
		            	if(logo != null)
		            	{
		            		logo = Constant.IMAGES_DIR_URL + "/campaign_images/" + id + "/logo/" + logo;
		            	}
		            	
		            	list.add(new Campaign(id, rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), null, logo)); 
		            }while(rs.next());
		            
		            return list;
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "NetworkApp:CampaignDAO getAll " + ex.getMessage());
		}
		
    	return null;
    }
	
	public Campaign get(int id)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
				PreparedStatement st = conn.prepareStatement("select s.id, c.name, r.rt1, r.rt2, r.logo from qb_campaigns_new c "
						+ "join qb_campaign_rds r on(c.id = r.campaign_id) "
						+ "join network.campaign_extra s on(c.id = s.campaign_id) "
						+ "where c.item_id = ? and c.id = ?");
			)
		{
    		st.setInt(1, ITEM_ID);
    		st.setInt(2, id);
    		
    		try(ResultSet rs = st.executeQuery();)
    		{
		        if(rs.next())
		        {
		        	String logo = rs.getString(5);
		        	
		        	if(logo != null)
	            	{
	            		logo = Constant.IMAGES_DIR_URL + "/campaign_images/" + id + "/logo/" + logo;
	            	}
		        	
		        	return new Campaign(id, rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), null, logo); 
		        }
    		}
	    }
		catch (SQLException ex)
		{
			System.out.println(new java.util.Date() + "NetworkApp:CampaignDAO get " + ex.getMessage());
		}
		
    	return null;
    }
	
	/**
	 * Inserts a campaign.
	 * id is -1.
	 * @param campaign
	 * @return Returns the id of the new campaign.
	 */
	public int add(Campaign campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call CreateNetworkCampaign(?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setInt(1, -1);
    		st.setInt(2, ITEM_ID);
        	st.setInt(3, campaign.getNetworkId());
        	st.setString(4, campaign.getName());
        	st.setString(5, campaign.getLine1());
            st.setString(6, campaign.getLine2());
            st.setString(7, campaign.getImageName());
            st.registerOutParameter(8, Types.INTEGER);
            st.registerOutParameter(9, Types.INTEGER);
            
            st.executeUpdate();
	        
            return st.getInt(8); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:CampaignDAO add " + ex.getMessage());
        }
        
    	return -1;
    }
	
	/**
	 * Only the columns of qb_campaign_rds can be updated. 
	 * id is <> -1.
	 * @param campaign
	 * @return Returns the active status.
	 */
	public int[] update(Campaign campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call CreateNetworkCampaign(?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setInt(1, campaign.getId());
    		st.setInt(2, ITEM_ID);
        	st.setInt(3, campaign.getNetworkId());
        	st.setString(4, campaign.getName());
        	st.setString(5, campaign.getLine1());
            st.setString(6, campaign.getLine2());
            st.setString(7, campaign.getImageName());
            st.registerOutParameter(8, Types.INTEGER);  //This is the updated row count
            st.registerOutParameter(9, Types.INTEGER);
            
            st.executeUpdate();
            
            return new int[] {st.getInt(8), st.getInt(9)}; 
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:CampaignDAO update " + ex.getMessage());
        }
        
    	return null;
    }
	
	public int delete(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeleteNetworkCampaign(?,?)");
			)
        {
        	st.setInt(1, id);
        	st.setInt(2, 4);  //RDS type
        	
            return st.executeUpdate();
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "NetworkApp:CampaignDAO delete " + ex.getMessage());
        }
		
		return -1;
	}
}
