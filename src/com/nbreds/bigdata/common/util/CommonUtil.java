package com.nbreds.bigdata.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.spark.api.java.function.Function;

public class CommonUtil {
	
	private static String FILTER_VALUE = "";

	/**
	 * getDate 현재 날짜 return (yyyyMMdd)
	 * 
	 * @return String
	 */
	public static String getDate() {
		
		SimpleDateFormat dfToDate = new SimpleDateFormat("yyyyMMdd");
		return dfToDate.format(Calendar.getInstance().getTime());
		
	}

	/**
	 * getTime 현재 시간 return (HHmmss)
	 * 
	 * @return String
	 */
	public static String getTime() {
		
		SimpleDateFormat dfToDate = new SimpleDateFormat("HHmmss");
		return dfToDate.format(Calendar.getInstance().getTime());
		
	}

	/**
	 * getDateTime format 날짜 return
	 * 
	 * @param format
	 * @return String
	 */
	public static String getDateTime(String format) {
		
		SimpleDateFormat dfToDate = new SimpleDateFormat(format);
		return dfToDate.format(Calendar.getInstance().getTime());
		
	}
	
	/**
	 * getPreviousTime format, 이전 시간 계산 return
	 * 
	 * @param format
	 * @param Previous time 이전 시간
	 * @return String
	 */
	public static String getPreviousTime(String format, int previousTime, int amount) {
		
		SimpleDateFormat dfToDate = new SimpleDateFormat(format);

		// 현재 시간 구하기
		Calendar cal = Calendar.getInstance();
		
		// 1시간 전
		cal.add(previousTime, amount);
		
		return dfToDate.format(cal.getTime());
		
	}
	
	/**
	 * getSpecificWeek 현재 주차 구하기 return
	 * 
	 * @return int
	 */
	public static int getSpecificWeek() {
		
		Calendar cal = Calendar.getInstance();
		int week = (int) cal.get(Calendar.WEEK_OF_YEAR);
		return week - 1;
		
	}

	/**
	 * setFilterValue set FILTER_VALUE
	 * 
	 * @param String
	 * @return void
	 */
	public static void setFilterValue(String value) {
		
		FILTER_VALUE = value;
		
	}

	/**
	 * getFilterValue get FILTER_VALUE JavaRDD filtering
	 * 
	 * @return String
	 */
	public static String getFilterValue() {
		
		return FILTER_VALUE;
		
	}

	/**
	 * sparkFilter FILTER_VALUE 에 해당하는 데이터 filtering
	 */
	@SuppressWarnings("rawtypes")
	public static Function sparkFilter = new Function<String, Boolean>() {
		
		private static final long serialVersionUID = 1L;

		@Override
		public Boolean call(String arg0) throws Exception {
			if (arg0.indexOf(FILTER_VALUE) > 0) {
				return true;
			} else {
				return false;
			}
		}

	};
	
}
