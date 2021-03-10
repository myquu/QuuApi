package com.quu.network.service;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.json.JSONObject;

import com.quu.network.dao.IScheduleDAO;
import com.quu.network.model.Schedule;


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
    public int add(Schedule schedule) {
        
    	return scheduleDAO.add(schedule);
    }

    @Override
    public int update(Schedule schedule) {
        
    	return scheduleDAO.update(schedule);
    }
    
    @Override
    public boolean delete(int id) {
        
        return scheduleDAO.delete(id);
    }
}
