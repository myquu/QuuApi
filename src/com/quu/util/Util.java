// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Util.java

package com.quu.util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.quu.util.Constant;


// Referenced classes of package com.quu.j2g.businesslogic:
//            Constant
public class Util
{
	//We use apache.httpclient for web operations. Why HttpURLConnection should not be used...https://dzone.com/articles/pola-and-httpurlconnection
	
    public static String getWebResponse(String urlStr, Map<String, String> data, boolean readResponse)
    {
        try
		{
			HttpPost post = new HttpPost(urlStr);
			
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			for (Map.Entry<String, String> entry : data.entrySet()) {
	            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	        }
						
			post.setEntity(new UrlEncodedFormEntity(params));
			
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	             CloseableHttpResponse response = httpClient.execute(post))
	        {
	        	if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
	        	{
	        		if(readResponse)
			        {   
	        			return EntityUtils.toString(response.getEntity());
			        }
	        	}
	        }
		}
		catch(IOException ex){
			System.out.println("Skyview:getWebResponse1: " + urlStr);
		}
        
		return null;
    }
    
    //Same as above but with HTTP basic authentication
    public static String getWebResponse(String urlStr, String data, String contentType, String username, String password, boolean readResponse)
    {
    	String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    	
    	try
		{
			HttpPost post = new HttpPost(urlStr);

	        post.setEntity(new StringEntity(data));
	        post.setHeader("Content-Type", contentType);
	        post.setHeader("Authorization", "Basic " + encoded);
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	             CloseableHttpResponse response = httpClient.execute(post))
	        {
	        	if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
	        	{
	        		if(readResponse)
			        {   
	        			return EntityUtils.toString(response.getEntity());
			        }
	        	}
	        }
		}
		catch(IOException ex){
			System.out.println("Skyview:getWebResponse2: " + urlStr);
		}
		
    	return null;
    }
    
    //This is a test version of the above that returns the response code and message for debugging.
    public static String[] getWebResponseTest(String urlStr, String data, String contentType, String username, String password, boolean readResponse)
    {
    	String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    	
    	try
		{
			HttpPost post = new HttpPost(urlStr);

	        post.setEntity(new StringEntity(data));
	        post.setHeader("Content-Type", contentType);
	        post.setHeader("Authorization", "Basic " + encoded);
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	             CloseableHttpResponse response = httpClient.execute(post))
	        {
	        	if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
	        	{
	        		if(readResponse)
			        {   
	        			return new String[] {response.getStatusLine().getStatusCode() +"", EntityUtils.toString(response.getEntity())};
			        }
	        	}
	        	else
		        {
		        	return new String[] {response.getStatusLine().getStatusCode() +"", ""};
		        }
	        }
		}
		catch(IOException ex){
			System.out.println("Skyview:getWebResponseTest: " + urlStr);
		}
		
    	return null;
    }
        
    public static String sendGetRequest(String urlStr, boolean readResponse)
    {
    	HttpGet request = new HttpGet(urlStr);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) 
        {
        	if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
        	{
        		if(readResponse)
		        {   
        			return EntityUtils.toString(response.getEntity());
		        }
        	}
        }
		catch(IOException ex){
			System.out.println("Skyview:sendGetRequest: " + ex.getMessage());
		}
        
        return null;
    }
    
    //Creates a client to a TCP server.
    public static void sendToTCPServer(int port, String ip, String message)
    {
    	try (Socket socket = new Socket(ip, port)) {
    		 
    		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    		out.print(message); 
    		out.close();
        } catch (UnknownHostException ex) {
            //System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
             //System.out.println("I/O error: " + ex.getMessage());
        }
    }
    
    //This method clears the campaigns' cache.
    public static void clearQuuRDSCache()
    {
    	sendGetRequest("http://myquu.name:8080/QuuRDS/cache", false);
    }
    
    //This function generates DPS fields from the passed line1 and line2 (can be null). It will create at max 8 fields and return them in a list.
    public static List<String> setDPSFields(String line1, String line2)
    {
    	String text = line1 + (line2 != null ?  " "+line2 : "");
    	
    	//Fill up the boxes with shifting logic - If a word starts on any of the last 2 indices of the box and has a length > 2 the shift it to the next box.
    	if(text.length() <= 64)  //This check makes sure at the most 8 boxes get created
    	{
    		List<String> list = new ArrayList<>();
    		
    		int len = 0;
    		
    		while((len = text.length()) > 8)
    		{
    			String box = text.substring(0, 8);  //Take the first 8 chars (and put in a box)
    			
    			int indexOfLastSpace = box.lastIndexOf(" ");
    			
    			//If 5th or 6th index is a space AND If the last word starting at index 6 or 7 spills over to the next box. The check says is if the current box has a word of length 1 or 2 and the first char of the next box is not a space.
    			if((indexOfLastSpace == 5 || indexOfLastSpace == 6) && box.substring(indexOfLastSpace + 1).matches("^\\w{1,2}$") && text.charAt(8) != ' ')  
    			{
    				list.add(box.substring(0, indexOfLastSpace+1));  //Take upto the last space
					text = text.substring(indexOfLastSpace+1);
    			}
    			else  //The last space is either the 4th or an earlier index or the last(7th) index OR there is no space OR shifting is not needed.
    			{
    				list.add(box);
    				text = text.substring(8);
    			}
    		}
    		
    		//The remaining text
    		if(text.length() > 0)
    		{
    			list.add(text);
    		}
    		 		
    		return list;
    	}
    	
    	return null;
    }
    
    public static void logQueryString(String message)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        try
        {
            FileWriter fw = new FileWriter(Constant.LOG_FILE, true);
            fw.write((new StringBuilder(String.valueOf(date))).append(": ").append(message).append("\n").toString());
            fw.flush();
            fw.close();
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Error while logging ")).append(e.getMessage()).toString());
        }
    }
    
    
    public static void main(String[] args) {
		
    	/*
    	String params = "calls=KQMV-FM&cutid=8045";
    	String response = Util.getWebResponse("https://go-ads-backend.herokuapp.com/quu", params, "application/x-www-form-urlencoded", true);
    	System.out.println(response);
        */
    	/*
    	try {
	    	JSONObject obj = new JSONObject();
	    	//obj.put("image", "wqert");
	    	//obj.put("image", "null");
	    	obj.put("image", JSONObject.NULL);
	    	
	    	//System.out.println(obj.getString("image"));
	    	//System.out.println(obj.optString("image", null));
	    	System.out.println(obj.toString());
    	}
	    catch(JSONException ex)
	    {
	        System.out.println("JSON exception: " + ex.getMessage());
	    }
    	*/
    	
    	//System.out.println(getWebResponse("https://httpbin.org/post", "{\"name\":\"mkyong\"}", "application/json", true));   	
    	
    	//ZonedDateTime phTime = ZonedDateTime.now(ZoneId.of("America/Phoenix"));
    	//System.out.println(phTime);
    	/*
    	Map<String, String> data = new HashMap<String, String>();
    	data.put("imagePath","EmergencyBlastCampaigns/-1");
    	data.put("fileName","logo.png");
    	//data.put("downloadFile","https://i.imgur.com/9J3Ofa9.jpg");
    	data.put("base64String","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");
    	data.put("requestFrom", "QuuAPI");
    	System.out.println(Util.getWebResponse("http://quuit.com/imageserver/image/Resize200", data, true));
    	*/
    	//System.out.println(Util.getWebResponse("https://quuit.com/imageserver/image/saveBase64StringImage", data, true));
    }
}
