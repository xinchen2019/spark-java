package com.apple.core;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @Program: spark-java
 * @ClassName: GroupTop3
 * @Description: TODO
 * @Author Mr.Apple
 * @Create: 2021-08-09 13:51
 * @Version 1.1.0
 **/
public class GroupTop3 {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("GroupTop3")
                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("data/score.txt");
        JavaPairRDD<String, Integer> pairs = lines.mapToPair(
                new PairFunction<String, String, Integer>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, Integer> call(String line) throws Exception {
                        String[] lineSplited = line.split(" ");
                        return new Tuple2<String, Integer>(lineSplited[0], Integer.parseInt(lineSplited[1]));
                    }
                });
        JavaPairRDD<String, Iterable<Integer>> groupedPairs = pairs.groupByKey();
        JavaPairRDD<String, Iterable<Integer>> top3Score = groupedPairs.mapToPair(
                new PairFunction<Tuple2<String, Iterable<Integer>>, String, Iterable<Integer>>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, Iterable<Integer>> call(Tuple2<String, Iterable<Integer>> classScores) throws Exception {
                        Integer[] top3 = new Integer[3];
                        String className = classScores._1;
                        Iterator<Integer> scores = classScores._2.iterator();
                        while (scores.hasNext()) {
                            Integer score = scores.next();
                            for (int i = 0; i < top3.length; i++) {
                                if (top3[i] == null) {
                                    top3[i] = score;
                                    break;
                                } else if (score > top3[i]) {
                                    for (int j = 2; j > i; j--) {
                                        top3[j] = top3[j - 1];
                                    }
                                    top3[i] = score;
                                    break;
                                }
                            }
                        }
                        return new Tuple2<String, Iterable<Integer>>(className, Arrays.asList(top3));
                    }
                });

        top3Score.foreach(new VoidFunction<Tuple2<String, Iterable<Integer>>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void call(Tuple2<String, Iterable<Integer>> t) throws Exception {
                System.out.println("class: " + t._1);
                Iterator<Integer> scoreIterator = t._2.iterator();
                while (scoreIterator.hasNext()) {
                    Integer score = scoreIterator.next();
                    System.out.println(score);
                }
                System.out.println("=======================================");
            }
        });
        sc.close();
    }
}
