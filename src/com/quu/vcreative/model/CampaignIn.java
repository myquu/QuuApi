package com.quu.vcreative.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

public class CampaignIn {

	@JsonProperty
	private String VC_advertiser_ID;
	
	@JsonProperty("VC_advertiser")
	private String advertiserName;
	
	//@JsonProperty("Q_Ad_ID")
	//private int orderId;
	
	@JsonProperty
	private String VC_POID;
	
	@JsonProperty("VC_PO_name")
	private String orderName;
		
	@JsonProperty("Q_Campaign_ID")
	private int id;  //Line item id
	
	@JsonProperty("VC_campaign")
	private String name;  //Line item name
		
	@JsonProperty("VC_line1")
	private String line1;
	
	@JsonProperty("VC_line2")
	private String line2;
	
	@JsonProperty("VC_startDate")
	private String startDate;
	
	@JsonProperty("VC_endDate")
	private String endDate;
	
	@JsonProperty("VC_submitter_email")
	private String email;
	
	@JsonProperty("VC_imageUrl")
	private String imageUrl;
	
	@JsonProperty
	private String VC_adId;
	
	@JsonProperty("VC_source")
	private String source;
	
	//The image name is not passed from controller. It is a constant that will be saved only if image param comes with a value from controller.
	private String imageName;
		
}