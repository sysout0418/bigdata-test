package com.nbreds.bigdata.ipstatistics.dao;

import java.sql.SQLException;
import java.util.List;

import com.nbreds.bigdata.common.dao.CommonDao;
import com.nbreds.bigdata.ipstatistics.vo.IPStatisticsVo;

public class IPStatisticsDao extends CommonDao {
	
	private static IPStatisticsDao instance = null;
	
	public static IPStatisticsDao getInstance() {
		
		if (instance == null) {
			instance = new IPStatisticsDao();
		}
		
		return instance;
		
	}
	
	public List<?> selectIPStatistics() throws SQLException {
		
		return queryForList("IPStatistics.selectIPStatistics");
		
	}
	
	public void insertIPStatistics(IPStatisticsVo iPStatisticsVo) throws SQLException {
		
		insert("IPStatistics.insertIPStatistics", iPStatisticsVo);
		commit();
		
	}

}