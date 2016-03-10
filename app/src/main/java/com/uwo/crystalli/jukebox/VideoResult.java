package com.uwo.crystalli.jukebox;

public class VideoResult {

    public String id;
    public String title;
    public String thumbUrl; //All seem to be 120 x 90 for the default

    //TODO: don't know what this is
    public VideoResult() {
        super();
    }

    public VideoResult(String id, String title, String thumbUrl) {
        super();
        this.id = id;
        this.title = title;
        this.thumbUrl = thumbUrl;
    }

    public String getId() {
        return this.id;
    }

    @Override
    //TODO: Not sure if we need this
    public String toString() {
        return this.title;
    }
}
