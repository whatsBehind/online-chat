package com.whatsbehind.onlinechatclient.service;

import com.google.gson.Gson;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageSender;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

@NoArgsConstructor
public class ChatService {
    private final Gson gson = new Gson();
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public void pullOnlineUsers(Socket socket, User user) throws IOException, ClassNotFoundException {
        Message request = Message.builder()
                .sender(user.getId())
                .receiver(MessageSender.ADMIN.toString())
                .type(MessageType.ONLINE_USERS)
                .timeStamp(new Date().toString()).build();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(request);


    }

//    public void printOnlineUser(Socket socket) {
//        ois = new ObjectInputStream(socket.getInputStream());
//        Message response = (Message) ois.readObject();
//        return gson.fromJson(response.getContent(), List.class);
//    }

    public Message receiveMessage(Socket socket) throws IOException, ClassNotFoundException {
        ois = new ObjectInputStream(socket.getInputStream());
        return (Message) ois.readObject();
    }

    public void sendMessage(Socket socket, User user, String message, String receiver) throws IOException {
        Message request = Message.builder()
                .sender(user.getId())
                .receiver(receiver)
                .type(MessageType.CHAT)
                .content(message)
                .timeStamp(new Date().toString()).build();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(request);
    }
}
