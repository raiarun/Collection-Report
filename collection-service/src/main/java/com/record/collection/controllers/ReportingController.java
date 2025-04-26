package com.record.collection.controllers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.record.collection.beans.Budget;
import com.record.collection.beans.CollectionInfo;
import com.record.collection.beans.ExpenseInfo;
import com.record.collection.controllers.base.RequestControllerBase;
import com.record.collection.service.imp.CollectionRecordService.OrderBy;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;
import com.record.collection.service.imp.CollectionRecordService.QueryRestriction;
import com.record.collection.utils.Key;

@Controller
public class ReportingController extends RequestControllerBase 
{
	@RequestMapping(value="/getChartsData", method=RequestMethod.GET)
	public void getChartsData(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		HashMap<String, Object> records = new HashMap<String, Object>();
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		
		String year = getParamter(request, Key.BUDGET_YEAR);
		String _startDate = year + "/01/01";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		String _endDate =  getFormattedDate(cal);
		
		Date startDate = null, endDate = null;
		SimpleDateFormat sd = new SimpleDateFormat(dateTimeFormat);
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.EXPENSE_DATE, OrderBy.ASC);
		
		if(!StringUtils.isEmpty(year)) {
			queryParms.add(new QueryParam(Key.EXPENSE_YEAR, Integer.valueOf(year), QueryRestriction.EQ));
		}else {
			_startDate = getRequiredParamter(request, Key.START_DATE);
			_endDate = getRequiredParamter(request, Key.END_DATE);
			startDate = sd.parse(_startDate);
			endDate = sd.parse(_endDate);
			cal = Calendar.getInstance();
			cal.setTime(endDate);
			cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
			
			if(startDate.after(endDate)) {
				data.put("success", false);
				data.put("message", "Error: the start date is after the end date.");
				writeHttpResponse(response, data);
				return;
			}
			
			queryParms.add(new QueryParam(Key.EXPENSE_DATE, startDate, QueryRestriction.GT));
			queryParms.add(new QueryParam(Key.EXPENSE_DATE, endDate, QueryRestriction.LT));
		}
		
