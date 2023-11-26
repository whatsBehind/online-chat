package com.whatsbehind.onlinechatclient.view;

import com.whatsbehind.onlinechatclient.service.ChatService;
import com.whatsbehind.onlinechatclient.service.LoginService;
import com.whatsbehind.onlinechatclient.thread_.ServerListener;
import com.whatsbehind.onlinechatclient.thread_.ServerListenerManager;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;
import com.whatsbehind.onlinechatcommon.utility.Scanner_;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

@NoArgsConstructor
public class LoginView {

    private final static int LOGIN_RETRY_LIMIT = 3;
    private final LoginService loginService = new LoginService();
    private final ChatService chatService = new ChatService();
    private User user;
    private Socket socket;

    public static void main(String[] args) throws IOException {
        new LoginView().render();
    }

    private boolean rendering = true;
    private int loginRetryCount = 1;

    public void render() {
        boolean login = login();
        if (!login) {
            System.out.println("Too many retries!");
            return;
        }
        Printer.print("User %s login successfully. Welcome to Online Chat system", user.getId());

        while(rendering) {
            System.out.println("Online Chat system provides below functions: ");
            System.out.println("\t\t1: Pull online user list.");
            System.out.println("\t\t2: Send message to online user.");
            System.out.println("\t\t9: Exit system.");
            final String key = Scanner_.scanLine("Please select one function: ");
            switch(key) {
                case "1":
                    Printer.functionDelimiter();
                    System.out.println("Pull online user list.");
                    pullOnlineUsers();
                    break;
                case "2":
                    Printer.functionDelimiter();
                    chat();
                    break;
                case "9":
                    Printer.functionDelimiter();
                    logoff();
                    System.out.println("You exited Online Chat system.");
                    rendering = false;
                    break;
                default:
                    System.out.println("Wrong input. Please re-enter");
                    break;
            }

        }
    }

    private boolean login() {
        boolean login = false;
        System.out.println("================= User Login =================");
        while(loginRetryCount++ <= LOGIN_RETRY_LIMIT) {
            final String userId = Scanner_.scanLine("\t\tYour name: ");
            final String password = Scanner_.scanLine("\t\tYour password: ");
            try {
                socket = new Socket(InetAddress.getLocalHost(), 9999);
            } catch (IOException e) {
                Printer.print("User %s failed to connect to server.", userId);
                continue;
            }
            user = User.builder().id(userId).password(password).build();
            try {
                login = loginService.login(socket, user);
                ServerListenerManager.add(socket, user);
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
            if (!login) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Printer.print("User %s failed to login.", userId);
            } else {
                break;
            }
        }
        return login;
    }

    private void logoff() {
        try {
            loginService.logoff(socket, user);
            ServerListener serverListener = ServerListenerManager.get(user);
            ServerListenerManager.remove(user);
            serverListener.interrupt();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void pullOnlineUsers() {
        try {
            chatService.pullOnlineUsers(socket, user);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void chat() {
        try {
            chatService.pullOnlineUsers(socket, user);
            String receiverId = Scanner_.scanLine("Enter the user you want to chat with: ");
//            if (!users.contains(receiverId)) {
//                Printer.print("User %s is not online!", receiverId);
//                return;
//            }
            new ChatView(socket, user, receiverId).render();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
