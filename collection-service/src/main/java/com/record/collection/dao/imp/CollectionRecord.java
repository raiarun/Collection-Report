package com.record.collection.dao.imp;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.record.collection.beans.CollectionInfo;
import com.record.collection.dao.ICollectionRecordDAO;

@Repository
public class CollectionRecord extends BaseDAO implements ICollectionRecordDAO {

	@Override
	public void saveRecord(Object object) {
		_sessionFactory.getCurrentSession().save(object);
	}

	@Override
	public void updateRecord(Object object) {
		_sessionFactory.getCurrentSession().update(object);
	}
	
	@Override
	public void deleteRecord(Object object) {
		_sessionFactory.getCurrentSession().delete(object);
	}

	@Override
	public SessionFactory getSessionFactory() {
		return _sessionFactory;
	}

}
