package com.mahdi.Spark;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.junit.jupiter.api.Test;

public class SparkStreamingMQTT {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SparkStreamingMQTT.class);

    //This is a global static variable so that we can stop the stream
    //from the stored procedure
    private JavaStreamingContext jssc = null;

    String checkpointDirectory = "/checkpoint/";


    @Test
    public void processMQTT() throws InterruptedException {
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        LOG.info("************ Spark MQTT Streaming Starts now");
        // Create the spark application and set the name to MQTTgit remote add origin https://github.com/damakmahdi/Mqtt-Elasticsearch.git
        SparkConf sparkConf = new SparkConf().setAppName("MQTT").setMaster("local[*]");

        // Create the spark streaming context with a 'numSeconds' second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));
        jssc.checkpoint(checkpointDirectory);

        LOG.info("************ Subscribing to ICAM's Brocker : 'tcp://app.icam.fr:1883' starts now");
        LOG.info("************ Reading data from the Brocker's Topic : 'ardgetti/1/power' starts now");
        //2. MQTTUtils to collect MQTT messages
        JavaReceiverInputDStream<String> messages = MQTTUtils.createStream(jssc,
                "tcp://app.icam.fr:1883",
                "ardgetti/1/power");
        LOG.info("************ Data Processing starts now");
        //process the messages on the queue and save them to the database
        messages.print();
        LOG.info("************ Starting the context");
        jssc.start();
        jssc.awaitTermination();
    }
}