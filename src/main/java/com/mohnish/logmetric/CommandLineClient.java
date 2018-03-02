package com.mohnish.logmetric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.mohnish.logmetric.domain.LogDuration;
import com.mohnish.logmetric.service.LogAnalyzerService;
import com.mohnish.logmetric.service.LogParseAndLoadService;

@Component
public class CommandLineClient implements ApplicationRunner {
	static final String INVALID_ARGS_MSG = "Please provide correct input arguments - startDate, duration & threshold. To load access logs into database, also provide - accesslog";
	Logger logger = LoggerFactory.getLogger(CommandLineClient.class);
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");

	@Autowired
	Environment env;
	
	@Autowired
	LogParseAndLoadService logProcessor;
	
	@Autowired
	LogAnalyzerService logAnalyzer;
	
	@Autowired
	TaskExecutor executor;	
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		String _startDate = null;
		String _threshold = null;
		String _duration = null;
		String _accessLog = null;
		
		for(String argName : args.getOptionNames()) {
			switch (argName) {
			case "startDate":
				_startDate = args.getOptionValues(argName).isEmpty() ? null : args.getOptionValues(argName).get(0); break;
			case "duration":
				_duration = args.getOptionValues(argName).isEmpty() ? null : args.getOptionValues(argName).get(0); break;
			case "threshold":
				_threshold = args.getOptionValues(argName).isEmpty() ? null : args.getOptionValues(argName).get(0); break;
			case "accesslog":
				_accessLog = args.getOptionValues(argName).isEmpty() ? null : args.getOptionValues(argName).get(0); break;
			default:
				break;
			}
		}
		
		if(_startDate == null || _duration == null || _threshold == null)
			return;
		
		LocalDateTime startDate = parseStartDate(_startDate);
		Integer threshold = parseThreshold(_threshold);
		LogDuration duration = "daily".equals(_duration) ? LogDuration.DAILY
							: "hourly".equals(_duration) ? LogDuration.HOURLY : null;
	
		Path path = parseLogFilePath(_accessLog);
		
		if(path != null) {
			logger.info("Log file found, loading into database.");
			try { logProcessor.process(path); }
			catch(IOException e){
				logger.error("Error occurred while processing log file - {}", e.getMessage());
				throw new RuntimeException(e);
			}
		}
		
		if(startDate == null || duration == null || threshold == null) {
			System.out.println(INVALID_ARGS_MSG);
			System.exit(0);
		}
		
		//TODO - prefer thread join facility.
		((ThreadPoolTaskExecutor)executor).shutdown();
		logger.info("Log file load complete.");
		
		List<String> ipAddresses = logAnalyzer.findIpAddressByVisits(threshold, startDate, duration);
		
		if(ipAddresses == null || ipAddresses.isEmpty())
			System.out.println("No IP(s) matching the input criteria found.");
		else{
			System.out.printf("%d IP(s) match the criteria. %n", ipAddresses.size());
			
			for(String address : ipAddresses) {
				System.out.println(address);
			}
		}
		
	}

	private Path parseLogFilePath(String location) {
		if(location == null)
			return null;
		
		try{
			Path path = Paths.get(location).normalize();
			return Files.exists(path) ? path : null;
		}catch(Exception e){
			logger.error("Invalid access log path - {}. Error: {}", location, e.getMessage());
		}
		
		return null;
	}

	private LocalDateTime parseStartDate(String startDate) {
		try{
			return LocalDateTime.parse(startDate, dateFormatter);
		}catch(DateTimeParseException e){
			logger.error("Invalid startDate {}", startDate);
			return null;
		}
	}

	private Integer parseThreshold(String threshold) {
		try{
			return Integer.parseInt(threshold);
		}catch(NumberFormatException e){
			logger.error("Invalid threshold {}", threshold);
			return null;
		}
	}

}
