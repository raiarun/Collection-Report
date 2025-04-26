package com.record.collection.reload;

import org.apache.commons.configuration2.Configuration;

public class ApplicationProperties 
{
	private static final ApplicationProperties appProperties = new ApplicationProperties();
	
	private Configuration configuration;
	private String contextPath;
	
	public static ApplicationProperties getInstance() {
		synchronized(appProperties) {
			return appProperties;
		}
	}
	
	public void setApplicationProperties(Configuration configuration) {
		synchronized(appProperties) {
			this.configuration = configuration;
		}
	}
	
	public void setContextPath(String contextPath) {
		synchronized(appProperties) {
			this.contextPath = contextPath;
		}
	}
	
	public String getRequiredProperty(String key) {
		synchronized(appProperties) {
			return this.configuration != null ? this.configuration.getString(key) : "";
		}
	}
	
	public String getProperty(String key) {
		synchronized(appProperties) {
			return this.configuration != null ? this.configuration.getString(key) : "";
		}
	}
	
	public String getContextPath() {
		synchronized(appProperties) {
			return this.contextPath;
		}
	}
}
