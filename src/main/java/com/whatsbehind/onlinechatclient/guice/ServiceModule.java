package com.whatsbehind.onlinechatclient.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.whatsbehind.onlinechatclient.service.LoginService;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Singleton
    @Provides
    public LoginService provideLoginService() {
        return new LoginService();
    }
}