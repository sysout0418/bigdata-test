package com.nbreds.bigdata.sms.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.nbreds.bigdata.common.dao.CommonDao;
import com.nbreds.bigdata.sms.vo.SmsVo;

/**
 * 
 * <pre>
 * SmsDao - SMS DAO
 * </pre>
 * 
 * @author 김상일
 * @since 2018. 5. 2.
 */
public class SmsDao extends CommonDao {
	
	private static SmsDao instance = null;
	
	public static SmsDao getInstance() {
		if (instance == null) {
			instance = new SmsDao();
		}
		return instance;
	}
	
	public void insertSms(Map<String, Object> params) throws SQLException {
		try {
			insert("Sms.insertSms", params);
		} catch (SQLException e) {
			rollback();
			throw e;
		} finally {
			commit();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SmsVo> getSmsList() throws SQLException {
		return (List<SmsVo>) queryForList("Sms.getSmsList");
	}
	
}
