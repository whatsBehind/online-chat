package com.whatsbehind.onlinechatserver.listener;

import com.google.gson.Gson;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.onlinechatservice.ConnectResponse;
import com.whatsbehind.onlinechatcommon.model.user.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientListenerManager {
    private final static Map<String, ClientListener> clientListeners = new ConcurrentHashMap<>();
    private final static Gson gson = new Gson();
    private static ObjectOutputStream oos;

    public static void add(Socket socket, User user) throws IOException {
        Message response = Message.builder()
                .type(MessageType.CONNECT)
                .content(gson.toJson(ConnectResponse.builder().successful(true).build()))
                .timeStamp(new Date().toString()).build();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(response);
        ClientListener listener = new ClientListener(socket, user);
        clientListeners.put(user.getId(), listener);
        listener.start();
    }

    public static ClientListener get(User user) {
        final String id = user.getId();
        return clientListeners.get(id);
    }

    public static ClientListener get(String id) {
        return clientListeners.get(id);
    }

    public static void remove(User user) {
        final String id = user.getId();
        clientListeners.remove(id);
    }

    public static Map<String, ClientListener> getClientListeners() {
        return clientListeners;
    }
}
