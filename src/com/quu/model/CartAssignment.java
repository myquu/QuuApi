package com.quu.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties
public class CartAssignment
{
	private String timestamp;
	private int campaignId;
	private String sid;
	private String callLetters;
	private String cart;
	private String userId;
}
