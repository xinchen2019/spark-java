package com.apple.core.upgrade;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Program: spark-java
 * @ClassName: PlusClosureVariable
 * @Description: TODO
 * @Author Mr.Apple
 * @Create: 2021-08-27 22:52
 * @Version 1.1.0
 **/
public class PrintElement {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("PrintElement")
                .set("spark.default.parallelism", "2")
                .setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> numbersRDD = sc.parallelize(numbers);
        List<Integer> closureNumber = new ArrayList<Integer>();
        closureNumber.add(0);
        numbersRDD.foreach(new VoidFunction<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void call(Integer num) throws Exception {
                System.out.println(num);
            }
        });
        sc.close();
    }
}
