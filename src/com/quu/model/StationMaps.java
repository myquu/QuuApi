package com.quu.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StationMaps {

	//Key: Id, Value: Station obj
	private Map<Integer, Station> StationIdMap;
	//Key: sid, Value: Station obj
	private Map<String, Station> StationSidMap;
	//Key: callLetters, Value: Station obj
	private Map<String, Station> StationCallLettersMap;
		
}
