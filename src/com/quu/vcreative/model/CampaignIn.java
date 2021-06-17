package com.quu.vcreative.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
//@RequiredArgsConstructor  //picks only those fields that are marked @NonNull
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)  //This is so fields passed from controller but not declared here are ignored.
public class CampaignIn {

	@JsonIgnore
	private String VC_advertiser_ID;
	
	@JsonProperty("VC_advertiser")
	private String advertiserName;
	
	@JsonProperty
	private String VC_POID;
	
	@JsonProperty("VC_PO_name")
	private String orderName;
		
	@JsonProperty("VC_source")
	private String source;
	
	@JsonProperty("LineItems")
	private List<LineItemIn> lineItems;
}