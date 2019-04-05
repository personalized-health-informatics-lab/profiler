package com.sora.crawler.crawler.httpConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.sora.crawler.crawler.helper.logHelper;

@Component
public class httpConnector {	
	
	public synchronized String getContent(String urlStr) throws Exception{
		try {
//			System.out.println("Connecting to the pubmed");
			logHelper.info("Connecting to the pubmed");
			TimeUnit.MICROSECONDS.sleep(100);
			urlStr = urlStr+"&api_key=YOURKEY";
            URL url = new URL(urlStr);  
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();  
            httpUrlConn.setDoInput(true);  
            httpUrlConn.setRequestMethod("GET");  
            //input stream
            InputStream input = httpUrlConn.getInputStream();
            InputStreamReader read = new InputStreamReader(input, "utf-8");
            //add buffer for stream
            BufferedReader br = new BufferedReader(read);  
            //return content
            String data ;
            StringBuilder sb = new StringBuilder();
            while((data=br.readLine())!=null)  {
                sb.append(data);
            }
            //release resource
            br.close();  
            read.close();  
            input.close();  
            httpUrlConn.disconnect(); 
//            System.out.println("Connect finish");
            logHelper.info("Connect finish");
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();	
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
	}
}
