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
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO saveOrder " + ex.getMessage());
        }
        
    	return -1;
    }
	
	public int[] saveLineItem(int itemId, LineItemIn lineItem)
    {
    	try(
    			Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call SaveRDSCampaignVC(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			)
        {
    		st.setInt(1, itemId);
    		st.setString(2, lineItem.getVC_LineItem_ID());
    		st.setString(3, lineItem.getName());
            st.setString(4, lineItem.getStartDate());
            st.setString(5, lineItem.getEndDate());
            st.setString(6, lineItem.getLine1());
            st.setString(7, lineItem.getLine2());
            st.setString(8, lineItem.getDps1());
            st.setString(9, lineItem.getDps2());
            st.setString(10, lineItem.getDps3());
            st.setString(11, lineItem.getDps4());
            st.setString(12, lineItem.getDps5());
            st.setString(13, lineItem.getDps6());
            st.setString(14, lineItem.getDps7());
            st.setString(15, lineItem.getDps8());
            st.setString(16, lineItem.getImageName());
            st.setString(17, lineItem.getEmail());
            
            st.registerOutParameter(18, Types.INTEGER);
            st.setInt(18, lineItem.getId() != 0 ? lineItem.getId() : -1);
            
            st.registerOutParameter(19, Types.INTEGER);
            
            st.executeUpdate();
	        
            return new int[] {st.getInt(18), st.getInt(19)}; 
		}
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO saveLineItem " + ex.getMessage());
        }
        
    	return null;
    }
	
	public int deactivate(String POID, int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeactivateOrderAndRDSCampaignVC(?,?,?)");
			)
        {
			st.setString(1, POID);
        	st.setInt(2, id);
        	st.registerOutParameter(3, Types.INTEGER);  //1 - exists. 0 - Does not exist
        	
            st.executeUpdate();
            
            return st.getInt(3);
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO deactivate " + ex.getMessage());
        }
		
		return -1;
	}
	
	//Checks if the POID maps to an Order that was created via VC API call.
	public int campaignExists(String POID, int id)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			PreparedStatement st = conn.prepareStatement("select 1 from vcreative.PO_item_mapping m " +
    					"join qb_campaigns_new c on(m.item_id = c.item_id) where PO_id = ? and c.id = ?");
			)
        {
        	st.setString(1, POID);
        	st.setInt(2, id);
        	
        	try(ResultSet rs = st.executeQuery();)
	        {
		        if(rs.next())
		            return 1;
	        }
	    }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO campaignExists " + ex.getMessage());
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
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO assignStations poid=" + POID + "  id=" + id + "  " + ex.getMessage());
        }
		
		return null;
	}
	
	public void assignStationCarts(int advertiserId, int itemId, int id, String startDate, String endDate, int stationId, String VC_contractno, String carts)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call AssignStationCartsToCampaignVC(?,?,?,?,?,?,?,?)");
			)
        {
			st.setInt(1, advertiserId);
        	st.setInt(2, itemId);
        	st.setInt(3, id);
        	st.setString(4, startDate);
        	st.setString(5, endDate);
        	st.setInt(6, stationId);
        	st.setString(7, VC_contractno);
        	st.setString(8, carts);
        	
            st.executeUpdate();
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO assignStationCarts " + ex.getMessage());
        }
			
	}
	
	public void deleteStationsCarts(String POID, int id, int stationId, String carts)
	{
		try(
				Connection conn = getBusinessDBConnection();
    			CallableStatement st = conn.prepareCall("call DeleteStationsCartsFromCampaignVC(?,?,?,?)");
			)
        {
			st.setString(1, POID);
			st.setInt(2, id);
        	st.setInt(3, stationId);
        	st.setString(4, carts);
        	
            st.executeUpdate();
        }
        catch(SQLException ex)
        {
        	System.out.println(new java.util.Date() + "VCreative:CampaignDAO deleteStationsCarts " + ex.getMessage());
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
