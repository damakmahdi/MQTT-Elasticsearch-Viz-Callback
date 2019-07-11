

package com.mahdi.WebSockets;
/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

import com.mahdi.ElasticIndexing.BulkIndexing;
import com.mahdi.Utils.ArimaUtils;
import com.mahdi.Utils.JavaSQLContextSingleton;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.junit.jupiter.api.Test;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.mahdi.Spark.SparkModelling.round;

@ClientEndpoint
public class WebSocketClient {
    List<Double> ardgList = new ArrayList<>();
    String kmeans ="kmeans "+"\n"+"VALUE     CLUSTER"+"\n";
    String basicstats="stats"+"\n";
    List<Row> ard = new ArrayList<>();
    List<Row> ard2 = new ArrayList<>();
    List<Row> peak = new ArrayList<>();
    List<Double> peakList = new ArrayList<Double>();
    BulkIndexing bulkIndex=new BulkIndexing();
    public JavaStreamingContext jssc=null;
    private String  broker="tcp://app.icam.fr:1883";
    private String topic ="ardgetti/1/power";
    private String topic1 ="peaktech/power";
    Session userSession = null;
    private MessageHandler messageHandler;
    URI uri = new URI("ws://localhost:8080/SimpleServlet_WAR/socket");
public int duration =5;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public WebSocketClient() throws URISyntaxException {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }
    @OnMessage
    public void onMessage(String message)  {
             if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {

        try {
            this.userSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static interface MessageHandler {

        public void handleMessage(String message);
    }



    @Test
    public void streamMQTTWebSockets()  throws  InterruptedException{
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        SparkConf sparkConf = new SparkConf().setAppName("MQTT").setMaster("local[*]");
        jssc = new JavaStreamingContext(sparkConf, Durations.milliseconds(170));
        JavaReceiverInputDStream<String> messagesArdg = MQTTUtils.createStream(jssc, broker, topic);
        messagesArdg.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd -> {
                    rdd.collect().forEach(
                            s -> {
                                this.sendMessage(s + " ardg");
                                bulkIndex.bulkApiTest("addd",Float.valueOf(s),System.currentTimeMillis());
                            }
                    );
                });
        JavaReceiverInputDStream<String> messagesPeak = MQTTUtils.createStream(jssc, broker, topic1);
        messagesPeak.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd ->{rdd.collect().forEach(s->{
                            this.sendMessage(s+" peak");
                             bulkIndex.bulkApiTest("pkkk",Float.valueOf(s),System.currentTimeMillis());
                        }
                );
                }
        );
        jssc.start();
        jssc.awaitTermination();}

    @Test
    public void streamIndexing()  throws  InterruptedException{
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        StructType schema = new StructType(new StructField[]{
                new StructField("value", DataTypes.FloatType, true, Metadata.empty())
        });
        SparkConf sparkConf = new SparkConf().setAppName("MQTT").setMaster("local[*]");
        jssc = new JavaStreamingContext(sparkConf, Durations.milliseconds(170));
        JavaReceiverInputDStream<String> messagesArdg = MQTTUtils.createStream(jssc, broker, topic);
        messagesArdg.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd -> {
                    rdd.collect().forEach(
                            s -> {
                                 bulkIndex.bulkApiTest("addd",Float.valueOf(s),System.currentTimeMillis());
                            }
                    );
                });
        JavaReceiverInputDStream<String> messagesPeak = MQTTUtils.createStream(jssc, broker, topic1);
        messagesPeak.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd ->{rdd.collect().forEach(s->{
                            bulkIndex.bulkApiTest("pkkk",Float.valueOf(s),System.currentTimeMillis());
                        }
                );
                }
        );
        jssc.start();
        jssc.awaitTermination();}

    @Test
    public void streamKMEANS()  throws  InterruptedException{
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        StructType schema = new StructType(new StructField[]{
                new StructField("value", DataTypes.FloatType, true, Metadata.empty())
        });
        SparkConf sparkConf = new SparkConf().setAppName("MQTT").setMaster("local[*]");
        jssc = new JavaStreamingContext(sparkConf, Durations.milliseconds(170));
        JavaReceiverInputDStream<String> messagesArdg = MQTTUtils.createStream(jssc, broker, topic);
        messagesArdg.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd -> {
                    rdd.collect().forEach(
                            s -> {
                                // bulkIndex.bulkApiTest("addd",Float.valueOf(s),System.currentTimeMillis());
                                SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
                                ard.add((RowFactory.create((Float.valueOf(s)))));
                                if (ard.size()==100) {
                                    Dataset<Row> df = sqlContext.createDataFrame(ard, schema);
                                    VectorAssembler assembler = new VectorAssembler()
                                            .setInputCols(new String[]{"value"})
                                            .setOutputCol("features");
                                    Dataset<Row> dataset = assembler.transform(df);
                                    // Trains a k-means model.
                                    KMeans kmeans = new KMeans().setK(3).setSeed(1L);
                                    KMeansModel model = kmeans.fit(dataset);
                                    // Make predictions
                                    Dataset<Row> predictions = model.transform(dataset);
                                    predictions.show();
                                    predictions.collectAsList().forEach(e-> this.kmeans = this.kmeans +"\n" +e.getAs("value")+" "+e.getAs("prediction")+"\n");
                                    // Evaluate clustering by computing Silhouette score
                                    ClusteringEvaluator evaluator = new ClusteringEvaluator();
                                    double silhouette = evaluator.evaluate(predictions);
                                    this.kmeans = this.kmeans +"silhouette=  "+silhouette+"\n";
                                    System.out.println("Silhouette with squared euclidean distance = " + silhouette);
                                    // Shows the result.
                                    Vector[] centers = model.clusterCenters();
                                    System.out.println("Cluster Centers ARDGETTI: ");
                                    for (Vector center : centers) {
                                        this.kmeans = this.kmeans +"   center =  "+center;
                                        System.out.println(center);
                                    }
                                    this.sendMessage(this.kmeans);
                                    this.kmeans ="kmeans ";
                                    ard.clear();
                                }
                            }
                    );
                }
                );
        JavaReceiverInputDStream<String> messagesPeak = MQTTUtils.createStream(jssc, broker, topic1);
        messagesPeak.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd ->{
                    rdd.collect().forEach(
                            s -> {
                                // bulkIndex.bulkApiTest("pkkk",Float.valueOf(s),System.currentTimeMillis());
                                /*
                                SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
                                peak.add((RowFactory.create((Float.valueOf(s)))));
                                if (peak.size()==50) {
                                    Dataset<Row> df = sqlContext.createDataFrame(peak, schema);
                                    VectorAssembler assembler = new VectorAssembler()
                                            .setInputCols(new String[]{"value"})
                                            .setOutputCol("features");
                                    Dataset<Row> dataset = assembler.transform(df);
                                    // Trains a k-means model.
                                    KMeans kmeans = new KMeans().setK(3).setSeed(1L);
                                    KMeansModel model = kmeans.fit(dataset);
                                    // Make predictions
                                    Dataset<Row> predictions = model.transform(dataset);
                                    predictions.show();
                                    // Evaluate clustering by computing Silhouette score
                                    ClusteringEvaluator evaluator = new ClusteringEvaluator();
                                    double silhouette = evaluator.evaluate(predictions);
                                    System.out.println("Silhouette with squared euclidean distance = " + silhouette);
                                    // Shows the result.
                                    Vector[] centers = model.clusterCenters();
                                    System.out.println("Cluster Centers PEAKTECH: ");
                                    for (Vector center : centers) {
                                        System.out.println(center);
                                    }
                                    peak.clear();
                                }

                                 */
                            }
                    );
                }
        );
        jssc.start();
        jssc.awaitTermination();}

    @Test
    public void test()  throws  InterruptedException{
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        StructType schema = new StructType(new StructField[]{
                new StructField("value", DataTypes.FloatType, true, Metadata.empty())
        });
        SparkConf sparkConf = new SparkConf().setAppName("MQTT").setMaster("local[*]");
        jssc = new JavaStreamingContext(sparkConf, Durations.milliseconds(170));
        JavaReceiverInputDStream<String> messagesArdg = MQTTUtils.createStream(jssc, broker, topic);
        messagesArdg.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd -> {
                    rdd.collect().forEach(
                            s -> {
                                this.sendMessage(s + " ardg");
                                // bulkIndex.bulkApiTest("addd",Float.valueOf(s),System.currentTimeMillis());
/*
                            ardgList.add(Double.valueOf(s));
                            if (ardgList.size()>150){
                                Double[] ard = ardgList.toArray(new Double[ardgList.size()]);
                                double[] ardgArray = ArrayUtils.toPrimitive(ard);
                                ArimaUtils at = new ArimaUtils();
                                 System.out.println(ardgArray.length);
                                at.forecast(ardgArray);
                                ardgList.clear();
                            }
 */
                                SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
                                ard.add((RowFactory.create((Float.valueOf(s)))));
                                Dataset<Row> df = sqlContext.createDataFrame(ard, schema);
                                if (ard.size()==50) {
                                    VectorAssembler assembler = new VectorAssembler()
                                            .setInputCols(new String[]{"value"})
                                            .setOutputCol("features");

                                    Dataset<Row> dataset = assembler.transform(df);
                                    // Trains a k-means model.
                                    KMeans kmeans = new KMeans().setK(3).setSeed(1L);
                                    KMeansModel model = kmeans.fit(dataset);

                                    // Make predictions
                                    Dataset<Row> predictions = model.transform(dataset);
                                    predictions.show();
                                    // Evaluate clustering by computing Silhouette score
                                    ClusteringEvaluator evaluator = new ClusteringEvaluator();
                                    double silhouette = evaluator.evaluate(predictions);
                                    System.out.println("Silhouette with squared euclidean distance = " + silhouette);
                                    // Shows the result.
                                    Vector[] centers = model.clusterCenters();
                                    System.out.println("Cluster Centers: ");
                                    for (Vector center : centers) {
                                        System.out.println(center);
                                    }
                                    ard.clear();
                                }
                            }
                    );


                });
        JavaReceiverInputDStream<String> messagesPeak = MQTTUtils.createStream(jssc, broker, topic1);
        messagesPeak.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd ->{rdd.collect().forEach(s->{
                            this.sendMessage(s+" peak");
                            // bulkIndex.bulkApiTest("pkkk",Float.valueOf(s),System.currentTimeMillis());
                            /*
                            peakList.add(Double.valueOf(s));
                            if (peakList.size()>150){
                                Double[] peak = peakList.toArray(new Double[peakList.size()]);
                                double[] peakArray = ArrayUtils.toPrimitive(peak);
                                ArimaUtils at = new ArimaUtils();
                                // System.out.println("prediction starts now ");
                                at.forecast(peakArray);
                                peakList.clear();
                            }

                             */

                        }
                );
                }
        );
        jssc.start();
        jssc.awaitTermination();}


    @Test
    public void streamStatistics()  throws  InterruptedException{
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        StructType schema = new StructType(new StructField[]{
                new StructField("value", DataTypes.FloatType, true, Metadata.empty())
        });
        SparkConf sparkConf = new SparkConf().setAppName("MQTT").setMaster("local[*]");
        jssc = new JavaStreamingContext(sparkConf, Durations.milliseconds(170));
        JavaReceiverInputDStream<String> messagesArdg = MQTTUtils.createStream(jssc, broker, topic);
        messagesArdg.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd -> {
                    rdd.collect().forEach(
                            s -> {
                                SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
                                ard.add((RowFactory.create((Float.valueOf(s)))));
                                if (ard.size()==100) {
                                    Dataset<Row> df = sqlContext.createDataFrame(ard, schema);
                                    //  df.show(10);
                                    df.describe().show();
                                    ard.clear();
                                }
                            }
                    );
                });
        JavaReceiverInputDStream<String> messagesPeak = MQTTUtils.createStream(jssc, broker, topic1);
        messagesPeak.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd ->{rdd.collect().forEach(s->{
                            SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
                            peak.add((RowFactory.create((Float.valueOf(s)))));
                            if (peak.size()==50) {
                                Dataset<Row> df = sqlContext.createDataFrame(peak, schema);
                                //  df.show(10);
                                df.describe().show();
                                peak.clear();
                            }
                        }
                );
                });
        jssc.start();
        jssc.awaitTermination();}

    @Test
    public void streamCustomModelling()  throws  InterruptedException{
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        StructType schema = new StructType(new StructField[]{
                new StructField("value", DataTypes.FloatType, true, Metadata.empty())
        });
        StructType schema2 = new StructType(new StructField[]{
                new StructField("value", DataTypes.FloatType, true, Metadata.empty()),
                new StructField("allure", DataTypes.StringType, true, Metadata.empty()),
                new StructField("pic", DataTypes.BooleanType, true, Metadata.empty())
        });
        SparkConf sparkConf = new SparkConf().setAppName("MQTT").setMaster("local[*]");
        jssc = new JavaStreamingContext(sparkConf, Durations.milliseconds(170));
        JavaReceiverInputDStream<String> messagesArdg = MQTTUtils.createStream(jssc, broker, topic);
        messagesArdg.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd -> {
                    rdd.collect().forEach(
                            s -> {
                                SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
                                ard.add((RowFactory.create((Float.valueOf(s)))));
                                Float previousValue=444F;
                                boolean pic=false;
                                String allure="decroissante";
                                Float nextValue;
                                int i=0;
                                int picNumber=0;
                                int crNumber=0;
                                int decNumber=0;
                                int stagNumber=0;
                                if (ard.size()==100) {
                                    Dataset<Row> df = sqlContext.createDataFrame(ard, schema);
                                    ard.clear();
                                    List<Row> ar=df.collectAsList();
                                    nextValue=round(ar.get(i+1).getFloat(0));
                                    for (Row r : df.collectAsList()){
                                        if(round(r.getFloat(0))>=round(previousValue)) {
                                        allure="croissante";
                                        crNumber=crNumber+1;
                                        if(round(r.getFloat(0))>nextValue ) {
                                            pic=true;
                                            picNumber=picNumber+1;
                                        } }
                                    if(round(round(r.getFloat(0)))==round(previousValue)) {
                                        allure="stagnante";
                                        stagNumber=stagNumber+1;
                                    }
                                    ard.add(RowFactory.create( r.getAs("value"), allure,pic));
                                    previousValue=round(r.getFloat(0));
                                    pic=false;
                                    if (allure.equals("decroissante")){decNumber=decNumber+1;}
                                    allure="decroissante";
                                    if (ar.size()-2>i){
                                        i++;} }
                                Dataset<Row> ds = sqlContext.createDataFrame(ard, schema2);
                             //   JavaEsSparkSQL.saveToEs(ds,"test");
                                ds.show(10);
                             //   ds.describe("value").show();
                                System.out.println("Le nombre de pics = "+picNumber);
                                System.out.println("Le nombre de segments croissants = "+crNumber);
                                System.out.println("Le nombre de segments decroissants = "+decNumber);
                                System.out.println("Le nombre de segments stagnants = "+stagNumber);
                                double p = picNumber*100  / df.count();
                                double c = crNumber*100  / df.count();
                                double d= decNumber*100  / df.count();
                                double ss = stagNumber*100  / df.count();
                                System.out.println("Le % des pic = "+ p+"%");
                                System.out.println("Le % des segments croissants = "+ c+"%");
                                System.out.println("Le % des segments decroissants = "+ d+"%");
                                System.out.println("Le % des segments stagnants = "+ ss+"%");
                                ard.clear();
                                }
                            }
                    );


                });
        JavaReceiverInputDStream<String> messagesPeak = MQTTUtils.createStream(jssc, broker, topic1);
        messagesPeak.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd ->{rdd.collect().forEach(
                        s -> {
                            SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
                            peak.add((RowFactory.create((Float.valueOf(s)))));
                            Float previousValue=444F;
                            boolean pic=false;
                            String allure="decroissante";
                            Float nextValue;
                            int i=0;
                            int picNumber=0;
                            int crNumber=0;
                            int decNumber=0;
                            int stagNumber=0;
                            if (peak.size()==50) {
                                Dataset<Row> df = sqlContext.createDataFrame(peak, schema);
                                peak.clear();
                                List<Row> ar=df.collectAsList();
                                nextValue=round(ar.get(i+1).getFloat(0));
                                for (Row r : df.collectAsList()){
                                    if(round(r.getFloat(0))>=round(previousValue)) {
                                        allure="croissante";
                                        crNumber=crNumber+1;
                                        if(round(r.getFloat(0))>nextValue ) {
                                            pic=true;
                                            picNumber=picNumber+1;
                                        } }
                                    if(round(round(r.getFloat(0)))==round(previousValue)) {
                                        allure="stagnante";
                                        stagNumber=stagNumber+1;
                                    }
                                    peak.add(RowFactory.create( r.getAs("value"), allure,pic));
                                    previousValue=round(r.getFloat(0));
                                    pic=false;
                                    if (allure.equals("decroissante")){decNumber=decNumber+1;}
                                    allure="decroissante";
                                    if (ar.size()-2>i){
                                        i++;} }
                                Dataset<Row> ds = sqlContext.createDataFrame(peak, schema2);
                                //   JavaEsSparkSQL.saveToEs(ds,"test");
                                ds.show(10);
                               // ds.describe("value").show();
                                System.out.println("Le nombre de pics = "+picNumber);
                                System.out.println("Le nombre de segments croissants = "+crNumber);
                                System.out.println("Le nombre de segments decroissants = "+decNumber);
                                System.out.println("Le nombre de segments stagnants = "+stagNumber);
                                double p = picNumber*100  / df.count();
                                double c = crNumber*100  / df.count();
                                double d= decNumber*100  / df.count();
                                double ss = stagNumber*100  / df.count();
                                System.out.println("Le % des pic = "+ p+"%");
                                System.out.println("Le % des segments croissants = "+ c+"%");
                                System.out.println("Le % des segments decroissants = "+ d+"%");
                                System.out.println("Le % des segments stagnants = "+ ss+"%");
                                peak.clear();
                            }
                        }
                );


                });
        jssc.start();
        jssc.awaitTermination();}

    @Test
    public void streamArima()  throws  InterruptedException{
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        StructType schema = new StructType(new StructField[]{
                new StructField("value", DataTypes.FloatType, true, Metadata.empty())
        });
        SparkConf sparkConf = new SparkConf().setAppName("MQTT").setMaster("local[*]");
        jssc = new JavaStreamingContext(sparkConf, Durations.milliseconds(170));
        JavaReceiverInputDStream<String> messagesArdg = MQTTUtils.createStream(jssc, broker, topic);
        messagesArdg.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd -> {
                    rdd.collect().forEach(
                            s -> {
                            ardgList.add(Double.valueOf(s));
                            if (ardgList.size()>150){
                                Double[] ard = ardgList.toArray(new Double[ardgList.size()]);
                                double[] ardgArray = ArrayUtils.toPrimitive(ard);
                                ArimaUtils at = new ArimaUtils();
                                 System.out.println(ardgArray.length);
                                at.forecast(ardgArray);
                                ardgList.clear();
                            }
                            }
                    );
                });
        JavaReceiverInputDStream<String> messagesPeak = MQTTUtils.createStream(jssc, broker, topic1);
        messagesPeak.foreachRDD((VoidFunction<JavaRDD<String>>)
                rdd ->{rdd.collect().forEach(s->{
                            this.sendMessage(s+" peak");
                            peakList.add(Double.valueOf(s));
                            if (peakList.size()>150){
                                Double[] peak = peakList.toArray(new Double[peakList.size()]);
                                double[] peakArray = ArrayUtils.toPrimitive(peak);
                                ArimaUtils at = new ArimaUtils();
                                // System.out.println("prediction starts now ");
                                at.forecast(peakArray);
                                peakList.clear();
                            }
                        }
                );
                }
        );
        jssc.start();
        jssc.awaitTermination();}


}
