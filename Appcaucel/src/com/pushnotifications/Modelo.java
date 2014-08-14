package com.pushnotifications;
	
import java.util.concurrent.ExecutionException;

import com.pushnotifications.Webservice;

import android.content.Context;
import android.content.SharedPreferences;

public class Modelo {
	
	Context context;
	Webservice webservice;	
	//public static final String urlzbeltia = "http://192.168.100.1/zbeltia/ws/nfc.php; 
	public static final String urlzbeltia = "192.168.151.132/zbeltia_/ws";
	private final String PREFS_NAME = "SESION_ZBELTIA";
	//public static final String SERVER_URL ="www.zbeltia.com.mx/service/app/";
	public static final String SERVER_URL ="192.168.151.132/zbeltia/index.php/service/app";
	//http://192.168.151.132
	
	public void setContext(Context context) {
		this.context = context;		
	}
	
	
	// Escribe datos en variables de sesion tipo String
	
	public void setLocal(String variable, String valor){		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);								
		SharedPreferences.Editor editor = prefs.edit();							
		editor.putString(variable,valor);
		editor.commit();
	}	
	
	// Escribe datos en variables de sesion tipo Boolean
	
	public void setLocal(String variable, Boolean valor){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);								
		SharedPreferences.Editor editor = prefs.edit();							
		editor.putBoolean(variable,valor);
		editor.commit();
	}	

	// Devuelve los datos de una variable de sesion tipo String
	
	public String getLocal(String variable){
		String data = null;
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);			
		data = prefs.getString(variable, "0");
		return data;
	}
	
	// Devuelve los datos de una variable de sesion tipo Boolean
	
	public Boolean getLocalBoolean(String variable){
		Boolean data = false;
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);			
		data = prefs.getBoolean(variable, true);
		return data;
	}	
	

			
	// PARA REINCIAR LAS VARIABLES DE SESION
	public void closeSesion() {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}
	
	// Para enviar datos al servidor, devuelve el response en string
	public String postServer(String[] campos, String[] valores, String url){
		String response = null;
        webservice = new Webservice();		
		webservice.setPost(campos, valores, url);		                           
        webservice.execute();
        try {
        	response = webservice.get();           
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) { 
			e.printStackTrace();
		}
        return response;
	}
	
	public String postServer(String[] campos, String[] valores, String url, String vip){
		String response = null;
        webservice = new Webservice();		
		webservice.setPost(campos, valores, url);	
		webservice.setip(vip);
        webservice.execute();
        try {
        	response = webservice.get();           
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) { 
			e.printStackTrace();
		}
        return response;
	}
}
