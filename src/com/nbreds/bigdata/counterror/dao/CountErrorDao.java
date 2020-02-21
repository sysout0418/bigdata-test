package com.nbreds.bigdata.counterror.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.nbreds.bigdata.common.dao.CommonDao;

/**
 * 
 * <pre>
 * CountErrorDao - 에러 카운트 DAO
 * </pre>
 * 
 * @author 김상일
 * @since 2018. 5. 2.
 */
public class CountErrorDao extends CommonDao {
	
	private static CountErrorDao instance = null;
	
	public static CountErrorDao getInstance() {
		if (instance == null) {
			instance = new CountErrorDao();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> checkCountError(Map<String, Object> params) throws SQLException {
		return (List<Map<String, Object>>) queryForList("CountError.checkCountError", params);
	}
	
}
