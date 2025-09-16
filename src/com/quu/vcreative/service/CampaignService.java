package com.quu.vcreative.service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;

import com.quu.vcreative.dao.ICampaignDAO;
import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignOut;
import com.quu.vcreative.model.CampaignStationDetail;
import com.quu.vcreative.model.CampaignStationIn;
import com.quu.vcreative.model.ImageIn;
import com.quu.vcreative.model.LineItemIn;
import com.quu.vcreative.model.LineItemOut;
import com.quu.vcreative.model.StationCart;
import com.quu.dao.IQuuDAO;
import com.quu.model.Station;
import com.quu.model.StationMaps;
import com.quu.util.Constant;
import com.quu.util.Scheduler;
import com.quu.util.Util;


@RequestScoped
public class CampaignService implements ICampaignService{

	//We tried to inject IQuuDAO but it didn't work maybe because of its Application scope.
	
	@Inject
    private ICampaignDAO campaignDAO;
		

    //Handles both add and edit of an Order and line items inside it. 
    @Override
    public CampaignOut save(CampaignIn campaignIn) {
        
    	CampaignOut campaignOut = new CampaignOut();
    	campaignOut.setVC_POID(campaignIn.getVC_POID());
    	
    	int itemId = campaignDAO.saveOrder(campaignIn);
    	
    	if(itemId != -1)
    	{
    		boolean anyActive = false;
    		
    		List<LineItemOut> lineItemOuts = new ArrayList<LineItemOut>(); 
    		
    		for(LineItemIn lineItemIn : campaignIn.getLineItems())
    		{
    			String VCImageUrl = lineItemIn.getImageUrl(),
					imageName = null;
    			
    			if(VCImageUrl != null)
	        	{
	        		imageName = VCImageUrl.substring(VCImageUrl.lastIndexOf("/")+1);
	        		lineItemIn.setImageName(imageName);
	        	}
    			
    			List<String> list = Util.setDPSFields(lineItemIn.getLine1(), lineItemIn.getLine2());
    			
    			if(list != null)
    	    	{
    	    		//Populate DPS fields using reflection
    	    		try 
    	    		{
    	    			for(int i=0; i<list.size(); i++)
    	        		{
    		    			PropertyDescriptor pd = new PropertyDescriptor("dps"+(i+1), lineItemIn.getClass());
    		    			// Call setter on specified property (setDps1())
    		    			pd.getWriteMethod().invoke(lineItemIn, list.get(i));
    	        		}
    			    } 
    	    		catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
    	    	}
    			
	    		int[] ret = campaignDAO.saveLineItem(itemId, lineItemIn);
	    		
	    		if(ret != null)
	    		{
	    			int lineItemId = ret[0],
        				active = ret[1];
	    			
	    			if(active == 1)
	    			{
	    				anyActive = true;
	    			}
	    			
	    			//Copy the image to our imageserver
	    			if(imageName != null)
	    	    	{
	    	    		final String imageNameF = imageName;
	    	    		
	    	    		new Thread(() -> saveImageOnImageserver(VCImageUrl, lineItemId, imageNameF)).start();
	    	    	}
	    			
	    			lineItemOuts.add(new LineItemOut(lineItemIn.getVC_LineItem_ID(), lineItemId, (Constant.BIZCAMPAIGNPREVIEWURL + lineItemId)));
		        }
    		}
    		
    		campaignOut.setLineItems(lineItemOuts);
    		
    		//If any line item is active clear the cache.
    		if(anyActive)
    		{
    			new Thread(() -> Util.clearQuuRDSCache()).start();
    		}
    	}
    	
    	return campaignOut;
    }

        
    //Updates the image field of a line item. 
    @Override
    public int assignImage(ImageIn imageIn) {
        
    	String VCImageUrl = imageIn.getImageUrl(),
			imageName = VCImageUrl.substring(VCImageUrl.lastIndexOf("/")+1);
    	
    	imageIn.setImageName(imageName);
    	    	
    	int[] ret = campaignDAO.assignImage(imageIn);
    	
    	int found = ret[0]; 
    	
    	//If line item belongs to a VC Order. 
    	if(found == 1)
    	{
	    	//active status
	    	if(ret[1] == 1)
	    	{
	    		new Thread(() -> Util.clearQuuRDSCache()).start();
	    	}
    		    	
    		final String imageNameF = imageName;
    		
    		new Thread(() -> saveImageOnImageserver(VCImageUrl, imageIn.getId(), imageNameF)).start();
	    }
    	    	
    	return found;
    }
    
