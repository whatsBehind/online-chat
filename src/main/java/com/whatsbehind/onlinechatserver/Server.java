package com.whatsbehind.onlinechatserver;

import com.google.gson.Gson;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageSender;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatserver.service.LoginService;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

@NoArgsConstructor
public class Server {

    private final LoginService loginService = new LoginService();
    private final Gson gson = new Gson();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Server().start();
    }

    public void start() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(9999);

        while(true) {
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message request = (Message) ois.readObject();
            System.out.println(request);
            if (MessageType.USER_LOGIN.equals(request.getType())) {
                User user = gson.fromJson(request.getContent(), User.class);
                boolean login = loginService.login(socket, user);
                Message response = Message.builder()
                        .sender(MessageSender.ADMIN.toString())
                        .receiver(user.getId())
                        .timeStamp(new Date().toString())
                        .type(login ? MessageType.LOGIN_SUCCESS : MessageType.LOGIN_FAILURE).build();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(response);
                if (!login) {
                    ois.close();
                    oos.close();
                    socket.close();
                }
            }
        }
    }

}
