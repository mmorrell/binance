package com.morrell;

import com.google.gson.Gson;
import com.morrell.binance.Kline;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {

    private final static Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());
    private final static Gson gson = new Gson();

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(1);
        HttpClient httpClient = HttpClient.newBuilder().executor(executor).build();
        WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder();
        WebSocket webSocket = webSocketBuilder.buildAsync(URI.create("wss://stream.binance.com:9443/ws/bnbbtc@kline_1m"), new WebSocket.Listener() {
            @Override
            public void onOpen(WebSocket webSocket) {
                LOGGER.info("CONNECTED");
                LOGGER.info("Sending thank you message.");
                //webSocket.sendText("Thanks for letting me in.", true);
                WebSocket.Listener.super.onOpen(webSocket);
            }
            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                //LOGGER.info(data.toString());
                String json = data.toString();
                double price = gson.fromJson(gson.fromJson(json, Map.class).get("k").toString(), Kline.class).getPrice();
                LOGGER.info("Price: " + price);

                if(!webSocket.isOutputClosed()) {
                    //webSocket.sendText("This is a message", true);
                }
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }
            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                LOGGER.info("Closed with status " + statusCode + ", reason: " + reason);
                executor.shutdown();
                return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
            }
        }).join();
        LOGGER.info("WebSocket created");
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "ok").thenRun(() -> LOGGER.info("Sent close"));
    }
}
