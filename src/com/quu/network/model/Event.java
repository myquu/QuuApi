package com.quu.network.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
@JsonIgnoreProperties({ "EventCampaignID", "EventDescription", "EventDate" })
public class Event {

	@JsonProperty("EventID")
	private int id;
	@JsonProperty("EventCampaignID")
	private int campaignID;
	@JsonProperty("EventDate")
	private String date;
	@JsonProperty("Breaks")
	private List<Break> breakList;
	/*
	@XmlElement(required = false)
	private int EventCampaignID;
	@XmlElement(required = false)
	private String EventDescription;
	@XmlTransient
	@XmlElement(required = false)
	private String EventDate;
	*/
}
