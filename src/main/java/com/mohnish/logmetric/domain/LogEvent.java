package com.mohnish.logmetric.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "logs")
public class LogEvent implements Serializable {

	private static final long serialVersionUID = -1895755581376491372L;

	@Id
	private String id;
	
	@Column
	private LocalDateTime accessTime; 
	
	@Column
	private String sourceIp;

	public LogEvent() {}
	
	public LogEvent(String id, LocalDateTime accessTime, String sourceIp) {
		this();
		this.id = id;
		this.accessTime = accessTime;
		this.sourceIp = sourceIp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(LocalDateTime accessTime) {
		this.accessTime = accessTime;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	@Override
	public String toString() {
		return "LogEvent [id=" + id + ", accessTime=" + accessTime + ", sourceIp=" + sourceIp + "]";
	}

}
