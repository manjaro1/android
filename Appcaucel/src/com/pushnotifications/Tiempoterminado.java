package com.pushnotifications;

import static com.pushnotifications.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.pushnotifications.CommonUtilities.EXTRA_MESSAGE;
import static com.pushnotifications.CommonUtilities.SENDER_ID;

import com.pushnotifications.AlertDialogManager;
import com.pushnotifications.ConnectionDetector;
import com.pushnotifications.ServerUtilities;
import com.pushnotifications.WakeLocker;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Tiempoterminado extends Activity{
	// label to display gcm messages
		TextView lblMessage;
		Modelo modelo = new Modelo();
		Context ctx;
		// Asyntask
		AsyncTask<Void, Void, Void> mRegisterTask;
		
		// Alert dialog manager
		AlertDialogManager alert = new AlertDialogManager();
		
		// Connection detector
		ConnectionDetector cd;
		public static String id_usuario= null;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.tiempoagotado);
			ctx = this;
			modelo.setContext(this);
			id_usuario=modelo.getLocal("id_usuario");
			cd = new ConnectionDetector(getApplicationContext());

			// Check if Internet present
			if (!cd.isConnectingToInternet()) {
				// Internet Connection is not present
				alert.showAlertDialog(Tiempoterminado.this,
						"Internet  Error",
						"Verifique la conexion de la tableta", false);
				// stop executing code by return
				return;
			}
			
			// Getting name, email from intent
			Intent i = getIntent();
			
						
			// Make sure the device has the proper dependencies.
			GCMRegistrar.checkDevice(this);

			// Make sure the manifest was properly set - comment out this line
			// while developing the app, then uncomment it when it's ready.
			GCMRegistrar.checkManifest(this);

			lblMessage = (TextView) findViewById(R.id.lblMessage);
			
			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					DISPLAY_MESSAGE_ACTION));
			
			// Get GCM registration id
			final String regId = GCMRegistrar.getRegistrationId(this);
			Log.d("id_usuario", "test");
			// Check if regid already presents
			if (regId.equals("")) {
				// Registration is not present, register now with GCM			
				GCMRegistrar.register(this, SENDER_ID);
			} else {
				// Device is already registered on GCM
				if (GCMRegistrar.isRegisteredOnServer(this)) {
					// Skips registration.				
					//Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
				} else {
					// Try to register again, but not in the UI thread.
					// It's also necessary to cancel the thread onDestroy(),
					// hence the use of AsyncTask instead of a raw thread.
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							// Register on our server
							// On server creates a new user
							ServerUtilities.register(context, id_usuario, regId);
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mRegisterTask = null;
						}

					};
					mRegisterTask.execute(null, null, null);
				}
			}
		}		

		/**
		 * Receiving push messages
		 * */
		private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
				// Waking up mobile if it is sleeping
				WakeLocker.acquire(getApplicationContext());
				
				/**
				 * Take appropriate action on this message
				 * depending upon your app requirement
				 * For now i am just displaying it on the screen
				 * */
				
				// Showing received message
				//lblMessage.append(newMessage + "\n");			
				//Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
				
				// Releasing wake lock
				WakeLocker.release();
			}
		};
		
		@Override
		protected void onDestroy() {
			if (mRegisterTask != null) {
				mRegisterTask.cancel(true);
			}
			try {
				unregisterReceiver(mHandleMessageReceiver);
				GCMRegistrar.onDestroy(this);
			} catch (Exception e) {
				Log.e("UnRegister Receiver Error", "> " + e.getMessage());
			}
			super.onDestroy();
		}
		
		

		@Override
		protected void onPause() {
			modelo.setLocal("status", 0+"");
			super.onPause();
		}

		


		@Override
		protected void onStop() {
			modelo.setLocal("status", 0+"");
			super.onStop();
		}



		@Override
		protected void onResume() {
			
			modelo.setLocal("status", 1+"");
			super.onResume();
		}
		
		public void continuar(View view) {
			finish();
		}
		

	}
