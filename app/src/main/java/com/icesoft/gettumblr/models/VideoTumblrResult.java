package com.icesoft.gettumblr.models;

public class VideoTumblrResult extends TumblrResult {
    private static final int TYPE = 1;
    private VideoDetail[] details;
    public VideoTumblrResult(String queryString, Exception exception,VideoDetail[] details) {
        super(queryString, exception);
        this.details = details;
    }
    public VideoDetail[] getVideoDetails()
    {
        return details;
    }
}
