package com.whatsbehind.onlinechatserver.guice;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class UtilityModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new Gson();
    }
}
