package com.record.collection.reload;

import java.io.File;
import java.util.TimerTask;

public abstract class FileChangeObserver extends TimerTask 
{
	private long lastTimeStamp;
	private File configFile;

	public FileChangeObserver(File configFile) 
	{
		this.configFile = configFile;
		this.lastTimeStamp = configFile.lastModified();
	}

	@Override
	public void run() 
	{
	    long lastModified = configFile.lastModified();
	    if(this.lastTimeStamp != lastModified) 
	    {
	    	this.lastTimeStamp = lastModified;
	    	onChange(configFile);
	    }
	    
	}

	protected abstract void onChange(File configFile);
}