package com.nbreds.bigdata.common.util;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nbreds.bigdata.common.CommonConfig;

public class SparkContext {

	private static final Logger logger = LoggerFactory.getLogger(SparkContext.class);

	private JavaSparkContext context = null;
	private static String HDFSAddress = "";

	public SparkContext(Object cls) {
		if (context != null) {
			return;
		}

		HDFSAddress = CommonConfig.getInstance().getString("hdfs.server.address");
		String sparkAddress = CommonConfig.getInstance().getString("spark.server.address");

		logger.info("HDFS Address : " + HDFSAddress);
		logger.info("Spark Address : " + sparkAddress);

		context = new JavaSparkContext(new SparkConf().setAppName(cls.getClass().getName()).setMaster(sparkAddress));
	}
	
	public SparkContext(Object cls, String jarPath) {
		if (context != null) {
			return;
		}

		HDFSAddress = CommonConfig.getInstance().getString("hdfs.server.address");
		String sparkAddress = CommonConfig.getInstance().getString("spark.server.address");

		logger.info("HDFS Address : " + HDFSAddress);
		logger.info("Spark Address : " + sparkAddress);

		context = new JavaSparkContext(new SparkConf().setAppName(cls.getClass().getName()).setMaster(sparkAddress).setJars(new String[]{jarPath}));
	}

	public SparkContext(SparkConf conf) {
		if (context != null) {
			return;
		}

		context = new JavaSparkContext(conf);
	}

	public JavaSparkContext getContext() {
		return context;
	}

	public JavaRDD<String> textFile(String path) {
		if ("".equals(HDFSAddress)) {
			return null;
		}

		return context.textFile(HDFSAddress + path);
	}

	public JavaRDD<String> textFile(String path, int minPartition) {
		if ("".equals(HDFSAddress)) {
			return null;
		}

		return context.textFile(HDFSAddress + path, minPartition);
	}

	/**
	 * textFileFromDB
	 * DB 에 등록된 Job에 해당하는 File을 읽어들인다.
	 * 
	 * @param seq
	 * @return JavaRDD<String>
	 */
	public JavaRDD<String> textFileFromDB(String seq) {
		// TODO: DB 에 등록된 Job에 해당하는 File을 읽어들인다.
		return null;
	}
	
}
