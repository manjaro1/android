package example.nfc;
	
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Modelo {
	
	Context context;
	Webservice webservice;	
	//public static final String urlzbeltia = "www.zbeltia.com.mx"; 
	public static final String urlzbeltia = "192.168.0.6/zbeltia/ws";
	private final String PREFS_NAME = "SESION_ZBELTIA";
	
		
	
	public void setContext(Context context) {
		this.context = context;		
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
	
	// Devuelve la lista de alimentos registrados por el usuario (almacenados en local)
	
	public String getAlimentosLocal() {
		String alimentos_lista = getLocal("alimentos_lista");		
		//if(alimentos_lista.length()<=1){	
		//	alimentos_lista = getAlimentosServer();
		//}		
		if(alimentos_lista.equals("0")){	
			alimentos_lista = "";
		}				
		return alimentos_lista;
	}	
			
	// PARA REINCIAR LAS VARIABLES DE SESION
	public void closeSesion() {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}
}
