package com.github.satellite.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetWorkUtils {

	public static String getContent(String strUrl) {
		HttpURLConnection  urlConn = null;
		InputStream in = null;
		BufferedReader reader = null;

		try {
			URL url = new URL(strUrl);
			
			urlConn = (HttpURLConnection) url.openConnection();
			
			urlConn.setRequestMethod("GET");

			urlConn.connect();
			
			int status = urlConn.getResponseCode();
			
		    if (status == HttpURLConnection.HTTP_OK) {

				in = urlConn.getInputStream();
				
		    	reader = new BufferedReader(new InputStreamReader(in));
		    	
				StringBuilder output = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					output.append(line);
				}
				System.out.println(output.toString());
		      }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (urlConn != null) {
					urlConn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return"";
	}
	
}
