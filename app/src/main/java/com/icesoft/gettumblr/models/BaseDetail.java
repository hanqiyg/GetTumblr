package com.icesoft.gettumblr.models;

public abstract class BaseDetail
{
    public abstract int getType();
    public abstract boolean delete();
    public abstract long[] getDownloadId();
}
