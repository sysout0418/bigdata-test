package com.nbreds.bigdata.countip.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.nbreds.bigdata.common.dao.CommonDao;

public class CountIPDao extends CommonDao {

	private static CountIPDao instance = null;

	public static CountIPDao getInstance() {

		if (instance == null) {
			instance = new CountIPDao();
		}

		return instance;

	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> checkCountIP(Map<String, Object> params) throws SQLException {

		return (List<Map<String, Object>>) queryForList("CountIP.checkCountIP", params);

	}

}