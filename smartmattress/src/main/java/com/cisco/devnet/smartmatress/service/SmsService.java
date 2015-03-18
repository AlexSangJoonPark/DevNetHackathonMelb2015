package com.cisco.devnet.smartmatress.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class SmsService {

	private String smsUrl = "http://android.googleapis.com/gcm/send";
	
	public boolean sendSmsMessage(final String register_id, final String msg) {

		Runnable collectDataRunnable = new Runnable() {
			public void run() {

			 
				try {
					 
					HttpClient client = HttpClientBuilder.create().build();
					HttpPost post = new HttpPost(smsUrl);
				 
					// add header
					post.setHeader("Authorization", "key=AIzaSyB3iniGIz7tnqgFTVk4I6fNAyLOAoyx_JE");
					post.setHeader("User-Agent", "");
				 
					List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
					urlParameters.add(new BasicNameValuePair("registration_id", register_id));
					urlParameters.add(new BasicNameValuePair("data", "{\"notification\", \""+msg+"}"));
					post.setEntity(new UrlEncodedFormEntity(urlParameters));
					
					HttpResponse response = client.execute(post);
					System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
				 
					BufferedReader rd = new BufferedReader(
					        new InputStreamReader(response.getEntity().getContent()));
				 
					StringBuffer result = new StringBuffer();
					String line = "";
					while ((line = rd.readLine()) != null) {
						result.append(line);
					}
					System.out.println(result);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(collectDataRunnable);
		t.start();
		
		return true;
	}
}
