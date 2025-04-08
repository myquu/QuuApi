package com.quu.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) 
public class BBSchedule {

	private String name;
	private String start_time;
	@Setter(AccessLevel.NONE)  //NONE means do not generate a setter for this property
    private String end_time;
	
    private int duration;
    private List<String> radio_station_ids;
    private List<String> day_ids;
	private List<String> option_ids;
	private int show_logo_mus;
	
	
	//Transform end time from 00:00 => 23:59
	public void setEnd_time(String end_time) {
		
		if(end_time.equals("00:00"))
		{
			end_time = "23:59";
		}
		
		this.end_time = end_time;
	}
		
}
