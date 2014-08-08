package com.pushnotifications;

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
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MiIntentService extends IntentService {
	public static String ACTION_PROGRESO = null;
	
	public MiIntentService() {
        super("MiIntentService");
    }
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		String response;
		Log.e("lectura tarjeta", intent.getStringExtra("id"));
		Modelo modelo = new Modelo();
		//http://192.168.0.6/zbeltia_/ws/nfc.php
		tareaLarga();
		modelo.setContext(this);
		try{
			 	
			String id_usuario=intent.getStringExtra("id");
			HttpClient client = new DefaultHttpClient();
			HttpPost postURL  = new HttpPost("http://192.168.151.132/zbeltia_/ws/nfc.php");
				
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("id", id_usuario));

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
					JSONObject json = Json.getJSON(response);
		            Log.i("Server",""+json);
		            int accion=json.getInt("accion");
		            String Accions=Integer.toString(accion);
		            ///home/sansores/Descargas/android-master/Nfc/src/example/nfc/Json.java            ACTION_PROGRESO=Accions;
		            Intent bcIntent = new Intent();
		    		bcIntent.setAction(ACTION_PROGRESO);
		    		modelo.setLocal("id_usuario", json.getInt("id")+"");
		    		sendBroadcast(bcIntent);
		    		startActivity(new Intent(this, 
		    				Tiempoterminado.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					 
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
	}
	
	private void tareaLarga()
	{
		try { 
			Thread.sleep(5000); 
		} catch(InterruptedException e) {}
	}
}
