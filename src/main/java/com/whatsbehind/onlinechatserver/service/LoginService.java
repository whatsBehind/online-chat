package com.whatsbehind.onlinechatserver.service;

import lombok.Getter;

@Getter
public class LoginService {
//    private ObjectOutputStream oos;
//    public boolean login(Socket socket, User user) throws IOException {
//        final String id = user.getId();
//        final boolean isValidUser = ValidUser.isValidUser(id, user.getPassword());
//        if (isValidUser) {
//            ClientListenManager.add(socket, user);
//            return true;
//        }
//        return false;
//    }
//
//    public boolean logoff(Socket socket, User user) throws IOException {
//        Message response = Message.builder()
//                .type(MessageType.LOGOFF_SUCCESS)
//                .receiver(user.getId())
//                .sender(MessageSender.ADMIN.toString())
//                .timeStamp(new Date().toString())
//                .build();
//        oos = new ObjectOutputStream(socket.getOutputStream());
//        oos.writeObject(response);
//        return true;
//    }

}
