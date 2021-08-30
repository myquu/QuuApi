package com.quu.skyview.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Advertisement {

	@JsonProperty("CampaignID")
	private int campaignId;
	@JsonProperty("Length")
	private int length;
	
	//Internal properties
	@JsonProperty(access = Access.WRITE_ONLY)
	private int id;
}
