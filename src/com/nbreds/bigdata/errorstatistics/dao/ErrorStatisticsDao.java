package com.nbreds.bigdata.errorstatistics.dao;

import java.sql.SQLException;

import com.nbreds.bigdata.common.dao.CommonDao;
import com.nbreds.bigdata.errorstatistics.vo.ErrorStatisticsVo;

public class ErrorStatisticsDao extends CommonDao {
	
	private static ErrorStatisticsDao instance = null;
	
	public static ErrorStatisticsDao getInstance() {
		if (instance == null) {
			instance = new ErrorStatisticsDao();
		}
		return instance;
	}
	
	public void insertErrorStatistics(ErrorStatisticsVo errorStatVo) throws SQLException {
		try {
			insert("ErrorStatistics.insertErrorStatistics", errorStatVo);
		} catch (SQLException e) {
			rollback();
			throw e;
		} finally {
			commit();
		}
	}
	
}
