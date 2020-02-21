package com.nbreds.bigdata.common.dao;

import java.sql.SQLException;
import java.util.HashMap;

public class CommonCodeDao extends CommonDao{

	/**
	 * getSaveErrorLogConfig
	 * Error log 파일저장 설정은 가져온다
	 * @return HashMap<String, Object>
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getSaveErrorLogConfig() throws SQLException{
		return (HashMap<String, Object>) queryForObject("Common.getSaveErrorLogConfig");
	}
}
