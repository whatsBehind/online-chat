package com.whatsbehind.onlinechatcommon.utility;

import java.util.Scanner;

public class Scanner_ {

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println(scanLine("Type anything: "));
    }
    public static String scanLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
