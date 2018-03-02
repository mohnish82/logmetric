package com.mohnish.logmetric.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mohnish.logmetric.domain.LogEvent;

@Component
public class AsyncLogEventRepository {

	@Autowired
	LogEventRepository repository;
	
	@Async
	public void saveLogs(List<LogEvent> logs) {
		repository.save(logs);
	}
	
}
