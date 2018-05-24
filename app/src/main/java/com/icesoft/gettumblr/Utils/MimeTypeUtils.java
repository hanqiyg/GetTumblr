package com.icesoft.gettumblr.Utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2018/2/11.
 */

public class MimeTypeUtils
{
    public static final MimeTypeUtils INSTANCE = new MimeTypeUtils();
    private Properties props = new Properties();

    private MimeTypeUtils()
    {
        loadProperties();
    }
    public void loadProperties()
    {
        try{
            InputStream in = MimeTypeUtils.class.getResourceAsStream("/assets/mime_type.properties");
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static MimeTypeUtils getInstance()
    {
        return INSTANCE;
    }
    public String MimeTypeForExtension(String mimetype)
    {
        if(StringUtils.isBlank(mimetype)){
            return "UNKNOW";
        }else{
            return props.getProperty(mimetype.toLowerCase().trim());
        }
    }
}
