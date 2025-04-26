package com.record.collection.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.record.collection.beans.Budget;
import com.record.collection.beans.ExpenseInfo;
import com.record.collection.controllers.base.RequestControllerBase;
import com.record.collection.service.imp.CollectionRecordService.OrderBy;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;
import com.record.collection.service.imp.CollectionRecordService.QueryRestriction;
import com.record.collection.utils.Key;

@Controller
public class ExpenseReportController extends RequestControllerBase 
{
	private static final Logger _logger = Logger.getLogger(ExpenseReportController.class.getName());
	
	private static final String REIMBURSEMENT_REQUIRED = "Required";
	private static final String STATUS_PENDING = "Pending";
	private static final String STATUS_DENIED = "Denied";
	private static final String STATUS_APPROVED = "Approved";
	public static final String STATUS_ACKNOWLEDGED = "Acknowledged";
	
	private static final String KEY_ACKNOWLEDGE = "Acknowledge";
	private static final String KEY_DENY = "Deny";
	private static final String KEY_REIMBURSE = "Reimburse";
	
	@RequestMapping(value="/addExpense", method=RequestMethod.POST)
	public void addExpense(HttpServletRequest request, HttpServletResponse response) throws Exception
	{  		
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		String expenseName = getRequiredParamter(request, Key.EXPENSE_NAME); 
		String paymentMethod = getRequiredParamter(request, Key.PAYMENT_METHOD);
		String amount = getRequiredParamter(request, Key.AMOUNT);
		String description = getRequiredParamter(request, Key.DESCRIPTION);
		String reportedBy = getRequiredParamter(request, Key.REPORTED_BY);
		String _expenseDate = getRequiredParamter(request, Key.EXPENSE_DATE);
		String _authorizedUser = getRequiredParamter(request, Key.AUTHORIZED_USER);
		String reimbursement = getParamter(request, Key.REIMBURSEMENT);
		
		SimpleDateFormat sd = new SimpleDateFormat (dateTimeFormat);
		Date expenseDate = sd.parse(_expenseDate);
		
		String imageRootPath = File.separator + "webapps" + File.separator + "ROOT";
		String imageFolderPath = imageRootPath + File.separator + "image";
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		String timeStamp = stamp.toString().replace(":", "").replace(".", "").replace(" ", "-");
		
		String fileName = _authorizedUser.toLowerCase().replace(" ", "_").trim() + "-" + reportedBy.toLowerCase().replace(" ", "_").trim();
		fileName += "-" + timeStamp + "-" + request.getSession().getId().toLowerCase();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(expenseDate);
		String personalizedFolder = _authorizedUser.toLowerCase().replace(" ", "_").trim() + "-" + cal.get(Calendar.YEAR);
		String pImageFolderPath = imageFolderPath + File.separator + personalizedFolder;
		
		File folder = new File(_catalinaBase + File.separator + pImageFolderPath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		ArrayList<String> imageReferencePaths = new ArrayList<String>();
		boolean isUploaded = ServletFileUpload.isMultipartContent(request);
		if(isUploaded) {
			try {
				ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
				if(upload != null) { 
					List<FileItem> items = (List<FileItem>) upload.parseRequest(request);
					int counter = 0;
					for (FileItem item: items) {
						String fileExtension = item.getName().split("\\.")[1];
						String receiptFileName = fileName + "-" + counter + "." + fileExtension;
						File imageFile = new File(_catalinaBase + File.separator + pImageFolderPath + File.separator + receiptFileName);
						item.write(imageFile);
						
						imageReferencePaths.add(pImageFolderPath + File.separator + receiptFileName);
						counter++;
					}
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				data.put("success", false);
				writeHttpResponse(response, data);
				return;
			}
		}
		
		ExpenseInfo expenseInfo = new ExpenseInfo();
		expenseInfo.setExpenseName(expenseName);
		expenseInfo.setPaymentMethod(paymentMethod);
		expenseInfo.setDescription(description);
		expenseInfo.setAmount(Double.valueOf(amount));
		expenseInfo.setReimbursementRequired(REIMBURSEMENT_REQUIRED.equalsIgnoreCase(reimbursement));
		String _imageReferencePaths = imageReferencePaths.toString().replace("[", "").replace("]", "");
		_imageReferencePaths = _imageReferencePaths.replace(imageRootPath, "");
		expenseInfo.setImageReferencePath(_imageReferencePaths);
		expenseInfo.setCreatedDate(new Date());
		expenseInfo.setUpdatedDate(new Date());
		expenseInfo.setReportedBy(reportedBy);
		
		String loggedInUser = getAuthenticatedUser();
		expenseInfo.setStatus(STATUS_PENDING);
		
		expenseInfo.setExpenseDate(expenseDate);
		Calendar expenseDateCal = Calendar.getInstance();
		expenseDateCal.setTime(expenseDate);
		expenseInfo.setExpenseYear(expenseDateCal.get(Calendar.YEAR));
		expenseInfo.setCreatedBy(getAuthorizedUser(request));
		
		_collectionRecordService.saveRecord(expenseInfo);
		_logger.finest("Successfully saved an expense report by [" + loggedInUser + "]");
		data.put("success", true);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/submitExpenseApproval", method=RequestMethod.POST)
	public void submitExpenseApproval(HttpServletRequest request, HttpServletResponse response) throws Exception
	{ 
		String expenseGroup = getRequiredParamter(request, Key.EXPENSE_GROUP);
		String expenseType = getRequiredParamter(request, Key.EXPENSE_TYPE);
		String description = getParamter(request, Key.DESCRIPTION);
		String tableId = getRequiredParamter(request, Key.TABLE_ID);
		String action = getRequiredParamter(request, Key.ACTION);
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.ID, tableId, QueryRestriction.EQ));
		
		ArrayList<?> expenseData = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true);
		if(expenseData != null && expenseData.size() > 0) {
			 for(Object expense : expenseData) {
				 ExpenseInfo expenseInfo = (ExpenseInfo) expense;
				 if(KEY_ACKNOWLEDGE.equalsIgnoreCase(action)) {
					 expenseInfo.setStatus(STATUS_ACKNOWLEDGED);
				 }else if(KEY_DENY.equalsIgnoreCase(action)) {
					 expenseInfo.setStatus(STATUS_DENIED);
				 }
				 
				 if(!StringUtils.isEmpty(description)) {
					 expenseInfo.setDescription(description);
				 }
				 
				 expenseInfo.setExpenseGroup(expenseGroup);
				 expenseInfo.setExpenseType(expenseType);
				 expenseInfo.setUpdatedDate(new Date());
				 
				 _collectionRecordService.updateRecord(expenseInfo);
			 }
		}
		
		getPendingApprovalExpenses(request, response);
	}
	
