package com.quu.network.service;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.json.JSONObject;

import com.quu.network.dao.IScheduleDAO;
import com.quu.network.model.Schedule;
import com.quu.util.Util;


@RequestScoped
public class ScheduleService implements IScheduleService{

	@Inject
    private IScheduleDAO scheduleDAO;
		

	@Override
    public List<Schedule> getAll() {

        return scheduleDAO.getAll();
    }
    
    @Override
    public Schedule get(int id) {
        
        return scheduleDAO.get(id);
    }
    
    @Override
    public int add(String jsonS, int StationID, String CampaignIDs) {
    	
    	return scheduleDAO.add(jsonS, StationID, CampaignIDs);
    }

    @Override
    public int update(int id, String jsonS, int StationID, String CampaignIDs) {
        
    	int updateCount = scheduleDAO.update(id, jsonS, StationID, CampaignIDs);
    	
    	//No. of rows updated . Will be 0 or 1 
    	if(updateCount > 0)
    	{
	    	Util.clearQuuRDSCache();
	    }
    	
    	return updateCount;
    }
    
    //In the SP we check if the id belongs to the Sky Item. Only then we delete.
    @Override
    public int delete(int id) {
        
    	int ret = scheduleDAO.delete(id);  //TBD: call it delete and deactivate
    	
    	Util.clearQuuRDSCache();
    	
    	return ret;
    }
}
