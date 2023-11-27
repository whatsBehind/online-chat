package com.whatsbehind.onlinechatclient.service;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

@NoArgsConstructor
public class LoginService {
    private Gson gson;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    @Inject
    public LoginService(Gson gson) {
        this.gson = gson;
    }
    public boolean login(Socket socket, User user) throws IOException, ClassNotFoundException {
        Message request = Message.builder()
                .sender(user.getId())
                .timeStamp(new Date().toString())
                .type(MessageType.USER_LOGIN)
                .content(gson.toJson(user))
                .build();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(request);

        ois = new ObjectInputStream(socket.getInputStream());
        Message response = (Message) ois.readObject();
        return response != null && MessageType.LOGIN_SUCCESS.equals(response.getType());
    }

}
