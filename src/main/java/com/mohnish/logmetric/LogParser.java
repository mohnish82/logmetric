package com.mohnish.logmetric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.mohnish.logmetric.domain.LogEvent;
import com.mohnish.logmetric.repository.LogEventRepository;

@Component
public class LogParser {

	@Autowired
	LogEventRepository repository;
	
	Path logFilePath;
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Autowired
	public LogParser(Environment env) {
		String location = env.getProperty("accessLog");
		Path path = Paths.get(location).normalize();
		
		if(Files.exists(path))
			logFilePath =  path;
	}

	@PostConstruct
	public void parseLogFile() throws IOException {
		
		try (Stream<String> lines = Files.lines(logFilePath)) {
			lines.parallel().forEach(line -> {
				String[] tokens = line.split("\\|");
				
				String digest = digest(tokens);
				LogEvent event = new LogEvent(
										digest, 
										LocalDateTime.parse(tokens[0], dateFormatter), 
										tokens[1]);
				
				repository.save(event);
			});
		}
	}

	protected String digest(String[] tokens) {
		return DigestUtils.md5DigestAsHex((tokens[0] + tokens[1]).getBytes());
	}
	
}
