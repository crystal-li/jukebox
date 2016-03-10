package com.uwo.crystalli.jukebox;

import android.app.Application;

/* Global variables - just need this to indicate host/guest state */
/* TODO: Figure out what the proper way to do this is */

public class GlobalApplicationState extends Application {

    private Boolean isHost = null;
    private String YOUTUBE_API_KEY = "AIzaSyDJpskdkcvZ_6coBGE0hzznNr4sjbQGNno";

    public Boolean isHost() {
        return isHost;
    }

    public void setHost(Boolean isHost) {
        this.isHost = isHost;
    }

    public String getYoutubeApiKey() {
        return this.YOUTUBE_API_KEY;
    }


}
