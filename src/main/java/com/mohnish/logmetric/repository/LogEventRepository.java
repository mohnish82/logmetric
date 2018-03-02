package com.mohnish.logmetric.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.mohnish.logmetric.domain.LogEvent;

public interface LogEventRepository extends CrudRepository<LogEvent, Serializable>, LogEventRepositoryCustom {

}
