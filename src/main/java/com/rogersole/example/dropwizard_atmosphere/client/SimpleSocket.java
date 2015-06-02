package com.rogersole.example.dropwizard_atmosphere.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *  Basic Client Socket that sends a message on connection.
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SimpleSocket {

    private final CountDownLatch closeLatch;
    private final String         messageToSend;

    private Session              session;

    public SimpleSocket(String messageToSend) {
        this.closeLatch = new CountDownLatch(1);
        this.messageToSend = messageToSend;
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("CONNECTION CLOSED: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("GOT CONNECT: %s%n", session);
        this.session = session;
        session.getRemote().sendStringByFuture(messageToSend);
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        System.out.printf("SERVER RESPONSE: %s%n", msg);
        this.session.close(StatusCode.NORMAL, "Received answer!");
    }
}
