package com.whatsbehind.onlinechatclient.view;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.whatsbehind.onlinechatclient.guice.ViewModule;
import com.whatsbehind.onlinechatcommon.utility.Scanner_;
import lombok.Setter;

import java.io.IOException;

@Setter
public class MainView {

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new ViewModule());
        injector.getInstance(MainView.class).render();
    }

    private LoginView loginView;
    @Inject
    public MainView(LoginView loginView) {
        this.loginView = loginView;
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
                    loginView.render();
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
