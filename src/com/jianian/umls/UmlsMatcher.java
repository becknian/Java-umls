package com.jianian.umls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UmlsMatcher { 
	static String urlAddress = "https://utslogin.nlm.nih.gov/cas/v1/api-key";
	static String APIKEY = ""; // You need to enter your own API key
	static String charset = "UTF-8";
	static String contentType = "application/x-www-form-urlencoded;charset=" + charset;
	static String service = "http://umlsks.nlm.nih.gov";
	static String cuiUrlBase = "https://uts-ws.nlm.nih.gov/rest/content/current/CUI/";
	String ticketUrl = null;
	
	static String fileAddress = "D:/r15/codefile2.txt";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//			Scanner in = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(fileAddress))));
//			UmlsMatcher um = new UmlsMatcher();
//			while(in.hasNext()) {
//				System.out.println(um.getLabel(in.nextLine()));
//			}
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		UmlsMatcher um = new UmlsMatcher();
		System.out.println("C0000737: " + um.getLabel("C0000737"));	

	}
	
	public String getUrl() {
		
		String urlForTicket = "";
		try {
			URL url = new URL(urlAddress);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
//            httpConn.setConnectTimeout(5000);
            httpConn.setDoInput(true);  
            httpConn.setDoOutput(true);

            httpConn.setRequestProperty("Content-Type", contentType);
            
            
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new NameValuePair("apikey", APIKEY));
            
            OutputStream wr= httpConn.getOutputStream();
            wr.write(getQuery(params).getBytes(charset));
            wr.close();
            StringBuilder sb = new StringBuilder();  
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), charset));
            String line = null;  
            while ((line = br.readLine()) != null) {  
                sb.append(line + "\n");  
            }
            br.close();
            Pattern pattern = Pattern.compile("action=\"(.+?)\"");
            Matcher matcher = pattern.matcher(sb.toString());
            matcher.find();
            urlForTicket = matcher.group(1);
            
            
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlForTicket;
	}
	public String getTicket(){
		String ticket = "";
		if (ticketUrl == null){
			ticketUrl = getUrl();
		}
		try {
			URL url = new URL(ticketUrl);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
//            httpConn.setConnectTimeout(5000);
            httpConn.setDoInput(true);  
            httpConn.setDoOutput(true);

            httpConn.setRequestProperty("Content-Type", contentType);
            
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new NameValuePair("service", service));
            
            OutputStream wr= httpConn.getOutputStream();
            wr.write(getQuery(params).getBytes());
            wr.close();
            StringBuilder sb = new StringBuilder();  
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), charset));
            String line = null;  
            while ((line = br.readLine()) != null) {  
                sb.append(line + "\n");  
            }
            br.close();
            ticket = sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ticket;
	}
	
	public String getLabel(String cCode){
		String ticket = getTicket();
		String cuiUrl = cuiUrlBase + cCode + "?ticket=" + ticket;
		String label = "";
		try {
			URL url = new URL(cuiUrl);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("GET");
//            httpConn.setConnectTimeout(5000);
            httpConn.setDoInput(true);  
            httpConn.setDoOutput(true);

            StringBuilder sb = new StringBuilder();  
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(), charset));
            String line = null;  
            while ((line = br.readLine()) != null) {  
                sb.append(line + "\n");  
            }
            br.close();
            String jsonStr = sb.toString();
            
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonStr);
            JSONObject result = (JSONObject) json.get("result");
            label = result.get("name").toString();
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return label;
	}
	
	private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params){
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getName(), charset));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), charset));
	    }

	    return result.toString();
	}

}

class NameValuePair{
	String name;
	String value;
	public NameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName(){
		return name;
	}
	
	public String getValue(){
		return value;
	}
}
