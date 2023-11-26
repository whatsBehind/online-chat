package com.whatsbehind.onlinechatcommon.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class User {
    private String id;
    private String password;
}
