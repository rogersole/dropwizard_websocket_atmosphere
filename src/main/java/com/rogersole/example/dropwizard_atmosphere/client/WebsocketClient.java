package com.rogersole.example.dropwizard_atmosphere.client;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

/**
 * Class that implements a websocket client to test the current server.
 * 
 * @author rogersole
 *
 */
public class WebsocketClient {

    private static String BASE_URL = "ws://localhost:8080/websocket/";

    public static void main(String[] args) {

        WebSocketClient client = new WebSocketClient();
        SimpleSocket socketTimezone = new SimpleSocket("Japan");
        SimpleSocket socketGitHub = new SimpleSocket("Barcelona");
        SimpleSocket socketTuple =
                        new SimpleSocket("{\"email\":\"foo@example.com\", \"repository\":\"foobarbaz_"
                                        + (Math.round(Math.random() * 100)) + "\"}");

        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setHeader("Authorization", "Basic bmV3cmVsaWM6cXVhY2tnb2VzdGhlZHVjaw==");

        try {
            client.start();

            URI timezoneURI = new URI(BASE_URL + "timezone");
            client.connect(socketTimezone, timezoneURI, request);
            System.out.printf("Connecting to: %s%n", timezoneURI);
            socketTimezone.awaitClose(25, TimeUnit.SECONDS);

            URI githubURI = new URI(BASE_URL + "github/topactive");
            client.connect(socketGitHub, githubURI, request);
            System.out.printf("Connecting to: %s%n", githubURI);
            socketGitHub.awaitClose(45, TimeUnit.SECONDS);

            URI tupleURI = new URI(BASE_URL + "tuple");
            client.connect(socketTuple, tupleURI, request);
            System.out.printf("Connecting to: %s%n", tupleURI);
            socketTuple.awaitClose(25, TimeUnit.SECONDS);

        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            try {
                client.stop();
            }
            catch (Exception e) {}
        }
    }
}
