package com.quu.util;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.quu.service.IQuuService;

//https://zetcode.com/jaxrs/resteasycrud/
//This is where we put any code that needs to be run on app startup.

@WebListener
public class Initializer implements ServletContextListener {
	
	@Inject
	private IQuuService quuService;
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		Scheduler.StationMap = quuService.getStations();
		//System.out.println("QuuAPI Stations: " + Scheduler.Stations.size());   
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
				
	}
}
