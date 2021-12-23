package com.quu.watermark.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)  //This is so fields passed from controller but not declared here are ignored.
public class CampaignQId {

	@JsonProperty("campaignId")
	private int id;
	
	@JsonProperty("QIds")
	private List<String> QIds;
	
}
