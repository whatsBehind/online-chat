package com.whatsbehind.onlinechatcommon.utility;

public class Printer {
    public static void print(String template, String... args) {
        System.out.println(String.format(template, args));
    }

    public static void functionDelimiter() {
        System.out.println("----------------------------------------------------");
    }
}
