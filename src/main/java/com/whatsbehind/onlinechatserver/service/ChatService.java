package com.whatsbehind.onlinechatserver.service;

import com.google.gson.Gson;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageSender;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatserver.thread_.ClientListenManager;
import com.whatsbehind.onlinechatserver.thread_.ClientListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatService {
    private Gson gson = new Gson();
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public void getOnlineUsers(Socket socket) throws IOException {
        Map<String, ClientListener> clientListeners = ClientListenManager.getClientListeners();
        List<String> onlineUsers = new ArrayList<>(clientListeners.keySet());
        Message response = Message.builder()
                .sender(MessageSender.ADMIN.toString())
                .type(MessageType.ONLINE_USERS)
                .content(gson.toJson(onlineUsers)).build();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(response);
    }

    public void sendMessage(String sender, String receiver, String message) throws IOException {
        Message request = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .type(MessageType.CHAT)
                .content(message).build();
        ClientListener clientListener = ClientListenManager.get(receiver);
        Socket socket = clientListener.getClient();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(request);
    }
}
