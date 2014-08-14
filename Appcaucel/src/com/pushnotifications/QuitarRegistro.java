package com.pushnotifications;

import static com.pushnotifications.CommonUtilities.SENDER_ID;
import static com.pushnotifications.Modelo.SERVER_URL;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class QuitarRegistro extends Activity{
	AsyncTask<Void, Void, Void> mRegisterTask;
	Modelo modelo = new Modelo();
	public static String id_usuario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		modelo.setContext(this);
		id_usuario=modelo.getLocal("id_usuario");

		cerrarSesion();
	}

	// REINICIA LAS VARIABLES DE SESION Y TERMINA LA SESION ACTUAL
	public void cerrarSesion(){

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);


		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(this, SENDER_ID);
			//Log.d("registro", SENDER_ID);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.		
				Log.d("registro", "existente");
				Log.d("regId", regId);
				Boolean response = false, no_valid=false;
				String[] campos  ={"regId","id_usuario"};
				String[] valores ={regId,id_usuario};
				String server = modelo.postServer(campos, valores, "register",SERVER_URL);
				
			}
			//Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
		} 
	}

	public void showAlert(String msge){
		new AlertDialog.Builder(this)		    
	    .setMessage(msge)
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which){}
	     }).show();			
	}

public void continuar(View view) {
	finish();
}

}
