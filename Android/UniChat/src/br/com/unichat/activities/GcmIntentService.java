package br.com.unichat.activities;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	
	private String json;

	public GcmIntentService() {
		super("GcmIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging googleCloud = GoogleCloudMessaging.getInstance(getApplicationContext());
		String messageType = googleCloud.getMessageType(intent);
		
		// Parsing bundle to JSON
		json = "{";
		int i = 0;
		for (String key : extras.keySet()) {
			if (i == 0)
				json += "\"" + key + "\":\"" + extras.get(key) + "\"";
			else
				json += ",\"" + key + "\":\"" + extras.get(key) + "\"";
			i++;
		}
		json += "}";
		
		if(!extras.isEmpty()) {
			
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.camera_on)
			        .setContentTitle("My notification")
			        .setContentText(extras.getString("message"));
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(this, MainMenu.class);
			//resultIntent.putExtra("type", "old");
			//resultIntent.putExtra("user_id", cv.get(position).getAnonymID());
			
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainMenu.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(1, mBuilder.build());
			
			
			if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.i("Informações do GCM - ERRO", extras.toString());
			} else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.i("Informações do GCM - DELETED", extras.toString());
			} else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				Log.i("Informações do GCM - Mensagem", json);
				sendMessageToActivity();
			}
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void sendMessageToActivity() {
		Intent intent = new Intent(Chat.class.getName());
	    intent.putExtra("message", json);
	    sendBroadcast(intent);
	}
}
