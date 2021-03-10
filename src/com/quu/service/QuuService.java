package com.quu.service;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.dao.IQuuDAO;
import com.quu.model.Station;


@ApplicationScoped
public class QuuService implements IQuuService{

	@Inject
    private IQuuDAO quuDAO;
	
	public Map<String, Station> getStations()
	{
		return quuDAO.getStations();
	}
}
