package com.nbreds.bigdata.common.dao;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nbreds.bigdata.common.db.SQLManager;

/**
 * <pre>
 * 공통적으로 사용해야 하는 쿼리 호출 집합체
 * </pre>
 * @author branz
 * @since 2018.04
 * 
 * */
public class CommonDao {
	private static final Logger logger = LoggerFactory.getLogger(CommonDao.class);
	
	private static CommonDao instance = null;
	public static CommonDao getInstance() {
		if (instance == null) {
			instance = new CommonDao();
		}
		return instance;
	}

	/**********************
	 * 아래 양식으로 구현
	public String name() throws SQLException {
		return (String) SQLManager.getSqlMap().queryForObject("Common.Name");
	}
	**********************/

	public Object queryForObject(String namespace) throws SQLException {
		logger.info("queryForObject {}", namespace);
		return SQLManager.getSqlMap().selectOne(namespace);
	}
	
	public Object queryForObject(String namespace, Object model) throws SQLException {
		logger.info("queryForObject {}", namespace);
		return SQLManager.getSqlMap().selectOne(namespace, model);
	}
	
	public void update(String namespace) throws SQLException {
		logger.info("update {}", namespace);
		SQLManager.getSqlMap().update(namespace);
	}
	
	public void update(String namespace, Object model) throws SQLException {
		logger.info("update {}", namespace);
		SQLManager.getSqlMap().update(namespace, model);
	}
	
	public void insert(String namespace) throws SQLException {
		logger.info("insert {}", namespace);
		SQLManager.getSqlMap().insert(namespace);
	}
	
	public void insert(String namespace, Object model) throws SQLException {
		logger.info("insert {}", namespace);
		SQLManager.getSqlMap().insert(namespace, model);
	}

	@SuppressWarnings("rawtypes")
	public List queryForList(String namespace) throws SQLException {
		logger.info("queryForList {}", namespace);
		return SQLManager.getSqlMap().selectList(namespace);
	}

	@SuppressWarnings("rawtypes")
	public List queryForList(String namespace, Object model) throws SQLException {
		logger.info("queryForList {}", namespace);
		return SQLManager.getSqlMap().selectList(namespace, model);
	}
	
	public void rollback() {
		SQLManager.getSqlMap().rollback();
	}
	
	public void commit() {
		SQLManager.getSqlMap().commit();
	}
	
}
