package com.quu.watermark.service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.watermark.dao.ICampaignDAO;
import com.quu.watermark.model.CampaignIn;
import com.quu.watermark.model.CampaignOut;
import com.quu.watermark.model.CampaignQId;
import com.quu.util.Constant;
import com.quu.util.Util;


@RequestScoped
public class CampaignService implements ICampaignService{

	@Inject
    private ICampaignDAO campaignDAO;
		

    //Handles both add and edit of an Order and line items inside it. 
    @Override
    public CampaignOut save(CampaignIn campaignIn) {
        
    	String imageUrl = campaignIn.getImageUrl();
		String imageName = null;
			
		if(imageUrl != null)
    	{
    		imageName = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
    		campaignIn.setImageName(imageName);
    	}
		
		List<String> list = Util.setDPSFields(campaignIn.getLine1(), campaignIn.getLine2());
		
		if(list != null)
    	{
    		//Populate DPS fields using reflection
    		try 
    		{
    			for(int i=0; i<list.size(); i++)
        		{
	    			PropertyDescriptor pd = new PropertyDescriptor("dps"+(i+1), campaignIn.getClass());
	    			// Call setter on specified property (setDps1())
	    			pd.getWriteMethod().invoke(campaignIn, list.get(i));
        		}
		    } 
    		catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
    	}
		
		int[] ret = campaignDAO.saveCampaign(campaignIn);
		
		if(ret != null)
		{
			int id = ret[0],
				active = ret[1];
			
			if(imageName != null)
	    	{
	    		final String imageNameF = imageName;
	    		
	    		new Thread(() -> saveImageOnImageserver(imageUrl, id, imageNameF)).start();
	    	}
			
			//If any line item is active clear the cache.
			if(active == 1)
    		{
    			new Thread(() -> Util.clearQuuRDSCache()).start();
    		}
			
			return new CampaignOut(id);
		}
    		
    	return null;	    		    		
    }
        
    public int assignQIds(CampaignQId campaignQId)
    {
    	return campaignDAO.assignQIds(campaignQId.getId(), String.join(",", campaignQId.getQIds()));
    }
    
    public int unassignQIds(CampaignQId campaignQId)
    {
    	return campaignDAO.unassignQIds(campaignQId.getId(), String.join(",", campaignQId.getQIds()));
    }
    
    public int deactivate(int id) {
        
    	int ret = campaignDAO.deactivate(id);
    	
    	new Thread(() -> Util.clearQuuRDSCache()).start();
    	
    	return ret;
    }
    
    
    private void saveImageOnImageserver(String imageUrl, int id, String imageNameF)
    {
    	Map<String, String> params = new HashMap<String, String>();
		params.put("url", imageUrl);
		params.put("imagePath", "networkcampaign_images/" + id + "/logo");
		params.put("name", imageNameF);
			    		
		Util.getWebResponse(Constant.IMAGEFROMURLSERVICE_URL, params, false);
    }
        
}
