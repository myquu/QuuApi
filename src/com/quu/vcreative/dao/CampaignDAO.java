package com.quu.vcreative.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.enterprise.context.RequestScoped;

import com.quu.dao.BaseDAO;
import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignStationDetail;
import com.quu.vcreative.model.ImageIn;
import com.quu.vcreative.model.LineItemIn;


//TBD: Add a way to return error messages to the controller.

@RequestScoped
public class CampaignDAO extends BaseDAO implements ICampaignDAO{
	
	public int saveOrder(CampaignIn campaign)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveOrderVC(?,?,?,?)");
			)
        {
    		st.setString(1, campaign.getAdvertiserName());
        	st.setString(2, campaign.getOrderName());
        	st.setString(3, campaign.getVC_POID());
        	st.registerOutParameter(4, Types.INTEGER);
                        
            st.executeUpdate();
	        
            return st.getInt(4); 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO addOrder " + ex.getMessage());
        }
        
    	return -1;
    }
	
	public int[] saveLineItem(int itemId, LineItemIn lineItem)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveRDSCampaignVC(?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setInt(1, itemId);
        	st.setString(2, lineItem.getName());
            st.setString(3, lineItem.getStartDate());
            st.setString(4, lineItem.getEndDate());
            st.setString(5, lineItem.getLine1());
            st.setString(6, lineItem.getLine2());
            st.setString(7, lineItem.getImageName());
            st.setString(8, lineItem.getEmail());
            
            st.registerOutParameter(9, Types.INTEGER);
            st.setInt(9, lineItem.getId() != 0 ? lineItem.getId() : -1);
            
            st.registerOutParameter(10, Types.INTEGER);
            
            st.executeUpdate();
	        
            return new int[] {st.getInt(9), st.getInt(10)}; 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO addLineItem " + ex.getMessage());
        }
        
    	return null;
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
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO deactivate " + ex.getMessage());
        }
		
		return -1;
	}
	
	//NOT IN USE
	//Checks if the id belongs to a campaign that was created via VC API call.
	public int campaignExists(int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("select 1 from qb_campaigns_new where id = ? and source = 'VC'");
			)
        {
        	st.setInt(1, id);
        	        	
        	try(ResultSet rs = st.executeQuery();)
	        {
		        if(rs.next())
		            return 1;
	        }
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO delete " + ex.getMessage());
        }
		
		return -1;
	}
	
	public int[] assignImage(ImageIn imageIn)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignImageToCampaignVC(?,?,?,?,?)");
			)
        {
    		st.setString(1, imageIn.getVC_POID());
    		st.setInt(2, imageIn.getId());
        	st.setString(3, imageIn.getImageName());
            st.registerOutParameter(4, Types.INTEGER);  //1 - exists. 0 - Does not exist
            st.registerOutParameter(5, Types.INTEGER);
            
            st.executeUpdate();
	        
            return new int[] {st.getInt(4), st.getInt(5)}; 
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO assignImage " + ex.getMessage());
        }
        
    	return null;
    }
	
	public CampaignStationDetail assignStations(String POID, int id, String station_ids)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignStationsToCampaignVC(?,?,?,?,?,?,?,?)");
			)
        {
			st.setString(1, POID);
        	st.setInt(2, id);
        	st.setString(3, station_ids);
        	st.registerOutParameter(4, Types.INTEGER);  //1 - exists. 0 - Does not exist
        	st.registerOutParameter(5, Types.INTEGER);  //out_advertiser_id
        	st.registerOutParameter(6, Types.INTEGER);  //out_item_id
        	st.registerOutParameter(7, Types.VARCHAR);  //start date
        	st.registerOutParameter(8, Types.VARCHAR);  //end date
        	
            st.executeUpdate();
            
            return new CampaignStationDetail(st.getInt(4), st.getInt(5), st.getInt(6), st.getString(7), st.getString(8));
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO assignStations " + ex.getMessage());
        }
		
		return null;
	}
	
	public void assignStationCarts(int advertiserId, int itemId, int id, String startDate, String endDate, int stationId, String carts)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignStationCartsToCampaignVC(?,?,?,?,?,?,?)");
			)
        {
			st.setInt(1, advertiserId);
        	st.setInt(2, itemId);
        	st.setInt(3, id);
        	st.setString(4, startDate);
        	st.setString(5, endDate);
        	st.setInt(6, stationId);
        	st.setString(7, carts);
        	
            st.executeUpdate();
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO assignStationCarts " + ex.getMessage());
        }
			
	}
	
	//NOT IN USE
	public void saveTraffic(int id, int station_id, String carts)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveTrafficDataVC(?,?,?)");
			)
        {
        	st.setInt(1, id);
        	st.setInt(2, station_id);
        	st.setString(3, carts);
        	        	
            st.executeUpdate();
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO SaveTrafficDataVC " + ex.getMessage());
        }
	}
}
