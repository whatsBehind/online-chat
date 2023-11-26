package com.whatsbehind.onlinechatserver.publisher;

import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Getter
public class MessagePublisher {
    private User user;
    private Socket socket;
    private ObjectOutputStream oos;
    public MessagePublisher(User user, Socket socket) {
        this.user = user;
        this.socket = socket;
    }

    public void logoff() throws IOException {
        Message message = Message.builder()
                .type(MessageType.USER_LOGOFF).build();
        publish(message);
        socket.close();
        MessagePublisherManager.remove(user);
        Printer.print("Message Publisher of user [%s] logoff", user.getId());
    }

    public void publish(Object o) throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(o);
    }
}
