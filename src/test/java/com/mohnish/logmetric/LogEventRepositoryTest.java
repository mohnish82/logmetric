package com.mohnish.logmetric;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mohnish.logmetric.repository.LogEventRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogEventRepositoryTest {

	@Autowired
	LogEventRepository repo;
	
	@Test
	@Ignore
	public void findIpAddressesByVisitsBetweenDates() {
		
		LocalDateTime start = LocalDateTime.of(2017, Month.JANUARY, 1, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2017, Month.JANUARY, 1, 23, 59, 59);
		Long visits = 500L;
		
		List<String> ipAddresses = repo.findIpAddressesByVisitsBetweenDates(visits, start, end);
		
		assertTrue(ipAddresses.contains("192.168.102.136"));
		
		
	}
	
}
