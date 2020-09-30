package main.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint(value = "/chat")
public class WebsocketEndpoint {

    private Session session;
    private String message;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        // Get session and WebSocket connection
        System.out.println("open");
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // Handle new messages
        System.out.println("Message");
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // WebSocket connection closes
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        System.out.println(session);
    }
}
