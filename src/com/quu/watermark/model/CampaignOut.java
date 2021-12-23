package com.quu.watermark.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;


@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
@AllArgsConstructor
@Getter
@Setter

public class CampaignOut {

	@JsonProperty("campaignId")
	private int id;
		
}