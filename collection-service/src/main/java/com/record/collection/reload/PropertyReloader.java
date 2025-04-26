package com.record.collection.reload;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.lang3.StringUtils;

public class PropertyReloader 
{
	private static final PropertyReloader propertyReloader = new PropertyReloader();

	private static String configBasePath = "";
	private static String appContextPath = "";
	
	private static final String APPLICATION_PROPERTIES = "application.properties";
	
	private final Timer serviceScheduler = new Timer();
	
	public static PropertyReloader getInstance() {
		return propertyReloader;
	}
	
	public Configuration getConfig(String configFileName)
	{
		try { 
			return new Configurations().properties(new File(configFileName));
		} catch(Exception e) { 
			return null; 
		}
	}
	
	public void setConfigBasePath(String path) { 
		configBasePath = path + File.separator; 
	}
	
	public void setConfigBasePath(String realPath, String contextPath)
	{
		if(StringUtils.isNotEmpty(realPath) && StringUtils.isNotEmpty(contextPath) 
			&& StringUtils.isEmpty(configBasePath)) {
			configBasePath = System.getProperty("catalina.base") + File.separator + "conf" + contextPath + File.separator;
			appContextPath = contextPath;
		}
	}
	
	public void setAppPropertiesConfiguration()
	{
		Configuration config = getConfig(configBasePath + File.separator + APPLICATION_PROPERTIES);
		ApplicationProperties.getInstance().setApplicationProperties(config);
		ApplicationProperties.getInstance().setContextPath(appContextPath);
	}
	
	public void scheduleAppProperitesFileChangeObserver() {
		File configFile = new File(configBasePath + File.separator + APPLICATION_PROPERTIES);
		TimerTask task = new FileChangeObserver(configFile) {
			@Override
			protected void onChange(File configFile) {
				try {
					setAppPropertiesConfiguration();
				}catch(Exception e) { 
					e.printStackTrace(); 
				}
		    }
		};  
		
		// Schedule service to start now
		serviceScheduler.schedule(task, 0, 3000);
	}
	
	public void unscheduleAppProperitesFileChangeObserver() {
		serviceScheduler.cancel();
	}
}
