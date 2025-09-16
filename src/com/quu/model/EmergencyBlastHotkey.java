package com.quu.model;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//This class is used with createBillboardsAndDependants.

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmergencyBlastHotkey {

	private int id;
	private String name;
	private String type;
	private String category;
	private int minutesAlive;
	private int durationInSeconds;
	private String line1;
	private String line2;
	private String logo;
	private List<String> options;
	private List<String> stations;
		
}
