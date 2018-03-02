package com.mohnish.logmetric.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mohnish.logmetric.domain.LogDuration;
import com.mohnish.logmetric.repository.LogEventRepository;

@Service
public class LogAnalyzerService {

	@Autowired
	LogEventRepository repository;
	
	public List<String> findIpAddressByVisits(int numOfVisits, LocalDateTime startTime, LogDuration duration ) {
		
		if(numOfVisits < 0 || startTime == null  || duration == null)
			return null; 
		

		LocalDateTime endTime = LogDuration.DAILY == duration
								? startTime.plusDays(1)
								: startTime.plusHours(1);
		
		return repository.findIpAddressesByVisitsBetweenDates(Integer.valueOf(numOfVisits).longValue(), startTime, endTime);
	}
	
}
