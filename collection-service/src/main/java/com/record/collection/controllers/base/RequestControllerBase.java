package com.record.collection.controllers.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.record.collection.beans.Contributor;
import com.record.collection.beans.EarningPlan;
import com.record.collection.beans.Users;
import com.record.collection.reload.ApplicationProperties;
import com.record.collection.service.ICollectionRecordService;
import com.record.collection.service.imp.CollectionRecordService.OrderBy;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;
import com.record.collection.service.imp.CollectionRecordService.QueryRestriction;
import com.record.collection.utils.Key;

public class RequestControllerBase 
{
	private static final Logger _logger = Logger.getLogger(RequestControllerBase.class.getName());
	
	protected static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	
	@Autowired
	protected ApplicationProperties _config;
	
	@Autowired
	protected ICollectionRecordService _collectionRecordService;
	
	@Value("${catalina.base}")
	protected String _catalinaBase;
	
	protected String getContributors()
	{
		String params = "";
		ArrayList<QueryParam> queryParams = new ArrayList<QueryParam>();
		queryParams.add(new QueryParam(Key.ACTIVE, true, QueryRestriction.EQ));
		
		ArrayList<?> contributerData = _collectionRecordService.getCollectionData(Contributor.class, queryParams, true);
	    if(contributerData != null && contributerData.size() > 0) {
	    	ArrayList<String> contributors = new ArrayList<String>();
	    	for(Object _contributer : contributerData) {
	    		Contributor contributor = (Contributor) _contributer;
	    		if(!contributors.contains(contributor.getDisplayName().trim()))
	    			contributors.add(contributor.getDisplayName().trim());
	    	} 
	    	
	    	Collections.sort(contributors);
	    	params += contributors.toString().replace("[", "").replace("]", "");
	    }
	    
	    return params;
	}
	
	protected String getRequiredParamter(HttpServletRequest request, String key) throws Exception
	{ 
		String value = request.getParameter(key);
		if(value == null || key.length() < 1 || "null".equalsIgnoreCase(value))
			throw new Exception("Missing Required key value - " + key);
		return value.trim();
	}
	
	protected String getParamter(HttpServletRequest request, String key) throws Exception
	{ 
		String value = request.getParameter(key);
		if(value == null || key.length() < 1 || "null".equalsIgnoreCase(value))
			return "";
		return value.trim();
	}
	
	protected void writeHttpResponse(HttpServletResponse response, HashMap<String, Object> data) throws IOException
	{
		response.setContentType("text/plain; charset=utf-8"); 
		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls();
		Gson gson = builder.create();
		String ouputJsonStr = gson.toJson(data);
		response.getWriter().print(ouputJsonStr);
		response.getWriter().flush();
		response.getWriter().close();
	}
	
	protected Users getAuthorizedUser(HttpServletRequest request) throws Exception {
		String _authorizedUser = getRequiredParamter(request, Key.AUTHORIZED_USER);
		ArrayList<?> users =  _collectionRecordService.getCollectionData(Users.class, Key.USERNAME, _authorizedUser.trim());
		if(users != null && users.size() > 0) {
			return (Users) users.get(0);
		}
		
		return null;
	}
	
	protected String getContributorsAsUrlParam()
	{
		String params = getContributors();
	    if(!StringUtils.isEmpty(params)) {
	    	params = "contributors=" + params;
	    }
	    
	    return params;
	}

	protected String getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

}
