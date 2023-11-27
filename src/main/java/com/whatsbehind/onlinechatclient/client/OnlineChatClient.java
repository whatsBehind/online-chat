package com.whatsbehind.onlinechatclient.client;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.whatsbehind.onlinechatcommon.model.message.Message;
import com.whatsbehind.onlinechatcommon.model.message.MessageType;
import com.whatsbehind.onlinechatcommon.model.onlinechatservice.BaseResponse;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OnlineChatClient {

    private Gson gson;
    private Socket onlineChatServer;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    @Inject
    public OnlineChatClient(Gson gson) {
        this.gson = gson;
    }

    private boolean connect(User user) throws IOException, ClassNotFoundException {
        onlineChatServer = new Socket(InetAddress.getLocalHost(), 9999);
        Message request = Message.builder()
                .sender(user.getId())
                .type(MessageType.CONNECT)
                .content(gson.toJson(user))
                .timeStamp(new Date().toString()).build();
        oos = new ObjectOutputStream(onlineChatServer.getOutputStream());
        oos.writeObject(request);

        ois = new ObjectInputStream(onlineChatServer.getInputStream());
        Message response = (Message) ois.readObject();
        BaseResponse connectResponse = gson.fromJson(response.getContent(), BaseResponse.class);
        if (!connectResponse.isSuccessful()) {
            System.out.println("Failed to build connection with Online Chat Server!");
            onlineChatServer.close();
            onlineChatServer = null;
            return false;
        }
        return true;
    }
    public List<String> pullOnlineUsers(User user) throws IOException, ClassNotFoundException {
        if (onlineChatServer == null) {
            if (!connect(user)) {
                return Collections.emptyList();
            }
        }
        Message request = Message.builder()
                .sender(user.getId())
                .type(MessageType.ONLINE_USERS)
                .content(gson.toJson(user))
                .timeStamp(new Date().toString()).build();

        sendRequest(request);

        Message response = receiveResponse();
        return (List<String>) gson.fromJson(response.getContent(), List.class);
    }


    public boolean logoff(User user) throws IOException, ClassNotFoundException {
        if (onlineChatServer == null) {
            if (!connect(user)) {
                return false;
            }
        }
        Message request = Message.builder()
                .sender(user.getId())
                .timeStamp(new Date().toString())
                .type(MessageType.USER_LOGOFF)
                .content(gson.toJson(user))
                .build();
        sendRequest(request);

        Message response = receiveResponse();
        boolean logoff = response != null && MessageType.LOGOFF_SUCCESS.equals(response.getType());
        if (logoff) {
            onlineChatServer.close();
            onlineChatServer = null;
            Printer.print("Online Chat Server of user [%s] closes", user.getId());
        }
        return logoff;
    }

    public BaseResponse sendMessage(User user, String receiver, String message) throws IOException, ClassNotFoundException {
        if (onlineChatServer == null) {
            if (!connect(user)) {
                return BaseResponse.builder().successful(false).message("Server connection failed").build();
            }
        }

        Message request = Message.builder()
                .sender(user.getId())
                .receiver(receiver)
                .type(MessageType.CHAT)
                .content(message).build();
        sendRequest(request);
        Message response = receiveResponse();
        return gson.fromJson(response.getContent(), BaseResponse.class);
    }

    private void sendRequest(Message request) throws IOException {
        oos = new ObjectOutputStream(onlineChatServer.getOutputStream());
        oos.writeObject(request);
    }

    private Message receiveResponse() throws IOException, ClassNotFoundException {
        ois = new ObjectInputStream(onlineChatServer.getInputStream());
        return (Message) ois.readObject();
    }
}
