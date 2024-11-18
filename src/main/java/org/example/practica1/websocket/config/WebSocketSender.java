package org.example.practica1.websocket.config;

import java.io.IOException;

public interface WebSocketSender {

    void sendMessage(String message) throws IOException;

    void sendPeriodicMessages() throws IOException;

}
