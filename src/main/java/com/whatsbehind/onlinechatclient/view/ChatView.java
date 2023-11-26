package com.whatsbehind.onlinechatclient.view;

import com.whatsbehind.onlinechatclient.service.ChatService;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;
import com.whatsbehind.onlinechatcommon.utility.Scanner_;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.Socket;

@NoArgsConstructor
public class ChatView {

    private boolean rendering = true;
    private Socket socket;
    private User user;
    private String receiverId;
    private ChatService chatService = new ChatService();

    public ChatView(Socket socket, User user, String receiverId) {
        this.socket = socket;
        this.user = user;
        this.receiverId = receiverId;
    }

    public static void main(String[] args) {

    }

    public void render() {
        Printer.print("Start chatting with user %s", receiverId);
        while (rendering) {
            String message = Scanner_.scanLine("Type the message you want to send (Enter 9 if you want to exit chat): ");
            if (message.equals("9")) {
                rendering = false;
                continue;
            }
            send(message, receiverId);
        }
    }

    private void send(String message, String receiverId) {
        try {
            chatService.sendMessage(socket, user, message, receiverId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
