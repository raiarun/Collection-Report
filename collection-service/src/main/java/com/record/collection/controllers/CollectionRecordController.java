package com.record.collection.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.record.collection.beans.CollectionInfo;
import com.record.collection.beans.Contributor;
import com.record.collection.beans.Users;
import com.record.collection.controllers.base.RequestControllerBase;
import com.record.collection.service.imp.CollectionRecordService.OrderBy;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;
import com.record.collection.service.imp.CollectionRecordService.QueryRestriction;
import com.record.collection.utils.Key;

@Controller
public class CollectionRecordController extends RequestControllerBase
{
	private static final Logger _logger = Logger.getLogger(CollectionRecordController.class.getName());

	@RequestMapping(value="/saveCollection", method=RequestMethod.POST)
	public void saveCollection(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		CollectionInfo collectionInfo = new CollectionInfo();
		try {
			String contributionType = getRequiredParamter(request, Key.COLLECTION_TYPE);
			String contributor = getRequiredParamter(request, Key.CONTRIBUTOR);
			String collectionType = getRequiredParamter(request, Key.COLLECTION_TYPE);
			String fundPaymentMethod = getRequiredParamter(request, Key.FUND_PAYMENT_METHOD);
			String amount = getRequiredParamter(request, Key.AMOUNT);
			String collectionYear = getRequiredParamter(request, Key.COLLECTION_YEAR);
			String collectionMonth = getRequiredParamter(request, Key.COLLECTION_MONTH);
			String budgetCategoryType = getRequiredParamter(request, Key.BUDGET_CATEGORY_TYPE);
			String note = getParamter(request, Key.NOTE);
			String reportedBy = getParamter(request, Key.REPORTED_BY);
			
			collectionInfo.setContributionType(contributionType);
			ArrayList<?> contributerData = _collectionRecordService.getCollectionData(Contributor.class, Key.CONTRIBUTOR_NAME, contributor);
			
			if(contributerData != null && contributerData.size() > 0) {
				collectionInfo.setContributor((Contributor) contributerData.get(0));
			}
			
			SimpleDateFormat sd = new SimpleDateFormat (dateTimeFormat);
			Date collectionDateTime = sd.parse(getRequiredParamter(request, Key.COLLECTION_DATETIME));
			collectionInfo.setCollectionDateTime(collectionDateTime);
			
			collectionInfo.setCollectionType(collectionType);
			collectionInfo.setFundPaymentMethod(fundPaymentMethod);
			collectionInfo.setAmount(Double.valueOf(amount));
			collectionInfo.setCollectionYear(Integer.valueOf(collectionYear));
			collectionInfo.setCollectionMonth(collectionMonth);
			collectionInfo.setCreatedDate(new Date());
			collectionInfo.setUpdatedDate(new Date());
			collectionInfo.setBudgetCategoryType(budgetCategoryType);
			collectionInfo.setNote(note);
			collectionInfo.setReportedBy(reportedBy);

			String username = getAuthenticatedUser();
			ArrayList<?> users =  _collectionRecordService.getCollectionData(Users.class, Key.USERNAME, username);
			if(users != null && users.size() > 0) {
				collectionInfo.setCreatedBy((Users) users.get(0));
			}
			_collectionRecordService.saveRecord(collectionInfo);
			_logger.finest("Successfully saved a record: " + collectionInfo.toString() + " by [" + username + "]");
			data.put("success", true);
		}catch(Exception e) {
			try {
				_collectionRecordService.deleteRecord(collectionInfo);
			}catch(Exception e1) { }
			data.put("success", false);
			data.put("message", "Error: unable to save a new record.");
		}
		
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/getRecords", method=RequestMethod.GET)
	public void getRecords(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String key = getRequiredParamter(request, Key.REQUEST_TYPE);
		String value = getRequiredParamter(request, Key.VALUE);
		ArrayList<?> collectionInfo = _collectionRecordService.getCollectionData(CollectionInfo.class, key, value);
		HashMap<String, Object> data = new HashMap<String, Object>();
		ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
		double total = 0;
		if(collectionInfo != null && collectionInfo.size() > 0)
		{
			for(Object _record : collectionInfo) {
				CollectionInfo record = (CollectionInfo) _record;
				HashMap<String, String> infoRecord = new HashMap<String, String>();
				infoRecord.put(Key.CONTRIBUTION_TYPE, record.getContributionType());
				infoRecord.put(Key.FUND_PAYMENT_METHOD, record.getFundPaymentMethod());
				infoRecord.put(Key.CONTRIBUTOR, record.getContributor().getDisplayName());
				infoRecord.put(Key.COLLECTION_TYPE, record.getCollectionType());
				infoRecord.put(Key.CONTRIBUTOR_FULL_NAME, record.getContributor().getContributorFullName());
				infoRecord.put(Key.AMOUNT, String.valueOf(record.getAmount()));
				infoRecord.put(Key.COLLECTION_YEAR, String.valueOf(record.getCollectionYear()));
				infoRecord.put(Key.COLLECTION_MONTH, record.getCollectionMonth());
				infoRecord.put(Key.NOTE, record.getNote());
				records.add(infoRecord);
				total += record.getAmount();
			}
			
		}
		
		data.put("data", records);
		data.put("success", true);
		data.put("total", total);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/getCollectionEntries", method=RequestMethod.GET)
	public void getCollectionEntries(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String requestDate = getRequiredParamter(request, Key.COLLECTION_DATE);
		SimpleDateFormat sd = new SimpleDateFormat (dateTimeFormat);
		
		Date startDate = sd.parse(requestDate + " 00:00:00");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.COLLECTION_YEAR, cal.get(Calendar.YEAR), QueryRestriction.EQ));
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
		ArrayList<?> collectionData = _collectionRecordService.getCollectionData(CollectionInfo.class, queryParms, true);
		
		double total = 0;
		if(collectionData != null && collectionData.size() > 0) {
			 for(Object collection : collectionData) {
				 CollectionInfo collectionInfo = (CollectionInfo) collection;
				 String collectionDate = sd.format(collectionInfo.getCollectionDateTime());
				 if(collectionDate.contains(requestDate)){
					HashMap<String, String> infoRecord = new HashMap<String, String>();
					infoRecord.put(Key.CONTRIBUTION_TYPE, collectionInfo.getContributionType());
					infoRecord.put(Key.FUND_PAYMENT_METHOD, collectionInfo.getFundPaymentMethod());
					infoRecord.put(Key.CONTRIBUTOR, collectionInfo.getContributor().getContributorName());
					infoRecord.put(Key.AMOUNT, String.valueOf(collectionInfo.getAmount()));
					infoRecord.put(Key.COLLECTION_DATETIME, collectionDate);
					infoRecord.put(Key.CREATED_DATE, collectionInfo.getCreatedDate().toString());
					infoRecord.put(Key.REPORTED_BY, collectionInfo.getReportedBy());
					infoRecord.put(Key.NOTE, collectionInfo.getNote());
					records.add(infoRecord);
					total += collectionInfo.getAmount();
				 }
			 }
		 }
		
		data.put("data", records);
		data.put("success", true);
		data.put("total", total);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/getCollectionReport", method=RequestMethod.GET)
	public void getCollectionReport(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		String contributor = getRequiredParamter(request, Key.CONTRIBUTOR);
		String collectionType = getRequiredParamter(request, Key.COLLECTION_TYPE);
		SimpleDateFormat sd = new SimpleDateFormat (dateTimeFormat);
		Date startDate = sd.parse(getRequiredParamter(request, Key.START_DATE));
		Date endDate = sd.parse(getRequiredParamter(request, Key.END_DATE));
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.add(Calendar.DATE, 1);
		endDate = cal.getTime();
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		if(startDate.after(endDate)) {
			data.put("success", false);
			data.put("message", "Error: the start date is after the end date.");
			writeHttpResponse(response, data);
			return;
		}
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.CREATED_DATE, startDate, QueryRestriction.GT));
		queryParms.add(new QueryParam(Key.CREATED_DATE, endDate, QueryRestriction.LT));
		
