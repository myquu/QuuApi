package com.quu.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BBCampaign {

	private String rt1;
    private String rt2;
    private String dps1;
    private String dps2;
    private String dps3;
    private String dps4;
    private String dps5;
    private String dps6;
    private String dps7;
    private String dps8;
    private String start_date;
    private String end_date; 
    private String message_type;
    private String name;
    private String remarks; 
    
    @JsonProperty("schedules")	
	private List<BBSchedule> scheduleList;
}
