package com.mohnish.logmetric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.mohnish.logmetric.domain.LogEvent;
import com.mohnish.logmetric.repository.AsyncLogEventRepository;

@Component
public class LogParser {
	static final Logger logger = LoggerFactory.getLogger(LogParser.class);
	
	@Autowired
	AsyncLogEventRepository repository;
	
	Path logFilePath;
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	
	int batchSize;
	
	@Autowired
	public LogParser(Environment env) {
		String jdbcBatchSize = env.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size");
		batchSize = jdbcBatchSize != null ? Integer.parseInt(jdbcBatchSize) : 100;
		
		String location = env.getProperty("accessLog");
		Path path = Paths.get(location).normalize();
		
		if(Files.exists(path))
			logFilePath =  path;
	}

	@PostConstruct
	public void parseLogFile() throws IOException {
		
		if(logFilePath == null)
			return;
		
		logger.info("Log file found, loading into database.");
		
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
