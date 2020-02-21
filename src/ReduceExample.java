import java.util.Arrays;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

public class ReduceExample {

	public static void main(String[] args) {
		JavaSparkContext sc = new JavaSparkContext("local", "ReduceExample");

		// Reduce Function for cumulative sum
		Function2<Integer, Integer, Integer> reduceSumFunc = (accum, n) -> (accum + n);

		// Reduce Function for cumulative multiplication
		Function2<Integer, Integer, Integer> reduceMulFunc = (accum, n) -> (accum * n);

		// Parallelized with 2 partitions
		JavaRDD<Integer> rddX = sc.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 2);

		// cumulative sum
		Integer cSum = rddX.reduce(reduceSumFunc);
		// another way to write
		Integer cSumInline = rddX.reduce((accum, n) -> (accum + n));

		// cumulative multiplication
		Integer cMul = rddX.reduce(reduceMulFunc);
		// another way to write
		Integer cMulInline = rddX.reduce((accum, n) -> (accum * n));
		
		sc.close();
		
		System.out.println("cSum: " + cSum + ", cSumInline: " + cSumInline + "\ncMul: " + cMul + ", cMulInline: " + cMulInline);
	}

}