    //This method assigns stations to the campaign and adds specific carts to each station.
    public String[] assignStationsCarts(CampaignStationIn campaignStation)
    {
    	String status = "0"; //Invalid PO id or line item id
    	
    	String station_ids = "",  //These are ids of assignable stations
			unpartneredStations = "", unlicensedStations = "";
    	
    	List<StationCart> partneredStationCartList = new ArrayList<>();  
		    		
    	Map<String, Station> stationMap = Scheduler.stationMaps.getStationCallLettersMap();
    	
    	for(StationCart stationCarts : campaignStation.getStationCartList()) //for each station
    	{
    		String callLetters = stationCarts.getStation();
    		callLetters = callLetters.toUpperCase().replaceFirst("-FM$", "");
    		
    		Station station = stationMap.get(callLetters);
    		
    		/*
    		//Assign only if its a partnered station with Advertiser(3) or Advertiser unlimited(4) package level. 
    		if(station != null && (station.getPackage1() == 3 || station.getPackage1() == 4))
    		{
    			station_ids += station.getId() + ",";
	    		
	    		partneredStationCartList.add(new StationCart(null, stationCarts.getCartList(), station.getId(), station.getGroupCode()));
	    		//campaignDAO.saveTraffic(campaignStation.getId(), station.getId(), String.join(",", cartList));
    		}
    		else
    		{
    			unpartneredStations += stationCarts.getStation() + ",";
    		}
    		*/
    		//Station is not in Quu
    		if(station == null)
    		{
    			unpartneredStations += stationCarts.getStation() + ",";
    		}
    		//Station is in Quu but does not have Adsync license
    		else if(!ArrayUtils.contains(new int[]{3,4}, station.getPackage1()))
    		{
    			unlicensedStations += stationCarts.getStation() + ",";
    		}
    		//Station is partnered and licensed for Adsync
    		else
    		{
				station_ids += station.getId() + ",";
	    		
	    		partneredStationCartList.add(new StationCart(null, stationCarts.getCartList(), stationCarts.getVC_contractno(), station.getId(), station.getGroupCode(), station.getNoScrubCartno()));
	    		//campaignDAO.saveTraffic(campaignStation.getId(), station.getId(), String.join(",", cartList));
    		}
    	}
    	
    	//If there are assignable stations
    	if(!station_ids.isEmpty())
    	{
	    	station_ids = station_ids.substring(0, station_ids.length()-1);  //Remove the last comma
	    	
	    	//TEST. IF THE STATION LIST CONTAINS KBBH THEN DO NOT PROCESS THE REQUEST AND RETURN FAILURE STATUS. TO BE REMOVED
	    	String[] elements = station_ids.split(",");
	        
	        for (String element : elements) 
	        {
	            if ("180663".equals(element)) 
	            {
	            	status = "0";
		    		return new String[]{status, unpartneredStations, unlicensedStations};
	            }
	        }
	    	/////////////////////////////////////////////////	    		    	
	    	/*The below call
	    	 * 1. Adds the advertiser to the campaign by selecting an existing one or creating a new one.
	    	 * 2. Assigns new stations to the advertiser(if needed). 
	    	 * 3. Deletes all station assignments and add anew to the campaign.
	    	 * 4. Deletes existing rdo campaigns (linked to the campaign) and its spots.
	    	 * 5. Sets the Order and campaign to active.
	    	 * Returns the status, advertiser id, item id, start date and end date for use later.
	    	 */
	    	//TBD: Check that carts exist and after Deleting all non numeric characters from them they are not empty. Do this before the below call
	    	CampaignStationDetail detail = campaignDAO.assignStations(campaignStation.getVC_POID(), campaignStation.getId(), station_ids);
	    	
	    	//If its a VC campaign
	    	if(detail.getStatus() == 1)  //If it goes inside this block, status changes to 1 but that can change on line #275
	    	{
	    		status = "1";
	    		
	    		//Iterate over the list of stations
	    		for(StationCart stationCarts : partneredStationCartList)
	    		{
	    			List<String> scrubbedCartList = new ArrayList<String>();  //This list is reinitialised per station
	    			
	    			//Iterate over the list of carts for each station
		    		for(String scrubbedCart : stationCarts.getCartList())
		    		{
		    			if(stationCarts.getNoScrubCartno() == 0)
		    			{		    			
			    			//Delete all non numeric characters from carts
			    			//scrubbedCart = scrubbedCart.replaceAll("\\D", "");
			    			
			    			//Delete all non digits preceding the first digit. Positive lookahead used here. This matches the rule in RDSParser for Ad syncs.
			    			scrubbedCart = scrubbedCart.replaceAll("^\\D+(?=\\d)", "");
		    			}
		    			
		    			//Weed out empty cart strings. Below we take only non empty carts.
		    			if(!scrubbedCart.isEmpty())
		    			{
		    				//Special handling of carts is needed for Cumulus stations. vCreative may or may not send them with leading 0s. We make sure they are 6 digits long by padding with leading zeroes. If its already 6 digits or more we leave it as is.
		    				if("CU".equals(stationCarts.getGroupCode()))
				    		{
		    					scrubbedCart = String.format("%1$6s", scrubbedCart).replace(' ', '0');
				    		}
		    				
		    				
		    				//Prohibit carts 9998 or 9999 from being entered. These are used for fake events and if we allowed an ad sync with these carts then During Other BBs will not show during fake events. Only Defaults will show. 
		    				if(!Arrays.asList(new String[]{"9998", "9999"}).contains(scrubbedCart))
		    				{
		    					scrubbedCartList.add(scrubbedCart);
		    				}
		    			}
		    		}
		    		
		    		//The scrubbedCartList passed to the below function could be empty in which case an rdo campaign record will be added for the station but no specific spot would be added for that record.
		    		int ret = campaignDAO.assignStationCarts(detail.getAdvertiserId(), detail.getItemId(), campaignStation.getId(), detail.getStartDate(), detail.getEndDate(), stationCarts.getStationId(), stationCarts.getVC_contractno(), String.join(",", scrubbedCartList));
		    	
		    		//If the above call failed then set status to "0". We are saying in the enclosing loop if any pass fails then we will let the loop continue with other passes but the response will be a failure code.
		    		if(ret == 0)
		    		{
		    			status = String.valueOf(ret);
		    		}
	    		}
	    		
	    		new Thread(() -> Util.clearQuuRDSCache()).start();
	    	}
	    }
    	
    	return new String[]{status, unpartneredStations, unlicensedStations};
    }
    
