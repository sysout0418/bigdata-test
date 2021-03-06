import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class MapExample {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("MapExample");
		JavaSparkContext sc = new JavaSparkContext(conf);

		// Parallelized with 2 partitions
		JavaRDD<String> x = sc.parallelize(Arrays.asList("spark", "rdd", "example", "sample", "example"), 2);

		// Word Count Map Example
		JavaRDD<Tuple2<String, Integer>> y1 = x.map(e -> new Tuple2<>(e, 1));
		List<Tuple2<String, Integer>> list1 = y1.collect();

		// Another example of making tuple with string and it's length
		JavaRDD<Tuple2<String, Integer>> y2 = x.map(e -> new Tuple2<>(e, e.length()));
		List<Tuple2<String, Integer>> list2 = y2.collect();
		
		sc.close();
		
		System.out.println(list1);
		System.out.println(list2);
	}

}
