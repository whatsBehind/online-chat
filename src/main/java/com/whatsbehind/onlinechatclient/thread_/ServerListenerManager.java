package com.whatsbehind.onlinechatclient.thread_;

import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerListenerManager {
    private final static Map<String, ServerListener> serverListeners = new HashMap<>();
    public static void add(Socket socket, User user) {
        ServerListener listener = new ServerListener(socket, user);
        serverListeners.put(user.getId(), listener);
        listener.start();
        Printer.print("Valid user %s login.", user.getId());
    }

    public static ServerListener get(User user) {
        final String id = user.getId();
        return serverListeners.get(id);
    }

    public static void remove(User user) {
        final String id = user.getId();
        serverListeners.remove(id);
    }

    public static Map<String, ServerListener> getServerListeners() {
        return serverListeners;
    }
}
