package com.whatsbehind.onlinechatserver.thread_;

import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientListenManager {
    private final static Map<String, ClientListener> clientListeners = new HashMap<>();
    public static void add(Socket client, User user) throws IOException {
        ClientListener listener = new ClientListener(client, user);
        clientListeners.put(user.getId(), listener);
        listener.start();
        Printer.print("Valid user %s login.", user.getId());
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
