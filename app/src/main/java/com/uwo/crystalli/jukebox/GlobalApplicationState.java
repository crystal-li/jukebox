package com.uwo.crystalli.jukebox;

import android.app.Application;

// Just a bunch of global application variables we need to keep track of
/* TODO: Figure out what the proper way to do this is */

public class GlobalApplicationState extends Application {

    private Boolean isHost = null;
    private String YOUTUBE_API_KEY = "AIzaSyDJpskdkcvZ_6coBGE0hzznNr4sjbQGNno";
    private Integer hostId = null;

    public Boolean isHost() {
        return this.isHost;
    }

    public void setHost(Boolean isHost) {
        this.isHost = isHost;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public Integer getHostId() {
        return this.hostId;
    }

    public String getYoutubeApiKey() {
        return this.YOUTUBE_API_KEY;
    }


}
