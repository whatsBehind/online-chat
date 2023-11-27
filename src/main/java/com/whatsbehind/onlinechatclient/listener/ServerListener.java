package com.whatsbehind.onlinechatclient.listener;

import com.google.gson.Gson;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerListener extends Thread {
    private Socket socket;
    private User user;
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
                    case USER_LOGOFF:
                        logoff();
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage(Message message) {
        Printer.print("\n[Chat] %s: %s", message.getSender(), message.getContent());
    }

    private void logoff() throws IOException {
        socket.close();
        ServerListenerManager.remove(user);
        listening = false;
        Printer.print("Server Listener of user [%s] logoff", user.getId());
    }
}
