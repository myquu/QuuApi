package com.quu.service;

import io.ably.lib.rest.AblyRest;
import io.ably.lib.rest.Channel;
import io.ably.lib.types.AblyException;

public class AblyUtil {

	private static final String API_KEY = "1M_Img.BBTlpQ:nUhp6CSKY3_sf_UTP4uDzBfRPv4Wf9Pe8qz8EDWGOv4";  //"EjP3HQ.kMTssw:PdXlYVBIqdUdPM-S"; 
	
	/**
	 * 
	 * @param eventName - This will be the network name
	 * @param channelName - {station group code}. This is the channel name. There will be a channel per station group.
	 * @param data
	 */
	public static void publish(String eventName, String channelName, String data)
	{
		try {
			AblyRest rest = new AblyRest(API_KEY );
			Channel channel = rest.channels.get(channelName);  //"NetworkTrigger:" + trigger.getStationId()
			channel.publish(eventName, data);
		}
		catch(AblyException ex) {System.out.println("QuuAPI:AblyUtil " +ex.getMessage());}
	}
	
}
