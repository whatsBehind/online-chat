package com.whatsbehind.onlinechatserver;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;
import com.whatsbehind.onlinechatserver.listener.ClientListenerManager;
import com.whatsbehind.onlinechatserver.publisher.MessagePublisherManager;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

@AllArgsConstructor(onConstructor = @__({ @Inject }))
public class Server {

    private Gson gson;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Injector injector = Guice.createInjector();
        injector.getInstance(Server.class).start();
    }

    public void start() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(9999);

        while(true) {
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message request = (Message) ois.readObject();
            User user = gson.fromJson(request.getContent(), User.class);
            switch (request.getType()) {
                case USER_LOGIN:
                    boolean login = MessagePublisherManager.login(user, socket);
                    if (login) {
                        Printer.print("User [%s] login", user.getId());
                    }
                    break;
                case CONNECT:
                    ClientListenerManager.add(socket, user);
                    Printer.print("Client Listener of user [%s] starts", user.getId());
                    break;
            }

        }
    }

}
