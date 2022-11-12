package com.finance.healthchecker.comm.util;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

public class OKHttpClient {

	private final MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
	
	private String url;
	private String body;
	private String responseBody;
	private Request request;
	
	public boolean isConnect = true;
	public String error_msg = "";
	
	public OKHttpClient(Request request){
		this.request = request;
    }
	
	public int doUsingHttp() throws Exception {
		Response response = null;
		try{

			OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(30000, TimeUnit.SECONDS)
				.readTimeout(30000, TimeUnit.SECONDS)
				.writeTimeout(30000, TimeUnit.SECONDS)
				.build();
			
			response = client.newCall(this.request).execute();
			if (response.code() == 200) {
				this.responseBody = response.body().string();
			}
			else {
				this.isConnect = false;
				this.error_msg = response.toString();
			}
			
		} catch (Exception e) {
			this.isConnect = false;
			this.error_msg = "[ERROR] efnc.cardv2.batch.util.OKHttpClient.doUsingHttp() \n"+ e.toString();
		}

		int code = 0;
		if(response != null) {
			code = response.code();
		}

		return code;
	}
	
	 public boolean isConnect(){
		 return this.isConnect;
	 }
	 
	 public String getErrorMsg(){
		 return this.error_msg;
	 }

	 public String getResponseBody(){return this.responseBody;}

	 
}