package com.quu.network.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.network.dao.ICampaignDAO;
import com.quu.network.model.Campaign;
import com.quu.util.Constant;
import com.quu.util.Util;


@RequestScoped
public class CampaignService implements ICampaignService{

	private static final String IMAGENAME = "logo.jpeg";
	
	@Inject
    private ICampaignDAO campaignDAO;
		

    @Override
    public List<Campaign> getAll() {

        return campaignDAO.getAll();
    }
    
    @Override
    public Campaign get(int id) {
        
        return campaignDAO.get(id);
    }
    
    //Creates a new campaign. If a Base64 string(image) is passed, we convert it to an image file and save it on imageserver. To save it we need the campaign id. 
    @Override
    public int add(Campaign campaign) {
        
    	if(campaign.getImage() != null)
    	{
    		campaign.setImageName(IMAGENAME);
    	}
    	
    	int id = campaignDAO.add(campaign);
    	
    	if(campaign.getImage() != null)
    	{
	    	new Thread(() -> {
		    	
	    		//Handle image base64 to url conversion and save on image server
	    		//String params = "imagePath=campaign_images/" + id + "/logo&name=" + IMAGENAME + "&base64String=" + campaign.getImage();
	    		Map<String, String> params = new HashMap<String, String>();
	    		params.put("imagePath", "campaign_images/" + id + "/logo");
	    		params.put("name", IMAGENAME);
	    		params.put("base64String", campaign.getImage());
	        	
	    		Util.getWebResponse(Constant.BASE64STRINGTOIMAGESERVICE_URL, params, false);
	        	
		    }).start();
    	}
    	
    	return id;
    }

    //Updates a campaign. If a Base64 string(image) is passed, we convert it to an image file and save it on imageserver. To save it we need the campaign id.
    @Override
    public int update(Campaign campaign) {
        
    	if(campaign.getImage() != null)
    	{
    		campaign.setImageName(IMAGENAME);
    	}
    	
    	int[] ret = campaignDAO.update(campaign);
    	
    	int updateCount = ret[0];
    	
    	//No. of rows updated . Will be 0 or 1 
    	if(updateCount > 0)
    	{
	    	//active status
	    	if(ret[1] == 1)
	    	{
	    		Util.clearQuuRDSCache();
	    	}
	    	
	    	if(campaign.getImage() != null)
	    	{
		    	new Thread(() -> {
			    	
		    		//Handle image base64 to url conversion and save on image server
		    		Map<String, String> params = new HashMap<String, String>();
		    		params.put("imagePath", "campaign_images/" + campaign.getId() + "/logo");
		    		params.put("name", IMAGENAME);
		    		params.put("base64String", campaign.getImage());
		    		
		    		Util.getWebResponse(Constant.BASE64STRINGTOIMAGESERVICE_URL, params, false);
		        	
			    }).start();
	    	}
    	}
    	
    	return updateCount;
    }
    
    //In the SP we check if the id belongs to the Sky Item. Only then we delete.
    @Override
    public int delete(int id) {
        
        return campaignDAO.delete(id);
    }
}
