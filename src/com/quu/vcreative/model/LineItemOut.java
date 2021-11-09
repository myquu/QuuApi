package com.quu.vcreative.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
@AllArgsConstructor
@Getter
@Setter

public class LineItemOut {

	@JsonProperty("VC_LineItem_ID")
	private String VC_LineItem_ID;
	
	@JsonProperty("Q_LineItem_ID")
	private int id;  //Line item id
	
	@JsonProperty("Q_LineItem_URL")
	private String previewUrl;		
}