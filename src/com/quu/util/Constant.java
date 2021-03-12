// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Constant.java

package com.quu.util;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

// Referenced classes of package com.quu.j2g.businesslogic:
//            LinkedProperties, RDSParser

public class Constant
{
    public static final String JBOSS_HOME_DIR = System.getProperty("jboss.server.base.dir");
    public static final String PROPERTIES_DIR = "props";
    public static final String IMAGES_DIR_URL;
    public static final String IMAGES_DIR_URL_INSECURE;
    public static final String BASE64STRINGTOIMAGESERVICE_URL = "https://quuit.com/imageserver/image/saveBase64StringImage";
    public static final String IMAGEFROMURLSERVICE_URL = "http://quuit.com/imageserver/image/saveImageFromUrl";
    public static final String RDSCAMPAIGNPREVIEWURL = "http://advertiserexperience.com/Quu2Go/Preview.aspx?campaign_type=BizRDS&campaign_id=";
    	
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    
    static 
    {
        Properties props = new Properties();
        
        try
        {
        	props.load(new FileReader((new StringBuilder(String.valueOf(JBOSS_HOME_DIR))).append("/").append("props").append("/QuuRDS/config.properties").toString()));
        	System.out.println("QuuAPI config file loaded");
        }
        catch(IOException ex)
        {
            System.out.println("QuuAPI config file not found");
        }
        
        
        IMAGES_DIR_URL = props.getProperty("IMAGES_DIR_URL");
        IMAGES_DIR_URL_INSECURE = props.getProperty("IMAGES_DIR_URL_INSECURE");
    }
}
