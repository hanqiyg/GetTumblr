package com.icesoft.gettumblr.models;

import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;

public class TumblrResult {
    public Exception exception;
    public String queryString;
    public TumblrResult(String queryString,Exception exception) {
        this.queryString = queryString;
        this.exception = exception;
    }
}
