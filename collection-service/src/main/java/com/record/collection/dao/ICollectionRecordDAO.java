package com.record.collection.dao;

import org.hibernate.SessionFactory;

public interface ICollectionRecordDAO {
	void saveRecord(Object object);
	void updateRecord(Object object);
	void deleteRecord(Object object);
	SessionFactory getSessionFactory();
}
