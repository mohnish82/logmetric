package com.mohnish.logmetric.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class LogEventRepositoryImpl implements LogEventRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;
	
	@Override
	public List<String> findIpAddressesByVisitsBetweenDates(Long numOfVisits, LocalDateTime startTime, LocalDateTime endTime) {

        Query query = entityManager.createQuery("select e.sourceIp from LogEvent e where e.accessTime between :startTime and :endTime"
        		+ " group by e.sourceIp having count(e) > :numOfVisits", String.class);
        
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        query.setParameter("numOfVisits", numOfVisits);
		
        return query.getResultList();
	}

}
