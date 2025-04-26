package com.record.collection.beans.init;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.record.collection.reload.ApplicationProperties;

@Configuration
public class BeanConfig {
	
	 @Bean
	 public DataSource getDataSource() {
		ApplicationProperties config = ApplicationProperties.getInstance();
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName(config.getProperty("hsql.driver"));
	    dataSource.setUrl(config.getProperty("hsql.jdbcUrl"));
	    dataSource.setUsername(config.getProperty("hsql.username"));
	    dataSource.setPassword(config.getProperty("hsql.password"));
	    return dataSource;
	}
	 
	@Bean
	public ApplicationProperties getApplicationProperties() {
		return ApplicationProperties.getInstance();
	}
}
