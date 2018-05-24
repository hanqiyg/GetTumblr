package com.icesoft.gettumblr;

import android.app.Application;

import com.google.gson.Gson;
import com.icesoft.gettumblr.models.BaseDetail;
import com.icesoft.gettumblr.models.TypedJson;
import com.icesoft.gettumblr.models.VideoDetail;
import com.icesoft.gettumblr.sqlite.DownloadDao;

public class App extends Application {

    // Singleton instance
    private static App sInstance = null;
    private DownloadDao downloadDao;
    private Gson gson = new Gson();
    @Override
    public void onCreate() {
        super.onCreate();
        // Setup singleton instance
        sInstance = this;
        downloadDao = new DownloadDao(this);
    }

    // Getter to access Singleton instance
    public static App getInstance() {
        return sInstance ;
    }

    public void addDownloadId(VideoDetail result) {
        downloadDao.addDownload(result.TYPE,result.urlQueryString,result.downloadId,gson.toJson(result));
    }
    public BaseDetail getDownloadDetail(int type, int position)
    {
        String json = downloadDao.getDownloadByType(type,position);
        if(json == null)
        {
            return null;
        }
        switch (type)
        {
            case VideoDetail.TYPE : return gson.fromJson(json,VideoDetail.class);
            default: return null;
        }
    }
    public void deleteDownloadBySource(String url)
    {
        downloadDao.delDownloadBySourceUrl(url);
    }
    public int getDownloadCountByType(int type)
    {
        return downloadDao.getCountByType(type);
    }

    public BaseDetail getDownloadBySource(String link) {
        TypedJson typedJson = downloadDao.getDownloadBySource(link);
        if(typedJson == null)
        {
            return null;
        }
        switch (typedJson.type)
        {
            case VideoDetail.TYPE : return gson.fromJson(typedJson.json,VideoDetail.class);
            default: return null;
        }
    }
}
