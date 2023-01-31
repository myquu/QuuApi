// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Constant.java

package com.quu.util;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.annotation.ManagedBean;

// Referenced classes of package com.quu.j2g.businesslogic:
//            LinkedProperties, RDSParser
public class Constant
{
    public static final String JBOSS_HOME_DIR = System.getProperty("jboss.server.base.dir");
    public static final String LOG_FILE;
    public static final String IMAGES_DIR_URL;
    public static final String IMAGES_DIR_URL_INSECURE;
    //public static final String BASE64STRINGTOIMAGESERVICE_URL;
    //public static final String IMAGEFROMURLSERVICE_URL;
    //This service resizes the passed image to under 14kb and 200x200 and saves it on the image server. It can take both an external image url and a base 64 image string.
    public static final String SAVEIMAGESERVICE_URL;
    public static final String DELETEIMAGESERVICE_URL;
    public static final String BIZCAMPAIGNPREVIEWURL;
    public static final String RTCAMPAIGNPREVIEWURL;	
    
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public static final int APIUserId = 152;
    
    static 
    {
        Properties props = new Properties();
        
        try
        {
        	props.load(new FileReader((new StringBuilder(String.valueOf(JBOSS_HOME_DIR))).append("/").append("props").append("/QuuAPI/config.properties").toString()));
        	System.out.println("QuuAPI config file loaded");
        }
        catch(IOException ex)
        {
            System.out.println("QuuAPI config file not found");
        }
        
        
        LOG_FILE = props.getProperty("LOG_FILE");
        IMAGES_DIR_URL = props.getProperty("IMAGES_DIR_URL");
        IMAGES_DIR_URL_INSECURE = props.getProperty("IMAGES_DIR_URL_INSECURE");
        
        SAVEIMAGESERVICE_URL = props.getProperty("SAVEIMAGESERVICE_URL");
        DELETEIMAGESERVICE_URL = props.getProperty("DELETEIMAGESERVICE_URL");
        BIZCAMPAIGNPREVIEWURL = props.getProperty("BIZCAMPAIGNPREVIEWURL");
        RTCAMPAIGNPREVIEWURL = props.getProperty("RTCAMPAIGNPREVIEWURL");
    }
}
