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
public class Segment {

	@JsonProperty("order")
	private int order;
	@JsonProperty("duration")
	private int duration;
	@JsonProperty("campaignId")
	private int campaignId = -1;  //-1 is the default value
	
	//Internal properties
	@JsonProperty(access = Access.WRITE_ONLY)
	private int id;
}
