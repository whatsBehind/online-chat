package com.whatsbehind.onlinechatserver.dao;

import com.whatsbehind.onlinechatcommon.model.user.User;

import java.util.HashMap;
import java.util.Map;

public class ValidUser {
    private static Map<String, User> users = new HashMap<>();
    static {
        users.put("puyanh", User.builder().id("puyanh").password("aixiaoli").build());
        users.put("limei", User.builder().id("limei").password("aixiaoyang").build());
    }
    public static boolean isValidUser(String id, String password) {
        User user = users.get(id);
        if (user != null) {
            return password.equals(user.getPassword());
        }
        return false;
    }
}
