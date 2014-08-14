package com.pushnotifications;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class Webservice extends AsyncTask<String, String, String> {
	private String myIp, myPath, response = null;
	String[] Mynombres, Myvalores;
	boolean finished;
	int totalElementos;
	
	
	public synchronized void setPost(String[] nombres, String[] valores, String path) {
		Mynombres = nombres;
		Myvalores = valores;
		myPath = path;
		if (nombres.length == valores.length) {
			totalElementos = nombres.length;
		} else {
			totalElementos = 0;
		}
		myIp = "api.zbeltia.com.mx";
	}
	public synchronized void setip(String ip){
		myIp = ip;
	}
	public synchronized void setPost(String path) {
		myPath = path;
		totalElementos = 0;
	}
	
	@Override
	protected void onPreExecute() {
		
	}

	@Override
	protected String doInBackground(String... arg0) {

		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost postURL  = new HttpPost("http://" + myIp + "/" + myPath);
			Log.i("url", "http://" + myIp + "/" + myPath);
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			if (totalElementos > 0) {
				for (int i = 0; i < totalElementos; i++)
					params.add(new BasicNameValuePair(Mynombres[i],Myvalores[i]));				
			} else {
				params.add(new BasicNameValuePair("",""));				
			}

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			postURL.setEntity(ent);
			HttpResponse responsePOST = client.execute(postURL);
			int result=responsePOST.getStatusLine().getStatusCode();
			Log.i("status", ""+result);
			if(result==404){
				response = "404";
				//Log.i("response", "null");
			}else{
				HttpEntity resEntity = responsePOST.getEntity();
				Log.i("response", ""+resEntity);
				if (resEntity != null) {
				response = EntityUtils.toString(resEntity);
				}
			}
			
			//HttpEntity resEntity = responsePOST.getEntity();
			//if (resEntity != null) {
				//response = EntityUtils.toString(resEntity);
			//}			
			
		} catch (Exception e) {
			e.printStackTrace();
			response = null;
		}

		return response;
	}

	protected void onPostExecute(String bytes) {

	}

}
