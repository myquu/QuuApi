package com.quu.vcreative.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.vcreative.dao.ICampaignDAO;
import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignStationIn;
import com.quu.vcreative.model.StationCart;
import com.quu.model.Station;
import com.quu.util.Constant;
import com.quu.util.Scheduler;
import com.quu.util.Util;


@RequestScoped
public class CampaignService implements ICampaignService{

	@Inject
    private ICampaignDAO campaignDAO;
		

    //Creates a new campaign. 
    @Override
    public int add(CampaignIn campaign) {
        
    	String imageName = null;
    	String VCImageUrl = campaign.getImageUrl();
    	
    	if(VCImageUrl != null)
    	{
    		imageName = VCImageUrl.substring(VCImageUrl.lastIndexOf("/")+1);
    		campaign.setImageName(imageName);
    	}
    	
    	int id = campaignDAO.add(campaign);
    	
    	if(imageName != null)
    	{
    		final String imageNameF = imageName;
    		
	    	new Thread(() -> saveImageOnImageserver(VCImageUrl, id, imageNameF)).start();
    	}
    	
    	return id;
    }

    //Updates a campaign. 
    @Override
    public int update(CampaignIn campaign) {
        
    	String imageName = null;
    	String VCImageUrl = campaign.getImageUrl();
    	
    	if(VCImageUrl != null)
    	{
    		imageName = VCImageUrl.substring(VCImageUrl.lastIndexOf("/")+1);
    		campaign.setImageName(imageName);
    	}
    	
    	int[] ret = campaignDAO.update(campaign);
    	
    	int updateCount = ret[0];
    	
    	//No. of rows updated. 
    	if(updateCount > 0)
    	{
	    	//active status
	    	if(ret[1] == 1)
	    	{
	    		Util.clearQuuRDSCache();
	    	}
    	
	    	if(imageName != null)
	    	{
	    		final String imageNameF = imageName;
	    		
	    		new Thread(() -> saveImageOnImageserver(VCImageUrl, campaign.getId(), imageNameF)).start();
	    	}
    	}
    	    	
    	return updateCount;
    }
    
    //Assigns an image to a campaign. 
    @Override
    public int assignImage(CampaignIn campaign) {
        
    	String VCImageUrl = campaign.getImageUrl(),
			imageName = VCImageUrl.substring(VCImageUrl.lastIndexOf("/")+1);
    	
    	campaign.setImageName(imageName);
    	    	
    	int[] ret = campaignDAO.assignImage(campaign);
    	
    	int updateCount = ret[0];
    	
    	//No. of rows updated. 
    	if(updateCount > 0)
    	{
	    	//active status
	    	if(ret[1] == 1)
	    	{
	    		Util.clearQuuRDSCache();
	    	}
    		    	
    		final String imageNameF = imageName;
    		
    		new Thread(() -> saveImageOnImageserver(VCImageUrl, campaign.getId(), imageNameF)).start();
	    }
    	    	
    	return updateCount;
    }
    
    //This method assigns stations to the campaign and adds the carts in Marketron.
    public String[] assignStationsCarts(CampaignStationIn campaignStation)
    {
    	String station_ids = "", unpartneredStations = "";
    	
    	int ret = campaignDAO.campaignExists(campaignStation.getId());
    	
    	if(ret == 1)
    	{
	    	for(StationCart stationCart : campaignStation.getStationCartList()) 
	    	{
	    		String callLetters = stationCart.getStation();
	    		callLetters = callLetters.toUpperCase().replaceFirst("-FM$", "");
	    		
	    		Station station = Scheduler.StationMap.get(callLetters);
	    		
	    		//Partnered station
	    		if(station != null)
	    		{
		    		station_ids += station.getId() + ",";
		    		
		    		List<String> cartList = new ArrayList<String>();  
		    		//Delete all non numeric characters from carts
		    		stationCart.getCartList().forEach(cart -> cartList.add(cart.replaceAll("\\D", "")));
		    		
		    		//Insert in Marketron
		    		campaignDAO.saveTraffic(campaignStation.getId(), station.getId(), String.join(",", cartList));
	    		}
	    		else
	    			unpartneredStations += callLetters + ",";
	    	}
	    	
	    	if(!station_ids.isEmpty())
	    	{
		    	station_ids = station_ids.substring(0, station_ids.length()-1);  //Remove the last comma
		    	
		    	//Assign stations to the campaign and activate. 
		    	campaignDAO.assignStations(campaignStation.getId(), station_ids);
	    	}
	    	
	    	Util.clearQuuRDSCache();  //Clear cache
    	}
    	
    	return new String[]{unpartneredStations, null};
    }
    
    //In the SP we check if the id belongs to the Sky Item. Only then we delete.
    @Override
    public int deactivate(int id) {
        
    	int ret = campaignDAO.deactivate(id);
    	
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
