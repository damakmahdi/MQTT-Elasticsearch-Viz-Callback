/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.WebSockets;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ServerEndpoint("/socket")
public class WebSocketEndpoint {
public String ss;
public WebSocketService ps=new WebSocketService();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen::" + session.getId());
        // Access request parameters from URL query String.
        // If a client subscribes to STREAM, add Session to WebSocketService.
        Map<String, List<String>> params = session.getRequestParameterMap();
        if (params.get("push") != null && (params.get("push").get(0).equals("Stream"))) {
            WebSocketService.initialize();
            WebSocketService.add(session);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("From Session=" + session.getId() + " Message=" + message);

        this.ps.run(message);
        try {
            session.getBasicRemote().sendText("Hello Client " + session.getId() + "!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("onError::" + t.getMessage());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose::" +  session.getId());
    }
}