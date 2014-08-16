package com.pushnotifications;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class IntentPausa extends IntentService{
	
	public IntentPausa() {
        super("IntentPausa");
    }
	
	@Override
    public void onCreate() {
		
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Modelo modelo = new Modelo();
		modelo.setContext(this);
		String status=modelo.getLocal("status");
		while (status.equals("1")){
			Log.i("status", status);
		}
		
		
	}
}
