package com.record.collection.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.record.collection.beans.Budget;
import com.record.collection.beans.CollectionInfo;
import com.record.collection.beans.EarningPlan;
import com.record.collection.beans.ExpenseInfo;
import com.record.collection.controllers.base.RequestControllerBase;
import com.record.collection.service.imp.CollectionRecordService.OrderBy;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;
import com.record.collection.service.imp.CollectionRecordService.QueryRestriction;
import com.record.collection.utils.Key;

@Controller
public class BudgetRecordController extends RequestControllerBase
{
	private static final Logger _logger = Logger.getLogger(BudgetRecordController.class.getName());
	
	@RequestMapping(value="/lookupEarningPlan", method=RequestMethod.GET)
	public void lookupEarningPlan(HttpServletRequest request, HttpServletResponse response) throws Exception
	{  
		String earningYear = getRequiredParamter(request, Key.EARNING_YEAR);
		LinkedHashMap<String, Object> data = budgetData(earningYear);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/removeBudget", method=RequestMethod.POST)
	public void removeBudget(HttpServletRequest request, HttpServletResponse response) throws Exception
	{ 
		String budgetYear = getRequiredParamter(request, Key.BUDGET_YEAR);
		String id = getParamter(request, Key.TABLE_ID);
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();

		if(id != null && id.length() > 0) {
			queryParms.add(new QueryParam(Key.ID, id, QueryRestriction.EQ));
			ArrayList<?> budgetsInfo = _collectionRecordService.getCollectionData(Budget.class, queryParms, true);
			if(budgetsInfo != null && budgetsInfo.size() > 0) {
				Budget budget = (Budget) budgetsInfo.get(0);
				_collectionRecordService.deleteRecord(budget);
			}
		} 
		
		LinkedHashMap<String, Object> data = budgetData(budgetYear);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/getBudgetPlanData", method=RequestMethod.GET)
	public void getBudgetPlanData(HttpServletRequest request, HttpServletResponse response) throws Exception
	{  
		String budgetYear = getRequiredParamter(request, Key.BUDGET_YEAR);
		LinkedHashMap<String, Object> data = budgetData(budgetYear);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/copyPreviousYearPlan", method=RequestMethod.POST)
	public void copyPreviousYearPlan(HttpServletRequest request, HttpServletResponse response) throws Exception
	{  
		String budgetYear = getRequiredParamter(request, Key.BUDGET_YEAR);
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		
		queryParms.add(new QueryParam(Key.EARNING_YEAR, Integer.valueOf(budgetYear) - 1, QueryRestriction.EQ));
		orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
		
		ArrayList<?> earningPlanData = _collectionRecordService.getCollectionData(EarningPlan.class, queryParms, true, orderBy);
		if(earningPlanData != null && earningPlanData.size() > 0) {
			for(Object object : earningPlanData) {
				EarningPlan earningPlan = (EarningPlan) object;
				EarningPlan _earningPlan = new EarningPlan();
				try {
					_earningPlan.setEstimatedAmount(earningPlan.getEstimatedAmount());
					_earningPlan.setEarningYear(Integer.valueOf(budgetYear));
					_earningPlan.setCreatedDate(new Date());
					_earningPlan.setUpdatedDate(new Date());
					_earningPlan.setDescription(earningPlan.getDescription());
					_earningPlan.setCreatedBy(getAuthorizedUser(request));
					_collectionRecordService.saveRecord(_earningPlan);
				}catch(Exception e) {
					try {
						_collectionRecordService.deleteRecord(_earningPlan);
					}catch(Exception e1) {}
				}
			}
		}
		
		queryParms = new ArrayList<QueryParam>();
		orderBy = new LinkedHashMap<String, OrderBy>();
		queryParms.add(new QueryParam(Key.BUDGET_YEAR, Integer.valueOf(budgetYear) - 1, QueryRestriction.EQ));
		orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
		orderBy.put(Key.BUDGET_GROUP, OrderBy.ASC);
		
		ArrayList<?> budgetInfo = _collectionRecordService.getCollectionData(Budget.class, queryParms,  true, orderBy);
		if(budgetInfo != null && budgetInfo.size() > 0) {
			for(Object object : budgetInfo) {
				Budget budget = (Budget) object;
				Budget _budget = new Budget();
				try {
					_budget.setBudgetGroup(budget.getBudgetGroup());
					_budget.setBudgetType(budget.getBudgetType());
					_budget.setBudgetYear(Integer.valueOf(budgetYear));
					_budget.setAmount(budget.getAmount());
					_budget.setDescription(budget.getDescription());
					_budget.setIsSavingAmount(budget.isSavingAmount());
					_budget.setCreatedDate(new Date());
					_budget.setUpdatedDate(new Date());
					_budget.setCreatedBy(getAuthorizedUser(request));
					_collectionRecordService.saveRecord(_budget);
				}catch(Exception e) {
					try {
						_collectionRecordService.deleteRecord(_budget);
					}catch(Exception e1) { }
				}
			}
		}
		
		LinkedHashMap<String, Object> data = budgetData(budgetYear);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/addEarningPlan", method=RequestMethod.POST)
	public void addEarningPlan(HttpServletRequest request, HttpServletResponse response) throws Exception
	{  
		String earningYear = getRequiredParamter(request, Key.EARNING_YEAR);
		String estimatedAmount = getRequiredParamter(request, Key.ESTIMATED_AMOUNT);
		String description = getParamter(request, Key.DESCRIPTION);
		
		EarningPlan earningPlan = new EarningPlan();
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.EARNING_YEAR, Integer.valueOf(earningYear), QueryRestriction.EQ));
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
		
		ArrayList<?> earningPlanData = _collectionRecordService.getCollectionData(EarningPlan.class, queryParms, true, orderBy);
		if(earningPlanData != null && earningPlanData.size() > 0) {
			earningPlan = (EarningPlan) earningPlanData.get(0);
			earningPlan.setEarningYear(Integer.valueOf(earningYear));
			earningPlan.setEstimatedAmount(Double.valueOf(estimatedAmount));
			earningPlan.setDescription(description);
			earningPlan.setUpdatedDate(new Date());
			_collectionRecordService.updateRecord(earningPlan);
		}else {
			earningPlan.setEarningYear(Integer.valueOf(earningYear));
			earningPlan.setEstimatedAmount(Double.valueOf(estimatedAmount));
			earningPlan.setDescription(description);
			earningPlan.setCreatedDate(new Date());
			earningPlan.setUpdatedDate(new Date());
			earningPlan.setCreatedBy(getAuthorizedUser(request));
			_collectionRecordService.saveRecord(earningPlan);
		}
		
		_logger.finest("Successfully saved a new earning estimate by [" + getAuthenticatedUser() + "]");
		
		LinkedHashMap<String, Object> data = budgetData(earningYear);
		writeHttpResponse(response, data);
	}
	
	private ArrayList<LinkedHashMap<String, String>> getEstimatedEarningInfo(String year){
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.EARNING_YEAR, Integer.valueOf(year), QueryRestriction.EQ));
		
		ArrayList<?> earningPlanInfo = _collectionRecordService.getCollectionData(EarningPlan.class, queryParms, true, orderBy);
		ArrayList<LinkedHashMap<String, String>> records = new ArrayList<LinkedHashMap<String, String>>();
		
		if(earningPlanInfo != null && earningPlanInfo.size() > 0) {
			for(Object object : earningPlanInfo) {
				EarningPlan _earningPlan = (EarningPlan) object;
				LinkedHashMap<String, String> infoRecord = new LinkedHashMap<String, String>();
				infoRecord.put(Key.EARNING_YEAR, String.valueOf(_earningPlan.getEarningYear()));
				infoRecord.put(Key.ESTIMATED_AMOUNT, String.valueOf(_earningPlan.getEstimatedAmount().intValue()));
				infoRecord.put(Key.DESCRIPTION, _earningPlan.getDescription());
				records.add(infoRecord);
			}
		}
		
		return records;
	}
	
