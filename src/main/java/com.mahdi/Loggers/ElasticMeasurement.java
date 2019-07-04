/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.Loggers;
import com.mahdi.Listeners.MyMessageListener;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.UUID;


/*
This class sets up the general options of the MQTT client and launches the
subscription to the MQTT Listener previously implemented.
The logs come from this class.
 */
public class ElasticMeasurement {

    private static final String BROKER = "tcp://app.icam.fr:1883";
         private static final String TOPIC0 = "peaktech/power";
        private static final String TOPIC = "ardgetti/1/power";
    private MqttClient mqttClient;

    public void setUp(String s) throws MqttException {
        UUID uuid = UUID.randomUUID();
        MemoryPersistence persistence = new MemoryPersistence();
        mqttClient = new MqttClient(BROKER, uuid.toString(), persistence);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        mqttClient.connect(options);
        mqttClient.subscribe(s, new MyMessageListener());
    }

    public void tearDown() throws MqttException {
        mqttClient.disconnect();
    }

    public void measure() {
        try {

            long delai = System.currentTimeMillis();

            this.setUp(TOPIC0);
            this.setUp(TOPIC);
/*
Uncomment in order to retrieve data for n seconds (delai)
 */
       /*     do { } while (delai + 3000 > System.currentTimeMillis());
            this.tearDown();
            System.exit(0);
*/
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        ElasticMeasurement em = new ElasticMeasurement();
        em.measure();
    }
}
