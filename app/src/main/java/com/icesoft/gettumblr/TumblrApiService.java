package com.icesoft.gettumblr;

import com.icesoft.gettumblr.exceptions.URLParseException;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TumblrApiService
{
    public static final String consumer_key = "MfA6BDjf9VUaGZhk0Qzc9mQxMoqrGAGbYNsLBM6i8ZZQDTQYaQ";
    public static final String consumer_secret = "zXRjmPNWZ4lNtZ9TK5gvuQ0qsGEzB5IpGRdt3XyVkf9o910apy";
    public static final String token = "qcTky7QPyOiTsmFQTfJCQbblSgcg8JNhKJg7pKEXr1BlBuAKWB";
    public static final String token_secret = "uvtpgLFPIq3dGTTSxqG1pQSoSKgV6GXR4ZgsYeKBzl6Bq2by1q";

    private static final JumblrClient jumblrClient = new JumblrClient(consumer_key,consumer_secret);

    public static Post getPost(String link) throws JumblrException, URLParseException {
        String blogName = getBlogName(link);
        long postId = getPostId(link);
        Post post = null;
        if(blogName != null && postId != -1){
            try {
                 post = jumblrClient.blogPost(blogName, postId);
            }catch (JumblrException e){
                    throw e;
            }
        }else{
            throw new URLParseException("Can not find vaild blog name or post id from link [ " +link + " ]." );
        }
        return post;
    }

    private static String getBlogName(String link) {
        Pattern pattern= Pattern.compile("^(https|http)://(.*?)(.tumblr.com/post/)(\\d+)[\\w-]+");
        Matcher m = pattern.matcher(link);
        if(m.find())
            return m.group(2);
        return null;

    }
    private static long getPostId(String link){
        Pattern pattern= Pattern.compile("(tumblr.com/post/)(\\d+)");
        Matcher m = pattern.matcher(link);
        long id = -1;
        if(m.find()){
            try{
                id = Long.valueOf(m.group(2));
            }catch(NumberFormatException e){
                System.out.println("NumberFormatException");
            }
        }
        return id;
    }
}
