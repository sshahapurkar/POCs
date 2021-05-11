import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.IOException;

public class SparkAppMain {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    public static void main(String[] args) throws IOException {
        SparkAppMain sam = new SparkAppMain();
        sam.processFile();
    }

    private void processFile(){
        SparkConf sparkConf = new SparkConf()
                .setAppName("Example Spark App");
        //.setMaster("local[*]"); // Delete this line when submitting to a cluster
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        JavaRDD<String> stringJavaRDD = sparkContext.textFile("/Users/suhas/projects/POCs/SparkProjects/HelloWorldSparkInJava/src/main/resources/nationalparks.csv");
        System.out.println("Number of lines in file = " + stringJavaRDD.count());
    }

    private void processStreamingData() {
        SparkConf conf = new SparkConf()
                .setMaster("local[*]")
                .setAppName("TradeDataProcessor");
        JavaStreamingContext streamingContext =
                new JavaStreamingContext(conf, Durations.seconds(5));
        Logger.getRootLogger().setLevel(Level.ERROR);

        // Receive streaming data from the source
        JavaReceiverInputDStream<String> lines = streamingContext.socketTextStream(HOST, PORT);
        lines.print();

        // Execute the Spark workflow defined above
        streamingContext.start();
        try {
            streamingContext.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}