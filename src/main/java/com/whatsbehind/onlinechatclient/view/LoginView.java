package com.whatsbehind.onlinechatclient.view;

import com.google.inject.Inject;
import com.whatsbehind.onlinechatclient.client.OnlineChatClient;
import com.whatsbehind.onlinechatclient.listener.ServerListenerManager;
import com.whatsbehind.onlinechatclient.service.LoginService;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;
import com.whatsbehind.onlinechatcommon.utility.Scanner_;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

@NoArgsConstructor
public class LoginView {

    private final static int LOGIN_RETRY_LIMIT = 3;
    private LoginService loginService;
    private OnlineChatClient onlineChatClient;
    private User user;
    private Socket listenerSocket;

    @Inject
    public LoginView(LoginService loginService, OnlineChatClient onlineChatClient) {
        this.loginService = loginService;
        this.onlineChatClient = onlineChatClient;
    }

    public static void main(String[] args) {
        new LoginView().render();
    }

    private boolean rendering = true;
    private int loginRetryCount = 1;

    public void render() {
        rendering = true;
        boolean login = login();
        if (!login) {
            System.out.println("Too many retries!");
            return;
        }
        Printer.print("User %s login successfully. Welcome to Online Chat system", user.getId());
        Printer.functionDelimiter();

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
                    Printer.functionDelimiter();
                    break;
                case "2":
                    Printer.functionDelimiter();
                    chat();
                    Printer.functionDelimiter();
                    break;
                case "9":
                    Printer.functionDelimiter();
                    logoff();
                    System.out.println("You exited Online Chat system.");
                    rendering = false;
                    Printer.functionDelimiter();
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
                listenerSocket = new Socket(InetAddress.getLocalHost(), 9999);
            } catch (IOException e) {
                Printer.print("User %s failed to connect to server.", userId);
                continue;
            }
            user = User.builder().id(userId).password(password).build();
            try {
                login = loginService.login(listenerSocket, user);
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
            if (!login) {
                try {
                    listenerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Printer.print("User %s failed to login.", userId);
            } else {
                ServerListenerManager.add(listenerSocket, user);
                break;
            }
        }
        return login;
    }

    private void logoff() {
        boolean logoff = false;
        try {
            logoff = onlineChatClient.logoff(user);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (!logoff) {
                System.out.println("Failed to logoff");
            }
        }
    }

    private void pullOnlineUsers() {
        try {
            List<String> onlineUsers = onlineChatClient.pullOnlineUsers(user);
            for (String user : onlineUsers) {
                System.out.println(user);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void chat() {
        try {
            System.out.println("Online users: ");
            List<String> onlineUsers = onlineChatClient.pullOnlineUsers(user);
            for (String user : onlineUsers) {
                System.out.println(user);
            }
            final String receiver = Scanner_.scanLine("Please select who you want to chat with: ");
            if (!onlineUsers.contains(receiver)) {
                Printer.print("User [%s] is not online", receiver);
            } else {
                new ChatView(onlineChatClient, user, receiver).render();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
