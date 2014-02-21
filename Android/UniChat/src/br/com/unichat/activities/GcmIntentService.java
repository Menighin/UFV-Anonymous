package br.com.unichat.activities;

import android.app.IntentService;
import android.content.Intent;

public class GcmIntentService extends IntentService {

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
	}
}
