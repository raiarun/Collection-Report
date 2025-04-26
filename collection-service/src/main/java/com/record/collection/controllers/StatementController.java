package com.record.collection.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.record.collection.beans.CollectionInfo;
import com.record.collection.controllers.base.RequestControllerBase;
import com.record.collection.service.imp.CollectionRecordService.OrderBy;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;
import com.record.collection.service.imp.CollectionRecordService.QueryRestriction;
import com.record.collection.utils.Key;

@Controller
public class StatementController extends RequestControllerBase {
	private static final Logger _logger = Logger.getLogger(StatementController.class.getName());
	
	@RequestMapping(value="/getStatementInfo", method=RequestMethod.GET)
	public void getStatementInfo(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		String budgetYear = getRequiredParamter(request, Key.BUDGET_YEAR);
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.COLLECTION_YEAR, Integer.valueOf(budgetYear), QueryRestriction.EQ));
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.CONTRIBUTOR, OrderBy.ASC);
		
		ArrayList<?> collectionData = _collectionRecordService.getCollectionData(CollectionInfo.class, queryParms, true, orderBy);
		
		HashMap<String, HashMap<String, Object>> mapper = new HashMap<String, HashMap<String, Object>>();
		
		double total = 0;
		if(collectionData != null && collectionData.size() > 0) {
			 for(Object collection : collectionData) {
				 CollectionInfo collectionInfo = (CollectionInfo) collection;
				 if(!mapper.containsKey(collectionInfo.getContributor().getDisplayName())) {
					 mapper.put(collectionInfo.getContributor().getDisplayName(), new HashMap<String, Object>());
					 mapper.get(collectionInfo.getContributor().getDisplayName())
					 .put("name", collectionInfo.getContributor().getDisplayName());
					 mapper.get(collectionInfo.getContributor().getDisplayName())
					 .put("fullName", collectionInfo.getContributor().getContributorFullName());
					 mapper.get(collectionInfo.getContributor().getDisplayName())
					 .put("amount", collectionInfo.getAmount());
					 mapper.get(collectionInfo.getContributor().getDisplayName())
					 .put("email", collectionInfo.getContributor().getEmail());
					 
				 }else {
					 mapper.get(collectionInfo.getContributor().getDisplayName())
					 .put("amount", (double) mapper.get(collectionInfo.getContributor().getDisplayName()).get("amount") + collectionInfo.getAmount());
				 }
				 
				 total += collectionInfo.getAmount();
			 }
		}
		
		data.put("data", mapper);
		data.put("success", true);
		data.put("total", total);
		writeHttpResponse(response, data);
	}
}
