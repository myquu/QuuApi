package com.quu.skyview.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(of = {"eventId"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

	@JsonProperty("EventID")
	private int eventId;
	@JsonProperty("EventCampaignID")
	private int campaignId;
	@JsonProperty("QBreaks")
	private List<Break> breakList;
	
	
	//Internal properties
	@JsonProperty(access = Access.WRITE_ONLY)
	private int id;
}
