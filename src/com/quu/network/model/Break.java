package com.quu.network.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Break {

	@JsonProperty("BreakID")
	private int id;
	@JsonProperty("BreakPos")
	private int pos;
	@JsonProperty("ScheduledTime")
	private String scheduledTime;	
	@JsonProperty("Advertisements")
	private List<Advertisement> advertisementList;
	
}
