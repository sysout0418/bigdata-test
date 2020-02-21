import java.util.Arrays;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class GroupByExample {

	public static void main(String[] args) {
		JavaSparkContext sc = new JavaSparkContext("local", "GroupByExample");

		// Parallelized with 2 partitions
		JavaRDD<String> rddX = sc.parallelize(
				Arrays.asList("Joseph", "Jimmy", "Tina", "Thomas", "James",
						"Cory", "Christine", "Jackeline", "Juan"), 3);

		JavaPairRDD<Character, Iterable<String>> rddY = rddX.groupBy(word -> word.charAt(0));

		System.out.println(rddY.collect());

		sc.close();
	}

}
