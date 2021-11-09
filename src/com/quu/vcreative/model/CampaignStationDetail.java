package com.quu.vcreative.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//This class is used internally

@AllArgsConstructor
@Getter
@Setter
public class CampaignStationDetail {

	private int status;
	private int advertiserId;
	private int itemId;
	private String startDate;
	private String endDate;
}
