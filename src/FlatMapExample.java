import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class FlatMapExample {

	public static void main(String[] args) {
		JavaSparkContext sc = new JavaSparkContext("local", "FlatMapExample");

		// Parallelized with 2 partitions
		JavaRDD<String> rddX = sc.parallelize(Arrays.asList("spark rdd example", "sample example"), 2);

		// map operation will return List of Array in following case
		JavaRDD<String[]> rddY = rddX.map(e -> e.split(" "));
		List<String[]> listUsingMap = rddY.collect();

		// flatMap operation will return list of String in following case
		JavaRDD<String> rddY2 = rddX.flatMap(e -> Arrays.asList(e.split(" ")).iterator());
		List<String> listUsingFlatMap = rddY2.collect();
		
		sc.close();
		
		for (String[] strArray : listUsingMap) {
			System.out.println(Arrays.toString(strArray));
		}
		System.out.println(listUsingFlatMap);
	}

}
