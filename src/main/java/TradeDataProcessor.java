import com.google.gson.Gson;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import java.io.FileReader;

public class TradeDataProcessor {
            private static final String HOST = "localhost";
            private static final int PORT = 9999;

            public static void main(String[] args) {
                // Configure and initialize the SparkStreamingContext
                SparkConf conf = new SparkConf()
                    .setMaster("local[*]")
                    .setAppName("TradeDataProcessor");
                JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(5));
                Logger.getRootLogger().setLevel(Level.ERROR);
                JavaReceiverInputDStream<String> lines = streamingContext.socketTextStream(HOST, PORT);

                lines.foreachRDD(rdd -> {

                    if(!rdd.isEmpty() )
                    {
                        rdd.foreach(json -> {
                            Gson gson = new Gson();
                            TradePOJO tPojo = gson.fromJson(json, TradePOJO.class);

                            MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
                            String fileName = "/Users/suhas/projects/POCs/SparkProjects/HelloWorldSparkInJava/src/main/resources/ESMAEligibility-rule.yml";
                            Rule eligibilityRules = null;
                            try {
                                eligibilityRules = ruleFactory.createRule(new FileReader(fileName));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // create a rule set
                            Rules rules = new Rules();
                            rules.register(eligibilityRules);

                            //create a default rules engine and fire rules on known facts
                            RulesEngine rulesEngine = new DefaultRulesEngine();
                            Facts facts = new Facts();
                            facts.put("trade", tPojo);
                            rulesEngine.fire(rules, facts);
                        });
                    }
                });

                // Execute the Spark workflow defined above
                streamingContext.start();
                try {
                    streamingContext.awaitTermination();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
}