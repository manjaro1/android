package com.example.leernfc;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public class Tiempoterminado extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tiempoagotado);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		
	}
	
	@Override  
	 public void onBackPressed() {  
	 }  
	
	@Override
    protected void onStop() 
    {
        super.onStop();
        Log.d("onresume", "MYonStop is called");
        startActivity(new Intent(this, 
				Tiempoterminado.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        
        // insert here your instructions
    }
	
	
	
}