package br.com.unichat.activities;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	
	private String message;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging googleCloud = GoogleCloudMessaging.getInstance(getApplicationContext());
		String messageType = googleCloud.getMessageType(intent);
		
		if(!extras.isEmpty()) {
			if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.i("Informações do GCM - ERRO", extras.toString());
			} else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.i("Informações do GCM - DELETED", extras.toString());
			} else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				Log.i("Informações do GCM - Mensagem", extras.getString("price"));
				message =  extras.getString("price");
				sendMessageToActivity();
			}
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void sendMessageToActivity() {
		Intent intent = new Intent(Chat.class.getName());
	    intent.putExtra("message", message);
	    sendBroadcast(intent);
	}
}
