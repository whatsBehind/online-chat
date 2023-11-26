package com.whatsbehind.onlinechatcommon.model.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class Message implements Serializable {
    private String sender;
    private String receiver;
    private String timeStamp;
    private MessageType type;
    private String content;
}
