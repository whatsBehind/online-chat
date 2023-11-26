package com.whatsbehind.onlinechatcommon.model.onlinechatservice;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConnectResponse {
    private boolean successful;
    private String message;
}
