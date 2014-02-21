package br.com.unichat.activities;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

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
				
			} else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				
			} else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				
			}
		}
	}
}
