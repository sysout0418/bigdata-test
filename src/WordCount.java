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

public class WordCount implements Serializable {
	public void execute(String inputPath, String outputFile) {
		/**
		 * Spark Lib 이용해서 Hadoop저장된 이터 호출
		 */
		SparkConf conf = new SparkConf().setMaster("local").setAppName("WordCount");
		JavaSparkContext sc = new JavaSparkContext(conf);
		// RDD 타입으로 Hadoop 데이터 호출(RDD란 분산되어 존재하는 데이터 요소들의 모임, 분산되어 있는 변경 불가능한 객체 모음)
		JavaRDD<String> input = sc.textFile(inputPath);
		JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {
			public Iterator<String> call(String x) {
				return Arrays.asList(x.split(" ")).iterator();
			}
		});
		JavaPairRDD<String, Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Tuple2<String, Integer> call(String x) {
				return new Tuple2(x, 1);
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			public Integer call(Integer x, Integer y) throws Exception {
				return x + y;
			}
		});
		try {
			/**
			 * JDBC 설정
			 * JDBC 참고용 jar은 사전에 Spark에 업로드 필요
			 */
			Class.forName("com.mysql.jdbc.Driver"); // 드라이버 이름 대소문자 주의
			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.0.103:3306/test", "root", "root1234");
			System.out.println("PASS STEP 1");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"select name,cnt,DATE_FORMAT(inserted_date,'%Y-%m-%d %H:%i:%s') as inserted_date from test");
			while (rs.next()) {
				System.out.println(
						rs.getString("name") + " : " + rs.getInt("cnt") + "==>" + rs.getString("inserted_date"));
			}
			rs.close();
			stmt.close();
			List<Tuple2<String, Integer>> output = counts.collect();
			for (Tuple2<?, ?> tuple : output) {
				System.out.println(tuple._1() + ": " + tuple._2());
				stmt = conn.createStatement();
				stmt.executeUpdate("insert into test(name,cnt,inserted_date) values('" + tuple._1() + "'," + tuple._2()
						+ ",now())");
				rs.close();
				stmt.close();
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"select name,cnt,DATE_FORMAT(inserted_date,'%Y-%m-%d %H:%i:%s') as inserted_date from test");
			while (rs.next()) {
				System.out.println(
						rs.getString("name") + " : " + rs.getInt("cnt") + "==>" + rs.getString("inserted_date"));
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		sc.stop();
	}

	public static void main(String[] args) {
		String inputFile = "hdfs://10.0.2.15:9000/logs/2018-03-28/*/SERVER-01.*.log"; // hadoop 파일 경로
//		String inputFile = "hdfs://192.168.0.101:9000/logs/2018-03-28/*/SERVER-01.*.log"; // hadoop 파일 경로
		String outputFile = "/hadoop/temp/"; // 미사용
		WordCount wc = new WordCount();
		wc.execute(inputFile, outputFile);
	}
}