	@RequestMapping(value="/markReimbursed", method=RequestMethod.POST)
	public void markReimbursed(HttpServletRequest request, HttpServletResponse response) throws Exception
	{ 
		String tableId = getRequiredParamter(request, Key.TABLE_ID);
		String action = getRequiredParamter(request, Key.ACTION);
		if(KEY_REIMBURSE.equalsIgnoreCase(action)) {
			ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
			queryParms.add(new QueryParam(Key.ID, tableId, QueryRestriction.EQ));
			
			ArrayList<?> expenseData = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true);
			if(expenseData != null && expenseData.size() > 0) {
				 for(Object expense : expenseData) {
					 ExpenseInfo expenseInfo = (ExpenseInfo) expense;
					 expenseInfo.setReimbursed(true);
					 expenseInfo.setUpdatedDate(new Date());
					 _collectionRecordService.updateRecord(expenseInfo);
				 }
			}
		}
		
		getPendingReimbursementExpenses(request, response);
	}
	
	@RequestMapping(value="/getPendingReimbursementExpenses", method=RequestMethod.GET)
	public void getPendingReimbursementExpenses(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		///queryParms.add(new QueryParam(Key.STATUS, STATUS_ACKNOWLEDGED, QueryRestriction.EQ));
		queryParms.add(new QueryParam(Key.REIMBURSEMENT_REQUIRED, true, QueryRestriction.EQ));
		queryParms.add(new QueryParam(Key.REIMBURSED, false, QueryRestriction.EQ));
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.EXPENSE_DATE, OrderBy.ASC);
		
		LinkedHashMap<Integer, LinkedHashMap<String, ArrayList<String>>> budgetGroupTypeMap = new LinkedHashMap<Integer, LinkedHashMap<String, ArrayList<String>>>();
		double total = 0.00;
		ArrayList<LinkedHashMap<String, Object>> records = new ArrayList<LinkedHashMap<String, Object>>();
		ArrayList<?> expenseData = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true, orderBy);
		if(expenseData != null && expenseData.size() > 0) {
			 for(Object expense : expenseData) {
				 ExpenseInfo expenseInfo = (ExpenseInfo) expense;
				 LinkedHashMap<String, Object> infoRecord = new LinkedHashMap<String, Object>();
				 infoRecord.put(Key.TABLE_ID, expenseInfo.getId());
				 infoRecord.put(Key.EXPENSE_NAME, expenseInfo.getExpenseName());
				 infoRecord.put(Key.EXPENSE_GROUP, expenseInfo.getExpenseGroup());
				 infoRecord.put(Key.EXPENSE_TYPE, expenseInfo.getExpenseType());
				 infoRecord.put(Key.AMOUNT, String.valueOf(expenseInfo.getAmount()));
				 infoRecord.put(Key.PAYMENT_METHOD, expenseInfo.getPaymentMethod());
				 infoRecord.put(Key.EXPENSE_DATE, expenseInfo.getExpenseDate().toString().split(" ")[0]);
				 infoRecord.put(Key.REIMBURSEMENT_REQUIRED, expenseInfo.isReimbursementRequired());
				 infoRecord.put(Key.REIMBURSED, expenseInfo.isReimbursed());
				 infoRecord.put(Key.REPORTED_BY, expenseInfo.getReportedBy());
				 infoRecord.put(Key.DESCRIPTION, expenseInfo.getDescription());
				 infoRecord.put(Key.IMAGE_REFERENCE_PATH, expenseInfo.getImageReferencePath());
				 
				 Calendar cal = Calendar.getInstance();
				 cal.setTime(expenseInfo.getCreatedDate());
				 int year = cal.get(Calendar.YEAR);
				 if(budgetGroupTypeMap.get(year) == null) {
					 budgetGroupTypeMap.put(year, getExpenseGroupTypeInfo(year));
				 }
				 
				 infoRecord.put("budgetGroupType", budgetGroupTypeMap.get(year));
				 
				 records.add(infoRecord);
				 total += expenseInfo.getAmount();
			 }
		}
		
		data.put("data", records);
		data.put("success", true);
		data.put("total", total);
		writeHttpResponse(response, data);
	}
	
	@RequestMapping(value="/getPendingApprovalExpenses", method=RequestMethod.GET)
	public void getPendingApprovalExpenses(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.STATUS, STATUS_PENDING, QueryRestriction.EQ));
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.EXPENSE_DATE, OrderBy.ASC);
		
		LinkedHashMap<Integer, LinkedHashMap<String, ArrayList<String>>> budgetGroupTypeMap = new LinkedHashMap<Integer, LinkedHashMap<String, ArrayList<String>>>();
		double total = 0.00;
		ArrayList<LinkedHashMap<String, Object>> records = new ArrayList<LinkedHashMap<String, Object>>();
		ArrayList<?> expenseData = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true, orderBy);
		if(expenseData != null && expenseData.size() > 0) {
			 for(Object expense : expenseData) {
				 ExpenseInfo expenseInfo = (ExpenseInfo) expense;
				 LinkedHashMap<String, Object> infoRecord = new LinkedHashMap<String, Object>();
				 infoRecord.put(Key.TABLE_ID, expenseInfo.getId());
				 infoRecord.put(Key.EXPENSE_NAME, expenseInfo.getExpenseName());
				 infoRecord.put(Key.AMOUNT, String.valueOf(expenseInfo.getAmount()));
				 infoRecord.put(Key.PAYMENT_METHOD, expenseInfo.getPaymentMethod());
				 infoRecord.put(Key.EXPENSE_DATE, expenseInfo.getExpenseDate().toString().split(" ")[0]);
				 infoRecord.put(Key.CREATED_DATE, expenseInfo.getCreatedDate().toString().split(" ")[0]);
				 infoRecord.put(Key.REPORTED_BY, expenseInfo.getReportedBy());
				 infoRecord.put(Key.DESCRIPTION, expenseInfo.getDescription());
				 infoRecord.put(Key.IMAGE_REFERENCE_PATH, expenseInfo.getImageReferencePath());
				 
				 Calendar cal = Calendar.getInstance();
				 cal.setTime(expenseInfo.getCreatedDate());
				 int year = cal.get(Calendar.YEAR);
				 if(budgetGroupTypeMap.get(year) == null) {
					 budgetGroupTypeMap.put(year, getExpenseGroupTypeInfo(year));
				 }
				 
				 infoRecord.put("budgetGroupType", budgetGroupTypeMap.get(year));
				 
				 records.add(infoRecord);
				 total += expenseInfo.getAmount();
			 }
		}
		
		data.put("data", records);
		data.put("success", true);
		data.put("total", total);
		writeHttpResponse(response, data);
	}
	
	private LinkedHashMap<String, ArrayList<String>> getExpenseGroupTypeInfo(int year)
	{
		 LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();
			
		 ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		 queryParms.add(new QueryParam(Key.BUDGET_YEAR, year, QueryRestriction.EQ));
		
		 LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		 orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
		 orderBy.put(Key.BUDGET_GROUP, OrderBy.ASC);
		
		 ArrayList<?> budgetInfo = _collectionRecordService.getCollectionData(Budget.class, queryParms,  true, orderBy);
		 if(budgetInfo != null && budgetInfo.size() > 0) {
			 for(Object object : budgetInfo) {
				 Budget budget = (Budget) object;
				 if(map.get(budget.getBudgetGroup()) == null) {
					 map.put(budget.getBudgetGroup(), new ArrayList<String>());
				 }
				 
				 map.get(budget.getBudgetGroup()).add(budget.getBudgetType());
			 }
		 }
		 
		 return map;
	}
	
	@RequestMapping(value="/getExpenses", method=RequestMethod.GET)
	public void getExpenses(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		double total = 0.0;
		SimpleDateFormat sd = new SimpleDateFormat (dateTimeFormat);
		Date startDate = sd.parse(getRequiredParamter(request, Key.START_DATE));
		Date endDate = sd.parse(getRequiredParamter(request, Key.END_DATE));
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.add(Calendar.DATE, 1);
		endDate = cal.getTime();
		
		if(startDate.after(endDate)) {
			data.put("success", false);
			data.put("message", "Error: the start date is after the end date.");
			writeHttpResponse(response, data);
			return;
		}
		
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.CREATED_DATE, startDate, QueryRestriction.GT));
		queryParms.add(new QueryParam(Key.CREATED_DATE, endDate, QueryRestriction.LT));
		
		LinkedHashMap<String, OrderBy> orderBy = new LinkedHashMap<String, OrderBy>();
		orderBy.put(Key.CREATED_DATE, OrderBy.ASC);
		
		ArrayList<LinkedHashMap<String, String>> records = new ArrayList<LinkedHashMap<String, String>>();
		ArrayList<?> expenseData = _collectionRecordService.getCollectionData(ExpenseInfo.class, queryParms, true, orderBy);
		
		if(expenseData != null && expenseData.size() > 0) {
			 for(Object expense : expenseData) {
				 ExpenseInfo expenseInfo = (ExpenseInfo) expense;
				 if(!STATUS_DENIED.equalsIgnoreCase(expenseInfo.getStatus().trim())) {
					 LinkedHashMap<String, String> infoRecord = new LinkedHashMap<String, String>();
					 infoRecord.put(Key.EXPENSE_TYPE, expenseInfo.getExpenseType());
					 infoRecord.put(Key.EXPENSE_GROUP, expenseInfo.getExpenseGroup());
					 infoRecord.put(Key.APPROVED_BY, expenseInfo.getApprovedBy());
					 infoRecord.put(Key.AMOUNT, String.valueOf(expenseInfo.getAmount()));
					 infoRecord.put(Key.EXPENSE_DATE, expenseInfo.getExpenseDate().toString().split(" ")[0]);
					 infoRecord.put(Key.CREATED_DATE, expenseInfo.getCreatedDate().toString());
					 infoRecord.put(Key.REPORTED_BY, expenseInfo.getReportedBy());
					 infoRecord.put(Key.DESCRIPTION, expenseInfo.getDescription());
					 infoRecord.put(Key.STATUS, expenseInfo.getStatus());
					 infoRecord.put(Key.IMAGE_REFERENCE_PATH, expenseInfo.getImageReferencePath());
					 records.add(infoRecord);
					 total += expenseInfo.getAmount();
				 }
				 
			 }
		 }
		
		data.put("data", records);
		data.put("success", true);
		data.put("total", total);
		writeHttpResponse(response, data);
	}
	
}
