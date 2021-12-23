package com.quu.watermark.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
//@RequiredArgsConstructor  //picks only those fields that are marked @NonNull
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)  //This is so fields passed from controller but not declared here are ignored.
public class CampaignIn {

	@JsonProperty("campaignId")
	private int id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("startDate")
	private String startDate;
	
	@JsonProperty("endDate")
	private String endDate;
	
	@JsonProperty("line1")
	private String line1;
	
	@JsonProperty("line2")
	private String line2;
	
	//Submitter user's email id
	//@JsonProperty("email")
	//private String email;
	
	//@JsonProperty("source")
	//private String source;
	
	@JsonProperty("imageUrl")
	private String imageUrl;
	
	//The image name is not passed from controller. It is a constant that will be saved only if image param comes with a value from controller.
	private String imageName;
	
	//The 8 dps fields are not passed from controller. 
	private String dps1;
	private String dps2;
	private String dps3;
	private String dps4;
	private String dps5;
	private String dps6;
	private String dps7;
	private String dps8;
	
}