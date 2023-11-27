package com.whatsbehind.onlinechatcommon.model.onlinechatservice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class BaseResponse {
    private boolean successful;
    private String message;
}
