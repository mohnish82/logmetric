package com.mohnish.logmetric.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogEventRepositoryCustom {
	public List<String> findIpAddressesByVisitsBetweenDates(Long numOfVisits, LocalDateTime startTime, LocalDateTime endTime); 
}
