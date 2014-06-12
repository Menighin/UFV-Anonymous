package br.com.unichat.activities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import br.com.unichat.classes.Conversation;
import br.com.unichat.classes.ConversationDAO;
import br.com.unichat.classes.Message;
import br.com.unichat.settings.Settings;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	
	private Bundle extras;
	private ConversationDAO database;
	
	public GcmIntentService() {
		super("GcmIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		extras = intent.getExtras();
		GoogleCloudMessaging googleCloud = GoogleCloudMessaging.getInstance(getApplicationContext());
		String messageType = googleCloud.getMessageType(intent);
		
		if(!extras.isEmpty()) {
			try {
				database = new ConversationDAO(this);
				JSONObject messageJSON = new JSONObject(extras.getString("message"));
				int id = messageJSON.getInt("user_from");
				String msg = messageJSON.getString("message");
				
				// Notification title
				String notificationTitle;
				Conversation cv = database.getConversation(id);
				if (cv != null) {
					notificationTitle = cv.getAnonymousAlias();
				} else {
					notificationTitle = "Anônimo";
				}
				
				// Notification message
				String notificationMessage;
				if (msg.length() > 12 && msg.substring(0, 12).equals("[UniChatImg]"))
					notificationMessage = "Imagem";
				else if (msg.equals("[uniChatFechaSsaPorra]"))
					notificationMessage = "Você foi excluído(a) por esse anônimo!";
				else
					notificationMessage = msg;
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(this)
				        .setSmallIcon(R.drawable.notification)
				        .setAutoCancel(true)
				        .setVibrate(new long[] {100, 300, 100})
				        .setLights(Color.RED, 200, 1500)
				        .setContentTitle(notificationTitle)
				        .setContentText(notificationMessage)
				        .setTicker("Nova mensagem no UniChat!");
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(this, SplashScreen.class);
				//resultIntent.putExtra("type", "old");
				//resultIntent.putExtra("user_id", messageJSON.getString("user_from"));
				
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(SplashScreen.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				
				// Adding message on database
				boolean closingMessage = messageJSON.getString("message").equals("[uniChatFechaSsaPorra]");
				Message message;
				Log.d("CURRENT CONVERSATION", Settings.CURRENT_CONVERSATION_ID + " == " + id + "?");
				if (Settings.CURRENT_CONVERSATION_ID != id) {
					if (!closingMessage) {
						if (messageJSON.getString("message").length() > 12 && messageJSON.getString("message").substring(0, 12).equals("[UniChatImg]")) {
							message = new Message.Builder()
								.left(true)
								.message(messageJSON.getString("message").substring(12))
								.time(DateFormat.getTimeInstance().format(new Date()).substring(0,5))
								.image(true)
								.wasDownloaded(false)
								.imagePath("")
								.createMessage();
						} else {
							message = new Message.Builder()
								.left(true)
								.message(msg)
								.time(DateFormat.getTimeInstance().format(new Date()).substring(0,5))
								.read(false)
								.createMessage();
						}
					
						if (database.getConversation(id) != null) {
							database.addMessage(id, message);
						} else {
							ArrayList<Message> messages = new ArrayList<Message>();
							messages.add(message);
							int randImg = new Random().nextInt(5);
							Conversation c = new Conversation(id, "Anônimo", DateFormat.getTimeInstance().format(new Date()).substring(0,5), false, messages, randImg, false);
							database.addConversation(c);
						}
					} else {
						database.updateConversationClosed(id);
					}
					
					if (database.isConversation(id)) {
						mNotificationManager.notify(1, mBuilder.build());
						Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(300);
					}
				}
				
				if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
					Log.i("Informações do GCM - ERRO", extras.toString());
				} else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
					Log.i("Informações do GCM - DELETED", extras.toString());
				} else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
					Log.i("Informações do GCM - Mensagem", extras.toString());
					sendMessageToActivity();
				}
			} catch (Exception e) {
				Log.e("RECEIVE MESSAGE INTENT", "INVALID JSON:" + extras.getString("message"));
			}
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void sendMessageToActivity() {
		Intent intent = new Intent(Chat.class.getName());
	    intent.putExtra("message", extras.getString("message"));
	    sendBroadcast(intent);
	}
}
