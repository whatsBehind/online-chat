package com.whatsbehind.onlinechatserver.service;

import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageSender;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatserver.dao.ValidUser;
import com.whatsbehind.onlinechatserver.thread_.ClientListenManager;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

@Getter
public class LoginService {
    private ObjectOutputStream oos;
    public boolean login(Socket socket, User user) throws IOException {
        final String id = user.getId();
        final boolean isValidUser = ValidUser.isValidUser(id, user.getPassword());
        if (isValidUser) {
            ClientListenManager.add(socket, user);
            return true;
        }
        return false;
    }

    public boolean logoff(Socket socket, User user) throws IOException {
        Message response = Message.builder()
                .type(MessageType.LOGOFF_SUCCESS)
                .receiver(user.getId())
                .sender(MessageSender.ADMIN.toString())
                .timeStamp(new Date().toString())
                .build();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(response);
        return true;
    }

}
