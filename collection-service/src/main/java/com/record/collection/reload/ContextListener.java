package com.record.collection.reload;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {

	private static final Logger _logger = Logger.getLogger(ContextListener.class.getName());
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		_logger.log(Level.FINEST, "Listening context .......");
		String contextPath = sce.getServletContext().getContextPath();
		String realPath = sce.getServletContext().getRealPath("");
		PropertyReloader.getInstance().setConfigBasePath(realPath, contextPath);
		PropertyReloader.getInstance().setAppPropertiesConfiguration();
		PropertyReloader.getInstance().scheduleAppProperitesFileChangeObserver();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		_logger.log(Level.FINEST, "Destroying context .......");
		PropertyReloader.getInstance().unscheduleAppProperitesFileChangeObserver();
	}

}
