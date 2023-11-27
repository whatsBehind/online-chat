package com.whatsbehind.onlinechatclient.view;

import com.whatsbehind.onlinechatclient.client.OnlineChatClient;
import com.whatsbehind.onlinechatcommon.model.onlinechatservice.BaseResponse;
import com.whatsbehind.onlinechatcommon.model.user.User;
import com.whatsbehind.onlinechatcommon.utility.Printer;
import com.whatsbehind.onlinechatcommon.utility.Scanner_;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class ChatView {

    private boolean rendering = true;
    private OnlineChatClient chatClient;
    private User user;
    private String receiverId;

    public ChatView(OnlineChatClient chatClient, User user, String receiverId) {
        this.chatClient = chatClient;
        this.user = user;
        this.receiverId = receiverId;
    }

    public static void main(String[] args) {

    }

    public void render() {
        rendering = true;
        Printer.print("Start chatting with user %s", receiverId);
        while (rendering) {
            String message = Scanner_.scanLine("Type the message you want to send (Enter 9 if you want to exit chat): ");
            if (message.isBlank() || message.isEmpty()) {
                continue;
            }
            if (message.equals("9")) {
                rendering = false;
                continue;
            }
            send(message, receiverId);
        }
    }

    private void send(String message, String receiverId) {
        try {
            BaseResponse response = chatClient.sendMessage(user, receiverId, message);
            String printingTemp = response.isSuccessful() ? "Message sent to user [%s]" : "Failed to send message to user [%s]";
            Printer.print(printingTemp, receiverId);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
