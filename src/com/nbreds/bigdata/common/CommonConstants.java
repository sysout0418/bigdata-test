package com.nbreds.bigdata.common;

/**
 * <pre>
 * Common Constants
 * 전역으로 사용할 Static 부분 선언해두는 부분
 * </pre>
 * @author branz
 * @since 2018.04
 * */
public class CommonConstants {
	
	public static boolean isRunning = false;
	public static int maxRunCheckCount = 10;
	
	/*
	 * Test용 프로세스를 싱글톤으로 사용하기 위해 선언
	 * 항상 IsRunning과 RunCheckCount를 쌍으로 선언 
	 * maxRunCheckCount이상 동작하지 못했을시에 이전에 동작중인 프로세스 이상으로 판단하고 신규 동작할수있도록 함
	 */
	public static boolean testIsRunning = false;
	public static int testRunCheckCount = 0;
	
	/*
	 * SmsJob 싱글톤 설정
	 */
	public static boolean smsIsRunning = false;
	public static int smsRunCheckCount = 0;
	
	/*
	 * ScanError 싱글톤 설정
	 */
	public static boolean scanErrorIsRunning = false;
	public static int scanErrorRunCheckCount = 0;
	
	/*
	 * CountError 싱글톤 설정
	 */
	public static boolean countErrorIsRunning = false;
	public static int countErrorRunCheckCount = 0;
	
	/*
	 * CountError 싱글톤 설정
	 */
	public static boolean countIPIsRunning = false;
	public static int countIPRunCheckCount = 0;
	
	/*
	 * ErrorStatisticsHourJob
	 */
	public static boolean errorStatisticsHourIsRunning = false;
	public static int errorStatisticsHourRunCheckCount = 0;
	
	/*
	 * ErrorStatisticsDayJob
	 */
	public static boolean errorStatisticsDayIsRunning = false;
	public static int errorStatisticsDayRunCheckCount = 0;
	
	/*
	 * ErrorStatisticsWeekJob
	 */
	public static boolean errorStatisticsWeekIsRunning = false;
	public static int errorStatisticsWeekRunCheckCount = 0;
	
	/*
	 * ErrorStatisticsMonthJob
	 */
	public static boolean errorStatisticsMonthIsRunning = false;
	public static int errorStatisticsMonthRunCheckCount = 0;
	
	/*
	 * ErrorStatisticsYearJob
	 */
	public static boolean errorStatisticsYearIsRunning = false;
	public static int errorStatisticsYearRunCheckCount = 0;
	
	/*
	 * IPStatisticsHourJob
	 */
	public static boolean IPStatisticsHourIsRunning = false;
	public static int IPStatisticsHourRunCheckCount = 0;
	
	/*
	 * IPStatisticsDayJob
	 */
	public static boolean IPStatisticsDayIsRunning = false;
	public static int IPStatisticsDayRunCheckCount = 0;
	
	/*
	 * IPStatisticsWeekJob
	 */
	public static boolean IPStatisticsWeekIsRunning = false;
	public static int IPStatisticsWeekRunCheckCount = 0;
	
	/*
	 * IPStatisticsMonthJob
	 */
	public static boolean IPStatisticsMonthIsRunning = false;
	public static int IPStatisticsMonthRunCheckCount = 0;
	
	/*
	 * IPStatisticsYearJob
	 */
	public static boolean IPStatisticsYearIsRunning = false;
	public static int IPStatisticsYearRunCheckCount = 0;
	
}
