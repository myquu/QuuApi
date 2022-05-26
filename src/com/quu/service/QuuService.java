package com.quu.service;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.quu.dao.IQuuDAO;
import com.quu.model.Station;
import com.quu.model.StationMaps;


@ApplicationScoped
public class QuuService implements IQuuService{

	@Inject
    private IQuuDAO quuDAO;
	
	public StationMaps getStations()
	{
		return quuDAO.getStations();
	}
}