		ArrayList<?> expenseData = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true, orderBy);
		double totalExpenses = 0.0;
		HashMap<String, Double> itemizedExpenses = new HashMap<String, Double>();
		HashMap<String, Double> groupExpenses = new HashMap<String, Double>();
		if(expenseData != null && expenseData.size() > 0) {
			if(!StringUtils.isEmpty(year)) {
				startDate = ((ExpenseInfo) expenseData.get(0)).getExpenseDate();
				endDate = ((ExpenseInfo) expenseData.get(expenseData.size() - 1)).getExpenseDate();
				
				cal = Calendar.getInstance();
				cal.setTime(startDate);
				_startDate = getFormattedDate(cal);
				cal.setTime(endDate);
				_endDate =  getFormattedDate(cal);
			}
			
			 for(Object expense : expenseData) {
				 ExpenseInfo expenseInfo = (ExpenseInfo) expense;
				 if(!StringUtils.isEmpty(expenseInfo.getExpenseType())) {
					 if(itemizedExpenses.containsKey(expenseInfo.getExpenseType().trim())) {
						 double expenseAmount = itemizedExpenses.get(expenseInfo.getExpenseType().trim());
						 itemizedExpenses.put(expenseInfo.getExpenseType().trim(), expenseAmount + expenseInfo.getAmount());
					 }else {
						 itemizedExpenses.put(expenseInfo.getExpenseType().trim(), expenseInfo.getAmount());
					 }
					 totalExpenses += expenseInfo.getAmount();
				 }
				 
				 if(!StringUtils.isEmpty(expenseInfo.getExpenseGroup())) {
					 if(groupExpenses.containsKey(expenseInfo.getExpenseGroup().trim())) {
						 double expenseAmount = groupExpenses.get(expenseInfo.getExpenseGroup().trim());
						 groupExpenses.put(expenseInfo.getExpenseGroup().trim(), expenseAmount + expenseInfo.getAmount());
					 }else {
						 groupExpenses.put(expenseInfo.getExpenseGroup().trim(), expenseInfo.getAmount());
					 }
				 }
			 }
		}
		
		records.put("itemizedExpenses", itemizedExpenses);
		records.put("groupExpenses", groupExpenses);
		
		double totalIncome = 0.0;
		HashMap<String, Double> collectionRecord = new HashMap<String, Double>();
		queryParms = new ArrayList<QueryParam>();
		if(!StringUtils.isEmpty(year)) {
			queryParms.add(new QueryParam(Key.COLLECTION_YEAR, Integer.valueOf(year), QueryRestriction.EQ));
		}else {
			queryParms.add(new QueryParam(Key.COLLECTION_DATETIME, startDate, QueryRestriction.GT));
			queryParms.add(new QueryParam(Key.COLLECTION_DATETIME, endDate, QueryRestriction.LT));
		}
		
		ArrayList<?> collectionData = _collectionRecordService.getCollectionData(CollectionInfo.class, queryParms, true);
		if(collectionData != null && collectionData.size() > 0) {
			for(Object collection : collectionData) {
				CollectionInfo collectionInfo = (CollectionInfo) collection;
				if(collectionRecord.containsKey(collectionInfo.getCollectionType().trim())) {
					 double expenseAmount = collectionRecord.get(collectionInfo.getCollectionType().trim());
					 collectionRecord.put(collectionInfo.getCollectionType().trim(), expenseAmount + collectionInfo.getAmount());
				 }else {
					 collectionRecord.put(collectionInfo.getCollectionType().trim(), collectionInfo.getAmount());
				 }
				totalIncome += collectionInfo.getAmount();
			}
		}
		
		records.put("income", collectionRecord);
		records.put("totalIncome", totalIncome);
		records.put("totalExpenses", totalExpenses);
		records.put("dateRange", chartDate(_startDate) + " - " + chartDate(_endDate));
		
		data.put("data", records);
		data.put("success", true);
		writeHttpResponse(response, data);
	}
	
	private String chartDate(String date) {
		if(date.split(" ").length < 2)
			return date;
		String dateElems = date.split(" ")[0];
		String finalDate = dateElems.split("-")[1] + "/" + dateElems.split("-")[2] + "/" + dateElems.split("-")[0];
		return finalDate;
	}
	
	private String getFormattedDate(Calendar cal) {
		return cal.get(Calendar.YEAR) + "/" 
				+ (cal.get(Calendar.MONTH) < 10 
						? ("0" + cal.get(Calendar.MONTH)) 
								: cal.get(Calendar.MONTH)) 
				+ (cal.get(Calendar.DAY_OF_MONTH) < 10 
						? ("0" + cal.get(Calendar.DAY_OF_MONTH)) 
								: cal.get(Calendar.DAY_OF_MONTH));
	}
	
	@RequestMapping(value="/getBudgetExpenseData", method=RequestMethod.GET)
	public void getBudgetExpenseData(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> records = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, LinkedHashMap<String, Object>> budgetGroupExpenses = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		LinkedHashMap<String, LinkedHashMap<String, Object>> budgetTypeExpenses = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		
		String budgetYear = getRequiredParamter(request, Key.BUDGET_YEAR);
		BudgetInfo budgetInfo = getBudgetInfo(Integer.valueOf(budgetYear));
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.EXPENSE_YEAR, Integer.valueOf(budgetYear), QueryRestriction.EQ));
		queryParms.add(new QueryParam(Key.STATUS, ExpenseReportController.STATUS_ACKNOWLEDGED, QueryRestriction.EQ));
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.EXPENSE_GROUP, OrderBy.ASC);
		
		ArrayList<?> expenseInfo = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms,  true, orderBy);
		
		queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.COLLECTION_YEAR, Integer.valueOf(budgetYear), QueryRestriction.EQ));
		
		orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.BUDGET_CATEGORY_TYPE, OrderBy.ASC);
		
		ArrayList<?> collections = _collectionRecordService.getCollectionData(CollectionInfo.class, queryParms,  true, orderBy);
		
		HashMap<String, Double> budgetCategoryTypes = new HashMap<String, Double>();
		
		queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.EXPENSE_YEAR, Integer.valueOf(budgetYear), QueryRestriction.EQ));
		ArrayList<?> expenseData = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true);
		double totalExpenses = 0.00;
		if(expenseData != null && expenseData.size() > 0) {
			for(Object expense : expenseData) {
				 ExpenseInfo _expenseInfo = (ExpenseInfo) expense;
				 if(!StringUtils.isEmpty(_expenseInfo.getExpenseType())) {
					 totalExpenses += _expenseInfo.getAmount();
				 }
			 }
		}
		
		DecimalFormat f = new DecimalFormat("##.00");
		totalExpenses = Double.valueOf(f.format(totalExpenses));
		if(collections != null && collections.size() > 0){
			for(Object object : collections) {
				CollectionInfo collection = (CollectionInfo) object;
				if(budgetCategoryTypes.get(collection.getBudgetCategoryType()) == null) {
					//TODO: find a way to avoid checking hard coded value
					if(collection.getBudgetCategoryType() != null && collection.getBudgetCategoryType().toLowerCase().contains("saving")) {
						budgetCategoryTypes.put(collection.getBudgetCategoryType(), totalExpenses*(-1.00));
					}else {
						budgetCategoryTypes.put(collection.getBudgetCategoryType(), 0.00);
					}
				}
				
				double amount = budgetCategoryTypes.get(collection.getBudgetCategoryType())  + collection.getAmount();
				budgetCategoryTypes.put(collection.getBudgetCategoryType(), Double.valueOf(f.format(amount)));
			}
		}
		
		//TODO: find a way to replace implementation below. I did it just to make it work, did not have 
		// time to put effort on doing it a better way
		double actualExpenses = 0;
		if(expenseInfo != null && expenseInfo.size() > 0) {
			for(Object object : expenseInfo) {
				ExpenseInfo expense = (ExpenseInfo) object;
				actualExpenses += expense.getAmount();
				if(budgetGroupExpenses.get(expense.getExpenseGroup()) == null) {
					budgetGroupExpenses.put(expense.getExpenseGroup(), new LinkedHashMap<String, Object>());
					budgetGroupExpenses.get(expense.getExpenseGroup()).put("plan", 
							budgetInfo.getBudgetGroupsValue().get(expense.getExpenseGroup()));
					budgetGroupExpenses.get(expense.getExpenseGroup()).put("actual", expense.getAmount());
				}else {
					budgetGroupExpenses.get(expense.getExpenseGroup()).put("actual", 
							(double) budgetGroupExpenses.get(expense.getExpenseGroup()).get("actual") + expense.getAmount());
				}
				
				if(budgetTypeExpenses.get(expense.getExpenseType()) == null) {
					budgetTypeExpenses.put(expense.getExpenseType(), new LinkedHashMap<String, Object>());
					budgetTypeExpenses.get(expense.getExpenseType()).put("plan", 
							budgetInfo.getBudgetTypesValue().get(expense.getExpenseType()));
					budgetTypeExpenses.get(expense.getExpenseType()).put("actual", expense.getAmount());
					
					double value = 0.0;
					if(budgetInfo.getBudgetTypesValue().get(expense.getExpenseType()) != null) {
						value = (double) budgetInfo.getBudgetTypesValue().get(expense.getExpenseType());
					}
					
					budgetTypeExpenses.get(expense.getExpenseType()).put("difference", value - expense.getAmount());
				}else {
					double amount = 0.0;
					if(budgetTypeExpenses.get(expense.getExpenseType()).get("actual") != null) {
						amount = (double) budgetTypeExpenses.get(expense.getExpenseType()).get("actual");
					}
					double value = 0.0;
					if(budgetInfo.getBudgetTypesValue().get(expense.getExpenseType()) != null) {
						value = (double) budgetInfo.getBudgetTypesValue().get(expense.getExpenseType());
					}
					budgetTypeExpenses.get(expense.getExpenseType()).put("actual", amount + expense.getAmount());
					budgetTypeExpenses.get(expense.getExpenseType()).put("difference", value - (amount + expense.getAmount()));
				}
				
			}
		}
		
		for(Map.Entry<String, Double> entry : budgetCategoryTypes.entrySet()) {
			if(budgetTypeExpenses.get(entry.getKey()) == null) {
				budgetTypeExpenses.put(entry.getKey(), new LinkedHashMap<String, Object>());
				budgetTypeExpenses.get(entry.getKey()).put("plan", 
						budgetInfo.getBudgetTypesValue().get(entry.getKey()));
				budgetTypeExpenses.get(entry.getKey()).put("actual", entry.getValue());
				double value = 0.0;
				if(budgetInfo.getBudgetTypesValue().get(entry.getKey()) != null) {
					value = (double) budgetInfo.getBudgetTypesValue().get(entry.getKey());
				}
				budgetTypeExpenses.get(entry.getKey()).put("difference",value - entry.getValue());
			}else {
				double amount = 0.0;
				if(budgetTypeExpenses.get(entry.getKey()).get("actual") != null) {
					amount = (double) budgetTypeExpenses.get(entry.getKey()).get("actual");
				}
				
				double value = 0.0;
				if(budgetInfo.getBudgetTypesValue().get(entry.getKey()) != null) {
					value = (double) budgetInfo.getBudgetTypesValue().get(entry.getKey());
				}
				
				budgetTypeExpenses.get(entry.getKey()).put("actual", amount + entry.getValue());
				budgetTypeExpenses.get(entry.getKey()).put("difference", value - (amount + entry.getValue()));
			}
		}
		
		for(Map.Entry<String, Object> entry : budgetInfo.getBudgetTypesValue().entrySet()) {
			if(budgetTypeExpenses.get(entry.getKey()) == null) {
				budgetTypeExpenses.put(entry.getKey(), new LinkedHashMap<String, Object>());
				budgetTypeExpenses.get(entry.getKey()).put("group", budgetInfo.getBudgetTypeGroupValue().get(entry.getKey()));
				budgetTypeExpenses.get(entry.getKey()).put("plan", budgetInfo.getBudgetTypesValue().get(entry.getKey()));
				budgetTypeExpenses.get(entry.getKey()).put("actual", 0.0);
				budgetTypeExpenses.get(entry.getKey()).put("difference", budgetInfo.getBudgetTypesValue().get(entry.getKey()));
			}else {
				budgetTypeExpenses.get(entry.getKey()).put("group", budgetInfo.getBudgetTypeGroupValue().get(entry.getKey()));
			}
		}
		
		
		List<Map.Entry<String, LinkedHashMap<String, Object>> > list = new LinkedList<Map.Entry<String, LinkedHashMap<String, Object>> >(budgetTypeExpenses.entrySet()); 
		
		Collections.sort(list, new Comparator<Map.Entry<String, LinkedHashMap<String, Object>> >() { 
            public int compare(Map.Entry<String, LinkedHashMap<String, Object>> o1,  Map.Entry<String, LinkedHashMap<String, Object>> o2) { 
                return ((String) o1.getValue().get("group")).compareTo((String) o2.getValue().get("group")); 
            } 
        }); 
		
		budgetTypeExpenses = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		for(Map.Entry<String, LinkedHashMap<String, Object>> entry : list) {
			budgetTypeExpenses.put(entry.getKey(), entry.getValue());
		}
		
		records.put("totalBudgetAmount", budgetInfo.totalBudget);
		records.put("totalExpenseAmount", actualExpenses);
		records.put("budgetGroupExpenses", budgetGroupExpenses);
		records.put("budgetTypeExpenses", budgetTypeExpenses);
		
		data.put("data", records);
		data.put("budgetExpenseYear", budgetYear);
		data.put("success", true);
		writeHttpResponse(response, data);
	}
	
	private BudgetInfo getBudgetInfo(int year)
	{
		BudgetInfo budgetInfo = new BudgetInfo();
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.BUDGET_YEAR, Integer.valueOf(year), QueryRestriction.EQ));
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.BUDGET_GROUP, OrderBy.ASC);
		
		ArrayList<?> budgets = _collectionRecordService.getCollectionData(Budget.class, queryParms,  true, orderBy);
		
		budgetInfo.totalBudget = 0;
		if(budgets != null && budgets.size() > 0) {
			for(Object object : budgets) {
				Budget budget = (Budget) object;
				if(budgetInfo.getBudgetGroupsValue().get(budget.getBudgetGroup()) == null) {
					budgetInfo.getBudgetGroupsValue().put(budget.getBudgetGroup(), budget.getAmount());
				}else {
					budgetInfo.getBudgetGroupsValue().put(budget.getBudgetGroup(), (double) budgetInfo.getBudgetGroupsValue().get(budget.getBudgetGroup()) + (int) budget.getAmount());
				}
				
				if(budgetInfo.getBudgetTypesValue().get(budget.getBudgetType()) == null) {
					budgetInfo.getBudgetTypesValue().put(budget.getBudgetType(), budget.getAmount());
				}else {
					budgetInfo.getBudgetTypesValue().put(budget.getBudgetType(), (double) budgetInfo.getBudgetTypesValue().get(budget.getBudgetType()) + (int) budget.getAmount());
				}
				
				budgetInfo.getBudgetTypeGroupValue().put(budget.getBudgetType(), budget.getBudgetGroup());
				budgetInfo.totalBudget += budget.getAmount();
			}
		}
		
		return budgetInfo;
	}
	
	private class BudgetInfo
	{
		double totalBudget;
		LinkedHashMap<String, Object> groups;
		LinkedHashMap<String, Object> types;
		LinkedHashMap<String, Object> typeGroups;
		
		BudgetInfo(){
			groups = new LinkedHashMap<String, Object>();
			types = new LinkedHashMap<String, Object>();
			typeGroups = new LinkedHashMap<String, Object>();
		}
		
		public LinkedHashMap<String, Object> getBudgetGroupsValue(){
			return groups; 
		}
		
		public LinkedHashMap<String, Object> getBudgetTypeGroupValue(){
			return typeGroups; 
		}
		
		
		public LinkedHashMap<String, Object> getBudgetTypesValue(){
			return types;
		}
	}
	
}
