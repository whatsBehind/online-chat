package com.whatsbehind.onlinechatserver.publisher;

import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessagePublisherManager {
    private final static Map<String, MessagePublisher> publishers = new ConcurrentHashMap<>();
    public static boolean contains(User user) {
        final String id = user.getId();
        return publishers.containsKey(id);
    }
    public static MessagePublisher add(User user, Socket socket) {
        if (contains(user)) {
            return publishers.get(user);
        }
        final String id = user.getId();
        MessagePublisher publisher = new MessagePublisher(user, socket);
        return MessagePublisherManager.publishers.put(id, publisher);
    }

    public static MessagePublisher get(String userId) {
        return publishers.get(userId);
    }
    public static MessagePublisher get(User user) {
        return get(user.getId());
    }

    public static void remove(User user) {
        publishers.remove(user.getId());
    }

    public static boolean login(User user, Socket socket) throws IOException {
        boolean login = false;
        Message response = Message.builder()
                .receiver(user.getId())
                .timeStamp(new Date().toString()).build();
        if(contains(user)) {
            response.setType(MessageType.LOGIN_FAILURE);
            Printer.print("User [%s] already login", user.getId());
        } else {
            response.setType(MessageType.LOGIN_SUCCESS);
            add(user, socket);
            login = true;
            Printer.print("Message Publisher of user [%s] start", user.getId());
        }
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(response);
        return login;
    }

    public static List<String> getOnlineUsers() {
        return new ArrayList<>(publishers.keySet());
    }
}
