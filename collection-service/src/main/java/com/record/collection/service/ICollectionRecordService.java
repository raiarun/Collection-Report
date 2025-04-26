package com.record.collection.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.record.collection.service.imp.CollectionRecordService.OrderBy;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;

@SuppressWarnings("rawtypes") 
public interface ICollectionRecordService {
	void saveRecord(Object object);
	void updateRecord(Object object);
	void deleteRecord(Object object);
	ArrayList<?> getCollectionData(Class className, String columnName, String columnValue);
	ArrayList<?> getCollectionData(Class className, String columnName, Object columnValue, LinkedHashMap<String, OrderBy> orderBy);
	ArrayList<?> getAllData(Class className);
	ArrayList<?> getAllData(Class className, LinkedHashMap<String, OrderBy> orderBy);
	ArrayList<?> getCollectionData(Class className, ArrayList<QueryParam> queryParams,  boolean distinct);
	ArrayList<?> getCollectionData(Class className, ArrayList<QueryParam> queryParams,  boolean distinct, LinkedHashMap<String, OrderBy> orderBy);
}
