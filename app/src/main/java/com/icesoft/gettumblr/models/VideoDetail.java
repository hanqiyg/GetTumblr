package com.icesoft.gettumblr.models;

import com.icesoft.gettumblr.Utils.MimeTypeUtils;
import com.icesoft.gettumblr.Utils.TumblrUtils;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Video;

import java.io.File;

public class VideoDetail extends BaseDetail
{
    public static final int TYPE = 1;
    //request
    public String urlQueryString;

    //blog
    public String blogName;
    public String blogAvatarFilePath;

    //post
    public long postId = -1;
    public String postSlug;
    public long   postNoteCount = -1;
    public String postSource;

    // status
    public long downloadId;

    //video
    public int width;
    public String embed_code;
    public String posterFilePath = null;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof VideoDetail)
        {
            VideoDetail vobj = (VideoDetail) obj;
            if(TumblrUtils.getUrlFromEmbed(vobj.embed_code).equals(TumblrUtils.getUrlFromEmbed(this.embed_code)))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        String source = TumblrUtils.getUrlFromEmbed(this.embed_code);
        return source.hashCode();
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean delete() {
        boolean isAvatarFileDeleted = false,isPosterFileDeleted = false,isVideoFileDeleted = false;
        File avatarFile = new File(blogAvatarFilePath);
        if(avatarFile.exists() && avatarFile.isFile())
        {
            isAvatarFileDeleted = avatarFile.delete();
        }
        File posterFile = new File(posterFilePath);
        if(posterFile.exists() && posterFile.isFile())
        {
            isPosterFileDeleted = posterFile.delete();
        }
        return isAvatarFileDeleted && isPosterFileDeleted && isVideoFileDeleted;
    }

    @Override
    public long[] getDownloadId() {
        long[] ids = new long[1];
        ids[0] = downloadId;
        return ids;
    }
}
