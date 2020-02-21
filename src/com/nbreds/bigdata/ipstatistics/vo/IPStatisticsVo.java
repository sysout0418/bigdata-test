package com.nbreds.bigdata.ipstatistics.vo;

public class IPStatisticsVo {

	private String stat_type;
	private String reg_date;
	private String ip_address;
	private String service_type;
	private int ip_count;
	private String inserted_id;

	private int specific_week;
	private String start_date;
	private String end_date;

	@Override
	public String toString() {
		return "IPStatisticsVo [stat_type=" + stat_type + ", reg_date=" + reg_date + ", ip_address=" + ip_address
				+ ", service_type=" + service_type + ", ip_count=" + ip_count + ", inserted_id=" + inserted_id
				+ ", specific_week=" + specific_week + ", start_date=" + start_date + ", end_date=" + end_date + "]";
	}

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

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	public String getService_type() {
		return service_type;
	}

	public void setService_type(String service_type) {
		this.service_type = service_type;
	}

	public int getIp_count() {
		return ip_count;
	}

	public void setIp_count(int ip_count) {
		this.ip_count = ip_count;
	}

	public String getInserted_id() {
		return inserted_id;
	}

	public void setInserted_id(String inserted_id) {
		this.inserted_id = inserted_id;
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

}
