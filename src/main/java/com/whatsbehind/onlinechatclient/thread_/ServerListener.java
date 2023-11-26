package com.whatsbehind.onlinechatclient.thread_;

import com.google.gson.Gson;
import com.whatsbehind.onlinechatclient.service.ChatService;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class ServerListener extends Thread {
    private Socket socket;
    private User user;
    private ChatService chatService = new ChatService();
    private boolean listening = true;
    private Gson gson = new Gson();
    public ServerListener(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
    }
    public void run() {
        try {
            while(listening) {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message response = (Message) ois.readObject();
                MessageType type = response.getType();
                switch(type) {
                    case CHAT:
                        receiveMessage(response);
                        break;
                    case ONLINE_USERS:
                        printOnlineUsers(response);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage(Message message) {
        Printer.print("Received message %s from user %s", message.getContent(), message.getSender());
    }

    private void printOnlineUsers(Message response) {
        List<String> users = gson.fromJson(response.getContent(), List.class);
        System.out.println("Online users: ");
        for (String user : users) {
            System.out.println(user);
        }
    }
}
