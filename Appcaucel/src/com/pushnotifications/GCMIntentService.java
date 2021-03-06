package com.pushnotifications;

import static com.pushnotifications.CommonUtilities.SENDER_ID;
import static com.pushnotifications.CommonUtilities.displayMessage;


import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pushnotifications.R;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
       // Log.d("NAME", Tiempoterminado.name);
        ServerUtilities.register(context, Tiempoterminado.id_usuario, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("data");
        
        //displayMessage(context, message);
        // notifies user
        //generateNotification(context, message);
        Modelo modelo = new Modelo();
        modelo.setContext(context);
       // String status= modelo.getLocal("status");
        
        try{
        	JSONObject json = Json.getJSON(message);
        	
        	int tipo= json.getInt("tipo");
            String mensaje = json.getString("mensaje");
            switch(tipo){
            case 1:
            	
            	generateNotification(context, mensaje);
            	break;
            case 2:
            	//String statustiempo= json.getString("status");
            	//modelo.setLocal("status", status);
            	//Log.e("Estatus",status);
            	//Intent msgIntent = new Intent(this, IntentPausa.class);
				//msgIntent.putExtra("id", result);
				//startService(msgIntent);
            	String status= modelo.getLocal("status");
            	
            	String statustiempo= json.getString("statustiempo");
            	modelo.setLocal("statustiempo", statustiempo);
            	
            	if(status.equals("0")){
            		startActivity(new Intent(this, 
            				Tiempoterminado.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            	}
            	break;
            
            }
            
            
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, Tiempoterminado.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }

}