    //This method deletes carts from stations within a line item.
    public String[] deleteStationsCarts(CampaignStationIn campaignStation)
    {
    	String status = "0"; //Invalid PO id or line item id
    	
    	String unpartneredStations = "", unlicensedStations = "";
    	
    	int ret = campaignDAO.campaignExists(campaignStation.getVC_POID(), campaignStation.getId());
    	
    	//If its a VC campaign
    	if(ret == 1)
    	{
    		Map<String, Station> stationMap = Scheduler.stationMaps.getStationCallLettersMap();
    		
	    	for(StationCart stationCarts : campaignStation.getStationCartList()) //for each station
	    	{
	    		String callLetters = stationCarts.getStation();
	    		callLetters = callLetters.toUpperCase().replaceFirst("-FM$", "");
	    		
	    		Station station = stationMap.get(callLetters);
	    		
	    		//Station is not in Quu
	    		if(station == null)
	    		{
	    			unpartneredStations += stationCarts.getStation() + ",";
	    		}
	    		//Station is in Quu but does not have Adsync license
	    		else if(!ArrayUtils.contains(new int[]{3,4}, station.getPackage1()))
	    		{
	    			unlicensedStations += stationCarts.getStation() + ",";
	    		}
	    		//Station is partnered and licensed for Adsync
	    		else 
	    		{
	    			List<String> scrubbedCartList = new ArrayList<String>();  
		    		
	    			//Delete all non numeric characters from carts
		    		stationCarts.getCartList().forEach(cart -> scrubbedCartList.add(cart.replaceAll("\\D", "")));
	    			
	    			campaignDAO.deleteStationsCarts(campaignStation.getVC_POID(), campaignStation.getId(), station.getId(), String.join(",", scrubbedCartList));
	    		}
	    	}
	    	
	    	status = "1";
    		
	    	new Thread(() -> Util.clearQuuRDSCache()).start();
    	}
    	
    	return new String[]{status, unpartneredStations, unlicensedStations};
    }
    
    public int deactivate(String POID, int id) {
        
    	int ret = campaignDAO.deactivate(POID, id);
    	
    	new Thread(() -> Util.clearQuuRDSCache()).start();
    	
    	return ret;
    }
    
    
    private void saveImageOnImageserver(String VCImageUrl, int id, String imageName)
    {
    	Map<String, String> params = new HashMap<String, String>();
		params.put("downloadFile", VCImageUrl);
		params.put("imagePath", "campaign_images/" + id + "/logo");
		params.put("fileName", imageName);
		params.put("requestFrom", "QuuAPI");
		
		Util.getWebResponse(Constant.SAVEIMAGESERVICE_URL, params, false);
    }
        
}
