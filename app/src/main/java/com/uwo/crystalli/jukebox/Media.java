package com.uwo.crystalli.jukebox;

public class Media {

    public String videoId; //Youtube API's VideoID
    public String title;
    public String thumbUrl; //All seem to be 120 x 90 for the default
    public String dbId; //Jukebox API's unique ID

    //TODO: don't know what this is
    public Media() {
        super();
    }

    public Media(String videoId, String title, String thumbUrl, String dbId) {
        super();
        this.videoId = videoId;
        this.title = title;
        this.thumbUrl = thumbUrl;
        this.dbId = dbId;
    }

    public String getVideoId() {
        return this.videoId;
    }

    @Override
    //TODO: Not sure if we need this
    public String toString() {
        return this.title;
    }
}
