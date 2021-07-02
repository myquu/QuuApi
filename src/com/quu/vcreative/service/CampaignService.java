package com.quu.vcreative.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.vcreative.dao.ICampaignDAO;
import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignOut;
import com.quu.vcreative.model.CampaignStationDetail;
import com.quu.vcreative.model.CampaignStationIn;
import com.quu.vcreative.model.ImageIn;
import com.quu.vcreative.model.LineItemIn;
import com.quu.vcreative.model.LineItemOut;
import com.quu.vcreative.model.StationCart;
import com.quu.model.Station;
import com.quu.util.Constant;
import com.quu.util.Scheduler;
import com.quu.util.Util;


@RequestScoped
public class CampaignService implements ICampaignService{

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
    			String VCImageUrl = lineItemIn.getImageUrl();
    			String imageName = null;
    			
    			if(VCImageUrl != null)
	        	{
	        		imageName = VCImageUrl.substring(VCImageUrl.lastIndexOf("/")+1);
	        		lineItemIn.setImageName(imageName);
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
	    			
	    			if(imageName != null)
	    	    	{
	    	    		final String imageNameF = imageName;
	    	    		
	    	    		new Thread(() -> saveImageOnImageserver(VCImageUrl, lineItemId, imageNameF)).start();
	    	    	}
	    			
	    			lineItemOuts.add(new LineItemOut(lineItemIn.getVC_LineItem_ID(), lineItemId, (Constant.RDSCAMPAIGNPREVIEWURL + lineItemId)));
		        }
    		}
    		
    		campaignOut.setLineItems(lineItemOuts);
    		
    		//If any line item is active clear the cache.
    		if(anyActive)
    		{
    			Util.clearQuuRDSCache();
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
	    		Util.clearQuuRDSCache();
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
    	
    	String station_ids = "", unpartneredStations = "";
    	
    	List<StationCart> partneredStationCartList = new ArrayList<>();  
		    		
    	for(StationCart stationCarts : campaignStation.getStationCartList()) //for each station
    	{
    		String callLetters = stationCarts.getStation();
    		callLetters = callLetters.toUpperCase().replaceFirst("-FM$", "");
    		
    		Station station = Scheduler.StationMap.get(callLetters);
    		
    		//Partnered station
    		if(station != null)
    		{
    			station_ids += station.getId() + ",";
	    		
	    		partneredStationCartList.add(new StationCart(null, stationCarts.getCartList(), station.getId()));
	    		//campaignDAO.saveTraffic(campaignStation.getId(), station.getId(), String.join(",", cartList));
    		}
    		else
    		{
    			unpartneredStations += stationCarts.getStation() + ",";
    		}
    	}
    	
    	//If there are partnered stations
    	if(!station_ids.isEmpty())
    	{
	    	station_ids = station_ids.substring(0, station_ids.length()-1);  //Remove the last comma
	    	
	    	/*The below call
	    	 * 1. Updates the advertiser(if needed)
	    	 * 2. Assigns new stations to the advertiser(if needed). 
	    	 * 3. Deletes all station assignments and add anew to the campaign.
	    	 * 4. Deletes existing rdo campaigns and its spots.
	    	 * 5. Sets the order and campaign to active.
	    	 * Returns the status, advertiser id, item id, start date and end date for use later.
	    	 */
	    	CampaignStationDetail detail = campaignDAO.assignStations(campaignStation.getVC_POID(), campaignStation.getId(), station_ids);
	    	
	    	//If its a VC campaign
	    	if(detail.getStatus() == 1)
	    	{
	    		for(StationCart stationCarts : partneredStationCartList)
	    		{
	    			List<String> scrubbedCartList = new ArrayList<String>();  
		    		
	    			//Delete all non numeric characters from carts
		    		stationCarts.getCartList().forEach(cart -> scrubbedCartList.add(cart.replaceAll("\\D", "")));
	    			
	    			campaignDAO.assignStationCarts(detail.getAdvertiserId(), detail.getItemId(), campaignStation.getId(), detail.getStartDate(), detail.getEndDate(), stationCarts.getStationId(), String.join(",", scrubbedCartList));
	    		}
	    		
	    		status = "1";
	    		
	    		Util.clearQuuRDSCache();  //Clear cache
	    	}
	    }
    	
    	return new String[]{status, unpartneredStations};
    }
    
    //This method deletes carts from stations within a line item.
    public String[] deleteStationsCarts(CampaignStationIn campaignStation)
    {
    	String status = "0"; //Invalid PO id or line item id
    	
    	String unpartneredStations = "";
    	
    	int ret = campaignDAO.campaignExists(campaignStation.getVC_POID(), campaignStation.getId());
    	
    	//If its a VC campaign
    	if(ret == 1)
    	{
	    	for(StationCart stationCarts : campaignStation.getStationCartList()) //for each station
	    	{
	    		String callLetters = stationCarts.getStation();
	    		callLetters = callLetters.toUpperCase().replaceFirst("-FM$", "");
	    		
	    		Station station = Scheduler.StationMap.get(callLetters);
	    		
	    		//Partnered station
	    		if(station != null)
	    		{
	    			List<String> scrubbedCartList = new ArrayList<String>();  
		    		
	    			//Delete all non numeric characters from carts
		    		stationCarts.getCartList().forEach(cart -> scrubbedCartList.add(cart.replaceAll("\\D", "")));
	    			
	    			campaignDAO.deleteStationsCarts(campaignStation.getVC_POID(), campaignStation.getId(), station.getId(), String.join(",", scrubbedCartList));
		    	}
	    		else
	    		{
	    			unpartneredStations += stationCarts.getStation() + ",";
	    		}
	    	}
	    	
	    	status = "1";
    		
    		Util.clearQuuRDSCache();  //Clear cache
    	}
    	
    	return new String[]{status, unpartneredStations};
    }
    
    public int deactivate(String POID, int id) {
        
    	int ret = campaignDAO.deactivate(POID, id);
    	
    	Util.clearQuuRDSCache();
    	
    	return ret;
    }
    
    
    private void saveImageOnImageserver(String VCImageUrl, int id, String imageNameF)
    {
    	//Handle image base64 to url conversion and save on image server
		Map<String, String> params = new HashMap<String, String>();
		params.put("url", VCImageUrl);
		params.put("imagePath", "campaign_images/" + id + "/logo");
		params.put("name", imageNameF);
			    		
		Util.getWebResponse(Constant.IMAGEFROMURLSERVICE_URL, params, false);
    }
    
}
