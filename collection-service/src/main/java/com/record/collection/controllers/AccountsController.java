package com.record.collection.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.record.collection.beans.Contributor;
import com.record.collection.beans.Users;
import com.record.collection.controllers.base.RequestControllerBase;
import com.record.collection.service.imp.CollectionRecordService.OrderBy;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;
import com.record.collection.service.imp.CollectionRecordService.QueryRestriction;
import com.record.collection.utils.Key;

@Controller
public class AccountsController extends RequestControllerBase {
	private static final Logger _logger = Logger.getLogger(AccountsController.class.getName());
	
	@RequestMapping(value="/addAccount", method=RequestMethod.POST)
	public void addAccount(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		Users user = new Users();
		try {
			String username = getRequiredParamter(request, Key.USERNAME);
			String password = getRequiredParamter(request, Key.PASSWORD);
			String role = getRequiredParamter(request, Key.ROLE);
			String accessLevel = getRequiredParamter(request, Key.ACCESS_LEVEL);
			
			BCryptPasswordEncoder bq = new BCryptPasswordEncoder();
			String encryptedPassword = bq.encode(password.trim());
			user.setUsername(username);
			user.setPassword(encryptedPassword);
			user.setRole(role);
			user.setAccessLevel(accessLevel);
			user.setEnabled(true);
			_collectionRecordService.saveRecord(user);
			_logger.finest("Successfully created a new account : " + username);
			data.put("success", true);
		}catch(Exception e) {
			try {
				_collectionRecordService.deleteRecord(user);
			}catch(Exception e1) { }
			data.put("success", false);
			data.put("message", "Error: unable to save a new record.");
		}
		
		writeHttpResponse(response, data);
	}

	@RequestMapping(value="/addContributor", method=RequestMethod.POST)
	public void addContributor(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		Contributor contributor = new Contributor();
		try { 
			String contributorName = getRequiredParamter(request, Key.CONTRIBUTOR_NAME);
			String contributorFullName = getRequiredParamter(request, Key.CONTRIBUTOR_FULL_NAME);
			String association = getParamter(request, Key.ASSOCIATION);
			String email = getParamter(request, Key.EMAIL);
			contributor.setContributorName(contributorName);
			contributor.setDisplayName(contributorName);
			contributor.setActive(true);
			contributor.setContributorFullName(contributorFullName);
			contributor.setAssociation(association);
			contributor.setEmail(email);
			_collectionRecordService.saveRecord(contributor);
			
			String username = getAuthenticatedUser();
			_logger.finest("Successfully added a new contributor: " + contributor.toString() + " by [" + username + "]");
			data.put("success", true);
		}catch(Exception e) {
			try {
				_collectionRecordService.deleteRecord(contributor);
			}catch(Exception e1) { }
			data.put("success", false);
			data.put("message", "Error: unable to add a new record.");
		}
		
		if((boolean) data.get("success")) {
			String contributors = getContributors();
			data.put("contributors", contributors);
		}
		
		writeHttpResponse(response, data);
	}

	@RequestMapping(value="/getContributorsInfo", method=RequestMethod.GET)
	public void getContributorsInfo(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.ACTIVE, true, QueryRestriction.GT));
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.CONTRIBUTOR_NAME, OrderBy.ASC);
		
		ArrayList<?> contributerData = _collectionRecordService.getAllData(Contributor.class, orderBy);
	    if(contributerData != null && contributerData.size() > 0) {
	    	for(Object _contributer : contributerData) {
	    		Contributor contributor = (Contributor) _contributer;
	    		HashMap<String, Object> infoRecord = new HashMap<String, Object>();
	    		infoRecord.put(Key.TABLE_ID, contributor.getId());
	    		infoRecord.put(Key.CONTRIBUTOR_NAME, contributor.getDisplayName()); // Display Name is only updatable
	    		infoRecord.put(Key.CONTRIBUTOR_FULL_NAME, contributor.getContributorFullName());
	    		infoRecord.put(Key.EMAIL, contributor.getEmail());
	    		infoRecord.put(Key.ASSOCIATION, contributor.getAssociation());
	    		infoRecord.put(Key.ACTIVE, contributor.isActive());
	    		records.add(infoRecord);
	    	} 
	    }
	    
	    data.put("data", records);
		data.put("success", true);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/updateContributorsInfo", method=RequestMethod.POST)
	public void updateContributorsInfo(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String contributorName = getRequiredParamter(request, Key.CONTRIBUTOR_NAME);
		String contributorFullName = getRequiredParamter(request, Key.CONTRIBUTOR_FULL_NAME);
		String association = getParamter(request, Key.ASSOCIATION);
		String email = getParamter(request, Key.EMAIL);
		String tableId = getParamter(request, Key.TABLE_ID);
		String isActive = getParamter(request, Key.ACTIVE);
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
		
		ArrayList<QueryParam> queryParams = new ArrayList<QueryParam>();
		queryParams.add(new QueryParam(Key.ID, tableId, QueryRestriction.EQ));
		ArrayList<?> contributerData = _collectionRecordService.getCollectionData(Contributor.class, queryParams, true);
		if(contributerData != null && contributerData.size() > 0) {
			for(Object object : contributerData) {
				Contributor contributor = (Contributor) object;
				contributor.setDisplayName(contributorName);
				contributor.setContributorFullName(contributorFullName);
				contributor.setAssociation(association);
				contributor.setEmail(email);
				contributor.setActive("true".equalsIgnoreCase(isActive));

				HashMap<String, Object> infoRecord = new HashMap<String, Object>();
				infoRecord.put(Key.TABLE_ID, contributor.getId());
	    		infoRecord.put(Key.CONTRIBUTOR_NAME, contributor.getDisplayName());// Display Name is only updatable
	    		infoRecord.put(Key.CONTRIBUTOR_FULL_NAME, contributor.getContributorFullName());
	    		infoRecord.put(Key.EMAIL, contributor.getEmail());
	    		infoRecord.put(Key.ASSOCIATION, contributor.getAssociation());
	    		infoRecord.put(Key.ACTIVE, contributor.isActive());
	    		records.add(infoRecord);
				_collectionRecordService.updateRecord(contributor);
			}
		}
		
		data.put("data", records);
		data.put("success", true);
		writeHttpResponse(response, data);
	}
}
