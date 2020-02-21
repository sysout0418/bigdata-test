package com.nbreds.bigdata.test.job;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nbreds.bigdata.common.CommonConstants;
import com.nbreds.bigdata.test.dao.TestDao;

/**
 * <pre>
 * TEST
 * </pre>
 * @author branz
 * @since 2018.04
 * */
public class TestJob implements Job{
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".")+1, this.getClass().getName().length());
		System.out.println(className+" Start");
		if(CommonConstants.testIsRunning) {
			System.out.println("Process Still Running....");
			CommonConstants.testRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로이 시작
			 */
			if(CommonConstants.testRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.testRunCheckCount = 0;
			}else {
				return;
			}
		}else {
			CommonConstants.testIsRunning = true;
		}
		
//		JobDataMap jobMap = arg0.getJobDetail().getJobDataMap();
//		String user = jobMap.getString("USER");
//		String findWord = jobMap.getString("GETNAME");
//		String inputPath = jobMap.getString("GETURL");
//		/**
//		 * Spark Lib 이용해서 Hadoop저장된 이터 호출
//		 */
//		SparkConf conf = new SparkConf().setMaster("local").setAppName("WordCount");
//		JavaSparkContext sc = new JavaSparkContext(conf);
//		// RDD 타입으로 Hadoop 데이터 호출(RDD란 분산되어 존재하는 데이터 요소들의 모임, 분산되어 있는 변경 불가능한 객체 모음)
//		JavaRDD<String> input = sc.textFile(inputPath);
//		JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {
//			public Iterator<String> call(String x) {
//				return Arrays.asList(x.split("\n")).iterator();
//			}
//		});
//		JavaPairRDD<String, Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
//			@SuppressWarnings({ "rawtypes", "unchecked" })
//			public Tuple2<String, Integer> call(String x) {
//				return new Tuple2(x, 1);
//			}
//		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
//			public Integer call(Integer x, Integer y) throws Exception {
//				return x + y;
//			}
//		});
//		sc.stop();
		try {
			TestDao dao = TestDao.getInstance();
			List<?> testList = dao.testSelect();
			System.out.println("testList : "+testList);
			Iterator<?> it = testList.iterator();
			while(it.hasNext()) {
				HashMap<?,?> map = (HashMap<?,?>) it.next();
				System.out.println("map : "+map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			CommonConstants.testIsRunning = false;
		}
		
	}
	
}
