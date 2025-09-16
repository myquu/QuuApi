package com.quu.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TimeAvailablePerHourOutput {

	private String sid;
	private String callLetters;
	private String date;
	private List<TimeAvailablePerHour> hours;
}
