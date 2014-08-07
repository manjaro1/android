package com.example.leernfc;

import static com.example.leernfc.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.leernfc.CommonUtilities.SENDER_ID;
import static com.example.leernfc.CommonUtilities.EXTRA_MESSAGE;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.example.leernfc.WakeLocker;

import com.example.leernfc.ServerUtilities;

import com.google.android.gcm.GCMRegistrar;

import com.example.leernfc.ConnectionDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";

	private TextView mTextView;
	private NfcAdapter mNfcAdapter;
	Modelo modelo = new Modelo();
	Context ctx;
	ConnectionDetector cd;
	public static String id_usuario;
	AsyncTask<Void, Void, Void> mRegisterTask;
	public static final String EXTRA_MESSAGE = "message";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ctx = this;


		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
			// Stop here, we definitely need NFC
			Toast.makeText(this, "Movíl no soporta NFC.", Toast.LENGTH_LONG).show();
			finish();
			return;

		}
		modelo.setContext(this);
		id_usuario=modelo.getLocal("id_usuario");
		cd = new ConnectionDetector(getApplicationContext());

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		// obtener GCM  id del registro
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (cd.isConnectingToInternet()) { //checar conexion en internet
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
					//Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
				} else {
					// Try to register again, but not in the UI thread.
					// It's also necessary to cancel the thread onDestroy(),
					// hence the use of AsyncTask instead of a raw thread.
					Log.d("registro", "nuevo");
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							// Register on our server
							// On server creates a new user
							try{
								ServerUtilities.register(context, id_usuario,  regId);
							}catch(Exception e){
								e.printStackTrace();
							}

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

		//		if (!mNfcAdapter.isEnabled()) {
		//			mTextView.setText("NFC is disabled.");
		//		} else {
		//			mTextView.setText(R.string.explanation);
		//		}

		handleIntent(getIntent());
	}

	@Override
	protected void onResume() {
		super.onResume();

		/*
		 * It's important, that the activity is in the foreground (resumed). Otherwise
		 * an IllegalStateException is thrown. 
		 */
		setupForegroundDispatch(this, mNfcAdapter);
	}

	@Override
	protected void onPause() {
		/*
		 * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
		 */
		stopForegroundDispatch(this, mNfcAdapter);

		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		/*
		 * This method gets called, when a new Intent gets associated with the current activity instance.
		 * Instead of creating a new activity, onNewIntent will be called. For more information have a look
		 * at the documentation.
		 * 
		 * In our case this method gets called, when the user attaches a Tag to the device.
		 */
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {

				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);

			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			// In case we would still use the Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();

			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
	}

	/**
	 * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
	 * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
	 */
	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][]{};

		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}

		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}

	/**
	 * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
	 * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
	 */
	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	/**
	 * Background task for reading the data. Do not block the UI thread while reading. 
	 * 
	 * @author Ralf Wondratschek
	 *
	 */
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];

			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				// NDEF is not supported by this Tag. 
				return null;
			}

			NdefMessage ndefMessage = ndef.getCachedNdefMessage();

			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "Unsupported Encoding", e);
					}
				}
			}

			return null;
		}

		private String readText(NdefRecord record) throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at 3.2.1 
			 * 
			 * http://www.nfc-forum.org/specs/
			 * 
			 * bit_7 defines encoding
			 * bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */

			byte[] payload = record.getPayload();

			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;

			// String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
			// e.g. "en"

			// Get the Text
			return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				//mTextView.setText("Read content: " + result);
				//Lectura del nfc consultar webservice
				Log.e("result", result);
				Intent msgIntent = new Intent(MainActivity.this, MiIntentService.class);
				msgIntent.putExtra("id", result);
				startService(msgIntent);

				finish();




				//				Boolean response = false, no_valid=false;
				//				String[] campos  ={"id"};
				//				String[] valores ={result};
				//				String server = modelo.postServer(campos, valores, "nfc.php",urlzbeltia);
				//				
				//				
				//				// si no hay conexion la respuesta es null
				//				if(server==null){ showAlert("No esta conectado a ninguna red"); }
				//				else{
				//					// obtener la respuesta de la peticion
				//			        try {		        	       
				//			            JSONObject json = Json.getJSON(server);
				//			            response = json.getBoolean("response");
				//			            int status = json.getInt("status");
				//			            Log.i("status", ""+status);
				//			            
				//				        // datos correctos iniciar sesion		            
				//				        if (response) {
				//				        	if(status==500){ //error
				//				        		
				//				            }else{
				//				            	int accion=json.getInt("accion");
				//				            	if(accion==1){ // login correcto
				//				            	finish();	
				//				            	}else{ //grabar targeta
				//				            		
				//				            		int id=json.getInt("id");
				//   			            		    finish();
				////				            		 Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
				////				            		 startActivity(browserIntent);
				////				            		
				//				            	}
				//				            	
				//				            	
				//				            }
				//				        	
				//				        }		        
				//				        else{ showAlert("Datos de acceso incorrectos. Favor de verificar y volver a intentar"); }			            
				//			        }
				//			        catch (JSONException e) { showAlert("Ocurrio un error en la aplicacion");  e.printStackTrace(); }
				//				}			


			}
		}
	}



	public void showAlert(String msge){
		new AlertDialog.Builder(this)		    
		.setMessage(msge)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which){}
		}).show();			
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
			Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			
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
	
}