	@RequestMapping(value="/addBudget", method=RequestMethod.POST)
	public void addBudget(HttpServletRequest request, HttpServletResponse response) throws Exception
	{  
		String budgetGroup = getRequiredParamter(request, Key.BUDGET_GROUP);
		String budgetType = getRequiredParamter(request, Key.BUDGET_TYPE);
		String budgetYear = getRequiredParamter(request, Key.BUDGET_YEAR);
		String amount = getRequiredParamter(request, Key.AMOUNT);
		String description = getParamter(request, Key.DESCRIPTION);
		String id = getParamter(request, Key.TABLE_ID);
		String isSaving = getParamter(request, Key.IS_SAVINGAMOUNT);
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		
		boolean recordExists = false;
		Budget budget = new Budget();
		if(id != null && id.length() > 0) {
			queryParms.add(new QueryParam(Key.ID, id, QueryRestriction.EQ));
			orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
			ArrayList<?> budgetsInfo = _collectionRecordService.getCollectionData(Budget.class, queryParms, true, orderBy);
			if(budgetsInfo != null && budgetsInfo.size() > 0) {
				budget = (Budget) budgetsInfo.get(0);
				String currentBudgetGroup = budget.getBudgetGroup();
				String currentBudgetType = budget.getBudgetType();
				budget.setBudgetGroup(budgetGroup);
				budget.setBudgetType(budgetType);
				budget.setBudgetYear(Integer.valueOf(budgetYear));
				budget.setAmount(Double.valueOf(amount));
				budget.setDescription(description);
				budget.setUpdatedDate(new Date());
				_collectionRecordService.updateRecord(budget);
				
				HashMap<String, String> fields = new HashMap<String, String>();
				fields.put("currentGroupValue", currentBudgetGroup);
				fields.put("newGroupValue", budgetGroup);
				fields.put("currentTypeValue", currentBudgetType);
				fields.put("newTypeValue", budgetType);
				fields.put("budgetYear", budgetYear);
				updateBudgetRecords(fields);
				updateExpenseRecords(fields);
				updateCollectionecords(fields);
				recordExists = true;
				_logger.finest("Successfully updated the budget by [" + getAuthenticatedUser() + "]");
			}
		} 
		
		if(!recordExists){
			budget.setBudgetGroup(budgetGroup);
			budget.setBudgetType(budgetType);
			budget.setBudgetYear(Integer.valueOf(budgetYear));
			budget.setAmount(Double.valueOf(amount));
			budget.setDescription(description);
			budget.setIsSavingAmount("true".equalsIgnoreCase(isSaving));
			budget.setCreatedBy(getAuthorizedUser(request));
			budget.setCreatedDate(new Date());
			budget.setUpdatedDate(new Date());
			_collectionRecordService.saveRecord(budget);
			_logger.finest("Successfully saved a new budget by [" + getAuthenticatedUser() + "]");
		}
		
		LinkedHashMap<String, Object> data = budgetData(budgetYear);
		writeHttpResponse(response, data);
	}

