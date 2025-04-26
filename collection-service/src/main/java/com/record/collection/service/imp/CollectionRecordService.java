package com.record.collection.service.imp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.record.collection.dao.ICollectionRecordDAO;
import com.record.collection.service.ICollectionRecordService;

@Service
public class CollectionRecordService implements ICollectionRecordService {

	@Autowired
	protected ICollectionRecordDAO _collectionRecordDAO;
	
	@Transactional
	public void saveRecord(Object object) {
		_collectionRecordDAO.saveRecord(object);
	}


	@Transactional
	public void updateRecord(Object object) {
		_collectionRecordDAO.updateRecord(object);
	}
	
	@Transactional
	public void deleteRecord(Object object) {
		_collectionRecordDAO.deleteRecord(object);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<?> getCollectionData(Class className, String columnName, String columnValue) {
		SessionFactory factory = _collectionRecordDAO.getSessionFactory();
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(className).add(Restrictions.eq(columnName, columnValue));
		
		List<Object> data = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		session.close();
		if(data != null && data.size() > 0) {
			return  (ArrayList<?>) data;
		}
		
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<?> getCollectionData(Class className, String columnName, Object columnValue, LinkedHashMap<String, OrderBy> orderBy) {
		SessionFactory factory = _collectionRecordDAO.getSessionFactory();
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(className).add(Restrictions.eq(columnName, columnValue));
		if(orderBy != null && orderBy.size() > 0) {
			for(Entry<String, OrderBy> entry : orderBy.entrySet()) {
				if(OrderBy.ASC.equals(entry.getValue())){
					criteria.addOrder(Order.asc(entry.getKey()));
				}else {
					criteria.addOrder(Order.desc(entry.getKey()));
				}
			}
		}
		
		List<Object> data = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		session.close();
		if(data != null && data.size() > 0) {
			return  (ArrayList<?>) data;
		}
		
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<?> getAllData(Class className){
		SessionFactory factory = _collectionRecordDAO.getSessionFactory();
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(className);
		List<Object> data = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		session.close();
		if(data != null && data.size() > 0) {
			return  (ArrayList<?>) data;
		}
		
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<?> getAllData(Class className, LinkedHashMap<String, OrderBy> orderBy){
		SessionFactory factory = _collectionRecordDAO.getSessionFactory();
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(className);
		
		if(orderBy != null && orderBy.size() > 0) {
			for(Entry<String, OrderBy> entry : orderBy.entrySet()) {
				if(OrderBy.ASC.equals(entry.getValue())){
					criteria.addOrder(Order.asc(entry.getKey()));
				}else {
					criteria.addOrder(Order.desc(entry.getKey()));
				}
			}
		}
		
		List<Object> data = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		session.close();
		if(data != null && data.size() > 0) {
			return  (ArrayList<?>) data;
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<?> getCollectionData(Class className, ArrayList<QueryParam> queryParams, boolean distinct) {
		return getCollectionData(className, queryParams, distinct, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<?> getCollectionData(Class className, ArrayList<QueryParam> queryParams, boolean distinct,
			LinkedHashMap<String, OrderBy> orderBy) {
		SessionFactory factory = _collectionRecordDAO.getSessionFactory();
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(className);
		if(queryParams != null && queryParams.size() > 0)
		{
			for(QueryParam param : queryParams) {
				if(QueryRestriction.EQ.equals(param.getQueryRestriction())) {
					criteria.add(Restrictions.eq(param.getName(), param.getValue()));
				}else if(QueryRestriction.GT.equals(param.restriction)) {
					criteria.add(Restrictions.gt(param.getName(), param.getValue()));
				}else if(QueryRestriction.GE.equals(param.restriction)) {
					criteria.add(Restrictions.ge(param.getName(), param.getValue()));
				}else if(QueryRestriction.LT.equals(param.restriction)) {
					criteria.add(Restrictions.lt(param.getName(), param.getValue()));
				}else if(QueryRestriction.LE.equals(param.restriction)) {
					criteria.add(Restrictions.le(param.getName(), param.getValue()));
				}
				
			}
		}
		
		if(orderBy != null && orderBy.size() > 0) {
			for(Entry<String, OrderBy> entry : orderBy.entrySet()) {
				if(OrderBy.ASC.equals(entry.getValue())){
					criteria.addOrder(Order.asc(entry.getKey()));
				}else {
					criteria.addOrder(Order.desc(entry.getKey()));
				}
			}
		}
		
		List<Object> data = distinct ? criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list() : criteria.list();
		session.close();
		if(data != null && data.size() > 0) {
			return  (ArrayList<?>) data;
		}
		
		return null;
	}
	
	public static class QueryParam
	{
		private String name;
		private Object value;
		private QueryRestriction restriction;
		
		public QueryParam(String name, Object value, QueryRestriction restriction) {
			this.name = name;
			this.value = value;
			this.restriction = restriction;
		}
		
		public String getName() {
			return this.name;
		}
		
		public Object getValue() {
			return this.value;
		}
		
		public QueryRestriction getQueryRestriction() {
			return this.restriction;
		}
	}
	
	public static enum QueryRestriction
	{
		EQ,GT,LT,GE,LE
	}

	public static enum OrderBy
	{
		ASC, DESC
	}


}
