import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;

import scala.Tuple2;

public class WordForText implements Serializable {
	public void execute(String inputPath, String outputFile) {
		/**
		 * Spark Lib 이용해서 Hadoop저장된 이터 호출
		 */
		String word = "404";
		SparkConf conf = new SparkConf().setMaster("local").setAppName("WordCount");
		JavaSparkContext sc = new JavaSparkContext(conf);
		// RDD 타입으로 Hadoop 데이터 호출(RDD란 분산되어 존재하는 데이터 요소들의 모임, 분산되어 있는 변경 불가능한 객체 모음)
		JavaRDD<String> input = sc.textFile(inputPath);
		JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {
			public Iterator<String> call(String x) {
				System.out.println("Step 1 -------------------------------------");
				System.out.println("x : "+x);
				if(x.indexOf(word)>= 0){
					System.out.println("Contain "+word);
				}else {
					System.out.println("Not Contain "+word);
				}
				return Arrays.asList(x.split("\n")).iterator();
			}
		});
		JavaPairRDD<String, Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Tuple2<String, Integer> call(String x) {
				System.out.println("Step 2 -------------------------------------");
				System.out.println("x : "+x);
				return new Tuple2(x, 1);
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			public Integer call(Integer x, Integer y) throws Exception {
				return x + y;
			}
		});
		sc.stop();
	}

	public static void main(String[] args) {
		String inputFile = "hdfs://10.0.2.15:9000/logs/2018-04-10/*/SERVER-01.*.log"; // hadoop 파일 경로
//		String inputFile = "hdfs://192.168.0.101:9000/logs/2018-04-10/*/SERVER-01.*.log"; // hadoop 파일 경로
		String outputFile = "/hadoop/temp/"; // 미사용
		WordForText wc = new WordForText();
		wc.execute(inputFile, outputFile);
	}
}
