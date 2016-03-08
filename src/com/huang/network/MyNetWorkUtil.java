package com.huang.network;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class MyNetWorkUtil
{
	public static final String BASE_URL = "http://192.168.1.101:8080/location/";

	 public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	//参数类型
	public static String get(String url)
	{
		String result ="";
		OkHttpClient	client = new OkHttpClient();
		Request request = new Request.Builder().url(BASE_URL+url).build();
		Response response = null;
		try
		{
			response = client.newCall(request).execute();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if(response !=null )
		{
			try
			{
				result =  response.body().string();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
//	public static String post(String url, String json) throws IOException 
//	 {
//		 OkHttpClient client = new OkHttpClient();
//		 RequestBody body = RequestBody.create(JSON, json);
//		 Request request = new Request.Builder()
//		 .url(url)
//		 .post(body)
//		 .build();
//		    Response response = client.newCall(request).execute();
//		    return response.body().string();
//	}
//
//  String bowlingJson(String player1, String player2) {
//    return "{'winCondition':'HIGH_SCORE',"
//        + "'name':'Bowling',"
//        + "'round':4,"
//        + "'lastSaved':1367702411696,"
//        + "'dateStarted':1367702378785,"
//        + "'players':["
//        + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
//        + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
//        + "]}";
//  }
  
  public static String post(String url,RequestBody formBody)  {
	  String res = "";
	  OkHttpClient client = new OkHttpClient();
	 
	  Log.d("huang", BASE_URL+url);
	  	Request request = new Request.Builder()
	      .url(BASE_URL+url)
	      .post(formBody)
	      .build();

	  	Response response = null;
		try
		{
			response = client.newCall(request).execute();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  if (response != null && response.isSuccessful())
		  {
		      try
		      {
		    	  res =  response.body().string();
		      } catch (IOException e)
		      {
				// TODO Auto-generated catch block
				e.printStackTrace();
		      }
		      
		  }
	return res; 
	 
	}
  
}
