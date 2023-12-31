package com.whatsbehind.onlinechatserver.listener;

import com.google.gson.Gson;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.onlinechatservice.BaseResponse;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;
import com.whatsbehind.onlinechatserver.publisher.MessagePublisher;
import com.whatsbehind.onlinechatserver.publisher.MessagePublisherManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

@AllArgsConstructor
@Getter
public class ClientListener extends Thread {
    private Socket socket;
    private User user;
    private Gson gson = new Gson();

    private ObjectOutputStream oos;
    private boolean listening = true;

    public ClientListener(Socket socket, User user) {
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
                    case USER_LOGOFF:
                        logoff();
                        break;
                    case ONLINE_USERS:
                        returnOnlineUsers();
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

    private void logoff() throws IOException {
        MessagePublisher publisher = MessagePublisherManager.get(user);
        publisher.logoff();

        Message response = Message.builder()
                .type(MessageType.LOGOFF_SUCCESS).build();
        sendResponse(response);

        socket.close();
        ClientListenerManager.remove(user);
        listening = false;
        Printer.print("Client Listener of user [%s] logoff", user.getId());
    }

    private void returnOnlineUsers() throws IOException {
        List<String> onlineUsers = MessagePublisherManager.getOnlineUsers();
        Message response = Message.builder()
                .type(MessageType.ONLINE_USERS)
                .content(gson.toJson(onlineUsers)).build();
        sendResponse(response);
    }

    private void sendMessage(Message request) throws IOException {
        String sender = request.getSender();
        String receiver = request.getReceiver();
        String content = request.getContent();

        MessagePublisher publisher = MessagePublisherManager.get(receiver);
        BaseResponse baseResponse = BaseResponse.builder().successful(false).build();
        Message response = Message.builder()
                .receiver(sender)
                .type(MessageType.CHAT).build();
        try {
            publisher.sendMessage(sender, content);
            baseResponse.setSuccessful(true);
        } catch (IOException e) {
            Printer.print("Failed to send message from [%s] to [%s]", user.getId(), receiver);
        } finally {
            response.setContent(gson.toJson(baseResponse));
            sendResponse(response);
        }
    }

    private void sendResponse(Message message) throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
    }
}
