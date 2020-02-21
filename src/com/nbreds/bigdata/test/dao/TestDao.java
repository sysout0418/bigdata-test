package com.nbreds.bigdata.test.dao;

import java.sql.SQLException;
import java.util.List;

import com.nbreds.bigdata.common.db.SQLManager;

public class TestDao {
	
	private static TestDao instance = null;
	public static TestDao getInstance() {
		if (instance == null) {
			instance = new TestDao();
		}
		return instance;
	}
	
	public List<?> testSelect() throws SQLException {
		System.out.println("TestDao.testSelect");
		return SQLManager.getSqlMap().selectList("Test.testSelect");
	}
}
