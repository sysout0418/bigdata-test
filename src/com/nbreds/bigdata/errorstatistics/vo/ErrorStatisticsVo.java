package com.nbreds.bigdata.errorstatistics.vo;

public class ErrorStatisticsVo {
	
	private String stat_type;
	private String reg_date;
	private String error_code;
	private String service_type;
	private int error_count;
	
	private int specific_week;
	private String start_date;
	private String end_date;
	
	public ErrorStatisticsVo() {}
	
	public String getStat_type() {
		return stat_type;
	}

	public void setStat_type(String stat_type) {
		this.stat_type = stat_type;
	}

	public String getReg_date() {
		return reg_date;
	}

	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getService_type() {
		return service_type;
	}

	public void setService_type(String service_type) {
		this.service_type = service_type;
	}

	public int getError_count() {
		return error_count;
	}

	public void setError_count(int error_count) {
		this.error_count = error_count;
	}
	
	public int getSpecific_week() {
		return specific_week;
	}

	public void setSpecific_week(int specific_week) {
		this.specific_week = specific_week;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	@Override
	public String toString() {
		return "ErrorStatisticsVo [stat_type=" + stat_type + ", reg_date=" + reg_date + ", error_code=" + error_code
				+ ", service_type=" + service_type + ", error_count=" + error_count + ", specific_week=" + specific_week
				+ ", start_date=" + start_date + ", end_date=" + end_date + "]";
	}
	
}
