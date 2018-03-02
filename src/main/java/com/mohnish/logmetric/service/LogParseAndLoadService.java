package com.mohnish.logmetric.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.mohnish.logmetric.domain.LogEvent;
import com.mohnish.logmetric.repository.AsyncLogEventRepository;

@Component
public class LogParseAndLoadService {
	static final Logger logger = LoggerFactory.getLogger(LogParseAndLoadService.class);

	@Autowired
	AsyncLogEventRepository repository;
	
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	int batchSize;
	
	@Autowired
	public LogParseAndLoadService(Environment env) {
		String jdbcBatchSize = env.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size");
		batchSize = jdbcBatchSize != null ? Integer.parseInt(jdbcBatchSize) : 100;
	}

	public void process(Path logFilePath) throws IOException {
		String digest = null;
		List<LogEvent> logs = new ArrayList<>(batchSize);

		try(Scanner file = new Scanner(logFilePath)) {
			while(file.hasNext()) {
				String[] tokens = file.nextLine().split("\\|");
			
				digest = digest(tokens);
				logs.add(new LogEvent(digest, LocalDateTime.parse(tokens[0], dateFormatter), tokens[1]));
				
				if(logs.size() == batchSize) {
					List<LogEvent> copy = new ArrayList<>(logs.size());
					copy.addAll(logs);
					logs.clear();
					saveLogs(copy);
				}
			}
			
			if(!logs.isEmpty())
				saveLogs(logs);
		}
	}

	protected void saveLogs(List<LogEvent> logs) {
		repository.saveLogs(logs);
	}
	
	protected String digest(String[] tokens) {
		return DigestUtils.md5DigestAsHex((tokens[0] + tokens[1]).getBytes());
	}
	
}
