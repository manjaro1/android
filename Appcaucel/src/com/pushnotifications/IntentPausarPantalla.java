package com.pushnotifications;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class IntentPausarPantalla  extends IntentService{
	
	public IntentPausarPantalla() {
        super("IntentPausarPantalla");
    }
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		int i =1;
		while(true){
			pausartarea(); 
			try{
				i++;
				Modelo modelo = new Modelo();
				modelo.setContext(this);
				String statustiempo=modelo.getLocal("statustiempo");
				String id_usuario=intent.getStringExtra("id");
				
				Log.i("statustiempo", statustiempo+" - "+id_usuario);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void pausartarea()
	{
		try { 
			Thread.sleep(1000); 
		} catch(InterruptedException e) {}
	}

}
