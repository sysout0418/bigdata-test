package com.nbreds.bigdata.scanerror.dao;

import java.sql.SQLException;

import com.nbreds.bigdata.common.dao.CommonDao;

public class ScanErrorDao extends CommonDao{
	public int getErrorScanRange() throws NumberFormatException, SQLException {
		return Integer.parseInt(String.valueOf(queryForObject("ScanError.getErrorScanRange")));
	}

}
