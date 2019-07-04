/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.Listeners;

import com.mahdi.ElasticIndexing.BulkIndexing;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/*
Implementation of the MQTT Message Listener which index every arriving message into Elasticsearch automatically
 */
public class MyMessageListener implements IMqttMessageListener {
    public Long delai ;
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        byte[] payload = message.getPayload();
        String content = new String(payload);
        Float value = Float.valueOf(content);

        //ElasticIndexing ei = new ElasticIndexing();
        BulkIndexing bulkIndex=new BulkIndexing();
        if (topic.equals("ardgetti/1/power")) {
            //   ei.indexApiSyncTest("ardgettipower", value, time);
            delai = System.currentTimeMillis();
            bulkIndex.bulkApiTest("ardgettipower",value,delai);
        }
        if (topic.equals("peaktech/power")) {
            delai = System.currentTimeMillis();
            // ei.indexApiSyncTest("peaktechpower", value, time);
            bulkIndex.bulkApiTest("peaktechpower",value,delai);
        }
        System.out.println(topic + " value= " + value + " timestamp= " + delai);
    }
}