		if(!StringUtils.isEmpty(collectionType) && !"ALL".equalsIgnoreCase(collectionType))
			queryParms.add(new QueryParam(Key.COLLECTION_TYPE, collectionType, QueryRestriction.EQ));
		if(!StringUtils.isEmpty(contributor) && !"ALL".equalsIgnoreCase(contributor)) {
			ArrayList<?> contributors = _collectionRecordService.getCollectionData(Contributor.class, Key.CONTRIBUTOR_NAME, contributor);
			queryParms.add(new QueryParam(Key.CONTRIBUTOR, contributors.get(0), QueryRestriction.EQ));
		}
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.CONTRIBUTION_TYPE, OrderBy.ASC);
		
		ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
		ArrayList<?> collectionData = _collectionRecordService.getCollectionData(CollectionInfo.class, queryParms, true, orderBy);
		double total = 0;
		if(collectionData != null && collectionData.size() > 0) {
			 boolean canSeeContributors = "true".equalsIgnoreCase(getParamter(request, "canSeeContributors"));
			 for(Object collection : collectionData) {
				 CollectionInfo collectionInfo = (CollectionInfo) collection;
				 HashMap<String, String> infoRecord = new HashMap<String, String>();
				 infoRecord.put(Key.CONTRIBUTION_TYPE, collectionInfo.getContributionType());
				 infoRecord.put(Key.FUND_PAYMENT_METHOD, collectionInfo.getFundPaymentMethod());
				 if(canSeeContributors)
					 infoRecord.put(Key.CONTRIBUTOR, collectionInfo.getContributor().getContributorName());
				 else
					 infoRecord.put(Key.CONTRIBUTOR, collectionInfo.getContributor().getAssociation());
				 infoRecord.put(Key.AMOUNT, String.valueOf(collectionInfo.getAmount()));
				 infoRecord.put(Key.COLLECTION_DATETIME, collectionInfo.getCollectionDateTime().toString().split(" ")[0]);
				 infoRecord.put(Key.CREATED_DATE, collectionInfo.getCreatedDate().toString());
				 infoRecord.put(Key.REPORTED_BY, collectionInfo.getReportedBy());
				 infoRecord.put(Key.NOTE, collectionInfo.getNote());
				 records.add(infoRecord);
				 total += collectionInfo.getAmount();
			 }
		 }
		
		data.put("data", records);
		data.put("success", true);
		data.put("total", total);
		writeHttpResponse(response, data);
	}
	
}
