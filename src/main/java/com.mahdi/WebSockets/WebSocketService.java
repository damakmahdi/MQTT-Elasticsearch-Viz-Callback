/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.WebSockets;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
public class WebSocketService  implements Runnable   {

    private static WebSocketService instance;
    private static Map<String, Session> sessions = new HashMap<>();
    private JavaStreamingContext jssc = null;


    //Singleton
    public static void initialize() {
        if (instance == null) {
            instance = new WebSocketService();
            new Thread(instance).start();
        }
    }

    public static void add(Session s)
    {

        sessions.put(s.getId(), s);
    }

    public void runs(String message) {

            try {

                for (String key : sessions.keySet()) {

                    Session s = sessions.get(key);

                    if (s.isOpen()) {
                        s.getBasicRemote().sendText(message);
                    } else {
                        sessions.remove(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void run()  {
    }

    public static void main(String[] args)  throws InterruptedException{
    }
}