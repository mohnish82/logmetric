package com.mohnish.logmetric;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
@EnableAsync
public class LogmetricApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogmetricApplication.class, args);
	}

	@Bean
	public DataSource dataSource(Environment env) {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(env.getProperty("db.url"));
		ds.setUsername(env.getProperty("db.username"));
		ds.setPassword(env.getProperty("db.password"));
		ds.setConnectionTimeout(Integer.parseInt(env.getProperty("db.conn.timeout")));
		ds.setMaximumPoolSize(Integer.parseInt(env.getProperty("db.conn.max_pool_size")));
		
		return ds;
	}
	
    @Bean
    public TaskExecutor taskExecutor(Environment env) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(Integer.parseInt(env.getProperty("queue.capacity")));
        executor.setThreadNamePrefix("ThreadPool-");
        executor.initialize();
        
        return executor;
    }

}
