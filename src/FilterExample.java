import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class FilterExample {

	public static void main(String[] args) {
		JavaSparkContext sc = new JavaSparkContext("local", "FilterExample");

		// Filter Predicate
		Function<Integer, Boolean> filterPredicate = e -> e % 2 == 0;

		// Parallelized with 2 partitions
		JavaRDD<Integer> rddX = sc.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 2);

		// filter operation will return List of Array in following case
		JavaRDD<Integer> rddY = rddX.filter(filterPredicate);
		List<Integer> filteredList = rddY.collect();
		
		sc.close();
		
		System.out.println(filteredList);
	}

}
