package com.quu.model;

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
public class Station
{
	private int id;
	private String sid;
	private String callLetters;
    private int package1;
    
    @JsonProperty("HD")  //Without this it will be deserialised as "hd"
    private int hd;
    
    private int ignoreAutomation;
    private String marketName;
    private String groupName;
    private String groupCode;
    private String tzName;
}
