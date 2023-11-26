package com.whatsbehind.onlinechatclient.view;

import com.whatsbehind.onlinechatcommon.utility.Scanner_;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@NoArgsConstructor
@Setter
public class MainView {

    public static void main(String[] args) throws IOException {
        new MainView().render();
    }

    private boolean rendering = true;

    public void render() {
        while(rendering) {
            System.out.println("================= Welcome To Online Chat System =================");
            System.out.println("\t\t1: Log In");
            System.out.println("\t\t9: Exit System");
            final String key = Scanner_.scanLine("Please select one function: ");
            switch(key) {
                case "1":
                    new LoginView().render();
                    break;
                case "9":
                    System.out.println("You exited Online Chat system.");
                    rendering = false;
                    break;
                default:
                    System.out.println("Wrong input. Please re-enter");
                    break;
            }

        }
    }
}
