package com.quu.network.model;

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
public class Advertisement {

	@JsonProperty("CampaignID")
	private int id;
	@JsonProperty("ReportingID")
	private String reportingId;
	@JsonProperty("Length")
	private int length;
	
}