	private void updateBudgetRecords(HashMap<String, String> fields)
	{
		try {
			ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
			queryParms.add(new QueryParam(Key.BUDGET_GROUP, fields.get("currentGroupValue"), QueryRestriction.EQ));
			queryParms.add(new QueryParam(Key.BUDGET_YEAR, Integer.valueOf(fields.get("budgetYear")), QueryRestriction.EQ));
			ArrayList<?> tables = _collectionRecordService.getCollectionData(Budget.class, queryParms, true);
			
			if(tables != null && tables.size() > 0) {
				for(Object object : tables) {
					Budget budget = (Budget) object;
					budget.setBudgetGroup(fields.get("newGroupValue"));
					_collectionRecordService.updateRecord(budget);
				}
			}
			
			queryParms = new ArrayList<QueryParam>();
			queryParms.add(new QueryParam(Key.BUDGET_TYPE, fields.get("currentTypeValue"), QueryRestriction.EQ));
			queryParms.add(new QueryParam(Key.BUDGET_YEAR, Integer.valueOf(fields.get("budgetYear")), QueryRestriction.EQ));
			tables = _collectionRecordService.getCollectionData(Budget.class, queryParms, true);
			
			if(tables != null && tables.size() > 0) {
				for(Object object : tables) {
					Budget budget = (Budget) object;
					budget.setBudgetType(fields.get("newTypeValue"));
					_collectionRecordService.updateRecord(budget);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateExpenseRecords(HashMap<String, String> fields)
	{
		try {
			ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
			queryParms.add(new QueryParam(Key.EXPENSE_GROUP, fields.get("currentGroupValue"), QueryRestriction.EQ));
			queryParms.add(new QueryParam(Key.EXPENSE_YEAR, Integer.valueOf(fields.get("budgetYear")), QueryRestriction.EQ));
			ArrayList<?> tables = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true);
			
			if(tables != null && tables.size() > 0) {
				for(Object object : tables) {
					ExpenseInfo expenseInfo = (ExpenseInfo) object;
					expenseInfo.setExpenseGroup(fields.get("newGroupValue"));
					_collectionRecordService.updateRecord(expenseInfo);
				}
			}
			
			queryParms = new ArrayList<QueryParam>();
			queryParms.add(new QueryParam(Key.EXPENSE_TYPE, fields.get("currentTypeValue"), QueryRestriction.EQ));
			queryParms.add(new QueryParam(Key.EXPENSE_YEAR, Integer.valueOf(fields.get("budgetYear")), QueryRestriction.EQ));
			tables = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true);
			
			if(tables != null && tables.size() > 0) {
				for(Object object : tables) {
					ExpenseInfo expenseInfo = (ExpenseInfo) object;
					expenseInfo.setExpenseType(fields.get("newTypeValue"));
					_collectionRecordService.updateRecord(expenseInfo);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateCollectionecords(HashMap<String, String> fields)
	{
		try {
			ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
			queryParms.add(new QueryParam(Key.BUDGET_CATEGORY_TYPE, fields.get("currentTypeValue"), QueryRestriction.EQ));
			queryParms.add(new QueryParam(Key.COLLECTION_YEAR, Integer.valueOf(fields.get("budgetYear")), QueryRestriction.EQ));
			ArrayList<?> tables  = _collectionRecordService.getCollectionData(CollectionInfo.class, queryParms, true);
			
			if(tables != null && tables.size() > 0) {
				for(Object object : tables) {
					CollectionInfo collectionInfo = (CollectionInfo) object;
					collectionInfo.setBudgetCategoryType(fields.get("newTypeValue"));
					_collectionRecordService.updateRecord(collectionInfo);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	private LinkedHashMap<String, Object> budgetData(String budgetYear) {
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.EARNING_YEAR, Integer.valueOf(budgetYear), QueryRestriction.EQ));
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
		
		ArrayList<?> earningPlanInfo = _collectionRecordService.getCollectionData(EarningPlan.class, queryParms, true, orderBy);
		double estimatedEarning = 0.0;
		if(earningPlanInfo != null && earningPlanInfo.size() > 0) {
			estimatedEarning = ((EarningPlan) earningPlanInfo.get(0)).getEstimatedAmount();
		}
		
		LinkedHashMap<String, Object> records = new LinkedHashMap<String, Object>();
		ArrayList<LinkedHashMap<String, String>> _estimatedEarning = getEstimatedEarningInfo(budgetYear);
		if(_estimatedEarning != null) {
			records.put("estimatedEarning", _estimatedEarning);
		}
		
		LinkedHashMap<String, Object> dataMap = getBudgetsInfo(budgetYear, estimatedEarning);
		ArrayList<LinkedHashMap<String, String>> budgets = (ArrayList<LinkedHashMap<String, String>>) dataMap.get("budgets");
		double total = (double) dataMap.get("total");
		
		if(budgets != null) {
			records.put("budgets", budgets);
		}
		
		data.put("data", records);
		data.put("success", true);
		data.put("total", (int) Math.round(total));
		data.put("percentage", dataMap.get("percentage"));
		return data;
	}

	private LinkedHashMap<String, Object> getBudgetsInfo(String budgetYear, double estimatedEarning)
	{
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.BUDGET_YEAR, Integer.valueOf(budgetYear), QueryRestriction.EQ));
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
		orderBy.put(Key.BUDGET_GROUP, OrderBy.ASC);
		
		ArrayList<?> budgetInfo = _collectionRecordService.getCollectionData(Budget.class, queryParms,  true, orderBy);
		ArrayList<LinkedHashMap<String, String>> budgets = new ArrayList<LinkedHashMap<String, String>>();
		
		LinkedHashMap<String, ArrayList<Budget>> reorderedBudgets = new LinkedHashMap<String, ArrayList<Budget>>();
		
		if(budgetInfo != null && budgetInfo.size() > 0) {
			for(Object object : budgetInfo) {
				Budget _budget = (Budget) object;
				if(reorderedBudgets.get(_budget.getBudgetGroup()) == null) {
					reorderedBudgets.put(_budget.getBudgetGroup(), new ArrayList<Budget>());
				}
				
				reorderedBudgets.get(_budget.getBudgetGroup()).add(_budget);
			}
		}
		
		DecimalFormat df = new DecimalFormat("#.##"); 
		double total = 0;
		double percentage = 0.00;
		for(Map.Entry<String, ArrayList<Budget>> entry : reorderedBudgets.entrySet())
		{
			for(Budget _budget : entry.getValue()) {
				LinkedHashMap<String, String> infoRecord = new LinkedHashMap<String, String>();
				infoRecord.put(Key.BUDGET_GROUP, _budget.getBudgetGroup());
				infoRecord.put(Key.BUDGET_TYPE, _budget.getBudgetType());
				infoRecord.put(Key.BUDGET_YEAR, String.valueOf(_budget.getBudgetYear()));
				infoRecord.put(Key.AMOUNT, String.valueOf((int) Math.round(_budget.getAmount())));
				infoRecord.put(Key.DESCRIPTION, StringUtils.isEmpty(_budget.getDescription()) ? "N/A" : _budget.getDescription());
				double _percentage =  (_budget.getAmount() / estimatedEarning)*100;
				String formattedValue = df.format(_percentage);
				percentage += _percentage;
				infoRecord.put(Key.PERCENTAGE, formattedValue);
				infoRecord.put(Key.TABLE_ID, _budget.getId());
				budgets.add(infoRecord);
				total += _budget.getAmount();
			}
		}
		
		map.put("budgets", budgets);
		map.put("total", total);
		map.put("percentage", df.format(percentage));
		return map;
	}
	
}
