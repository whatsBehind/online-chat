package com.whatsbehind.onlinechatserver.thread_;

import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;
import com.whatsbehind.onlinechatserver.service.ChatService;
import com.whatsbehind.onlinechatserver.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

@AllArgsConstructor
@Getter
public class ClientListener extends Thread {
    private Socket client;
    private User user;
    private boolean listening = true;
    public ClientListener(Socket client, User user) {
        this.client = client;
        this.user = user;
    }

    private LoginService loginService = new LoginService();
    private ChatService chatService = new ChatService();

    public void run() {
        try {
            while(listening) {
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                Message response = (Message) ois.readObject();
                Printer.print("User %s sent request %s", user.getId(), response.toString());
                MessageType type = response.getType();
                switch(type) {
                    case USER_LOGOFF:
                        logoff();
                        break;
                    case ONLINE_USERS:
                        returnOnlineUsers(client);
                        break;
                    case CHAT:
                        sendMessage(response);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void logoff() {
        try {
            loginService.logoff(client, user);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ClientListenManager.remove(user);
        listening = false;
        Printer.print("User %s logoff", user.getId());
    }

    private void returnOnlineUsers(Socket socket) {
        try {
            chatService.getOnlineUsers(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(Message message) {
        try {
            Printer.print("Chat Message %s", message.toString());
            chatService.sendMessage(message.getSender(), message.getReceiver(), message.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
