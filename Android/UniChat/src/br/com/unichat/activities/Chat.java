package br.com.unichat.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.unichat.classes.ConversationArrayAdapter;
import br.com.unichat.classes.Message;
import br.com.unichat.settings.Settings;

public class Chat extends Activity {
	
	private EditText message;
	private TextView talkingTo;
	private String sendToRegId;
	private Bundle extras;
	private ListView conversation;
	private ConversationArrayAdapter adapter;
	private Handler handler;
	private boolean result_ok = true;
	private BroadcastReceiver messageReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		
		message = (EditText) findViewById(R.id.message);
		talkingTo = (TextView) findViewById(R.id.talking_to);
		adapter = new ConversationArrayAdapter(getApplicationContext(), R.layout.list_item_message);
		conversation = (ListView) findViewById(R.id.list_messages);
		conversation.setAdapter(adapter);
		
		// Solving if the activity is either a client or server type
		extras = getIntent().getExtras();
		if (extras.getInt("type") == 0) {
			talkingTo.setText("Esperando o anônimo(a) se conectar...");
			message.setEnabled(false);
		} else {
			talkingTo.setText("Falando com: " + extras.getString("talkingTo"));
			talkingTo.setTextColor(getResources().getColor(R.color.uniChatGreen));
			sendToRegId = extras.getString("sendToRegId");
		}
		
		this.messageReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();
				//String messageString = bundle.getString("message");
				try {
					JSONObject messageJSON = new JSONObject(bundle.getString("message"));
					
					if(messageJSON.getString("message").equals("[fechaOChatUniChat]")) {
						Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(300);
	
						talkingTo.setText("Anônimo se desconectou :( ...");
						talkingTo.setTextColor(getResources().getColor(R.color.uniChatRed));
						message.setText("");
						message.setEnabled(false);
					} else if (messageJSON.getString("message").equals("[abreOChatUniChat]")) {
						Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(300);
						
						talkingTo.setText("Falando com: " + messageJSON.getString("user"));
						talkingTo.setTextColor(getResources().getColor(R.color.uniChatGreen));
						message.setEnabled(true);
						sendToRegId = messageJSON.getString("regId");
						
					} else {
						Date date = new Date();
						DateFormat format = DateFormat.getTimeInstance();
						Message message = new Message(true, messageJSON.getString("message"), format.format(date).substring(0, 5));
						adapter.add(message);
					}
				} catch (Exception e) {
					Log.e("RECEIVE MESSAGE", "INVALID JSON");
				}
				
				// Focus on last received message
				if (conversation.getCount() > 1)
					conversation.setSelection(conversation.getCount() - 1);

			}
		};		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(this.messageReceiver, new IntentFilter(Chat.class.getName()));
	}

	// Function called when user hit the send button on screen
	public void sendMessage (View v) {
		if (message.getText().length() > 0) {
			Message msg = new Message(false, message.getText().toString(), "");
			adapter.add(msg);
			Integer lastMsg = adapter.getItem(msg);
			
			try {
				String urlParameters = "message=" + URLEncoder.encode(message.getText().toString(), "UTF-8")
						+ "&user=" + Settings.me.getUserID() + "&api_key=" +  URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8")
						+ "&regId=" + sendToRegId;
				message.getText().clear();
	    		new SendMessageAsync().execute(urlParameters, lastMsg.toString());
			} catch (Exception e) {
				Log.e ("SEND BUTTON", e.toString());
			}
			
			conversation.setSelection(conversation.getCount() - 1);
		}
	}
	
	// AsyncTask to send the message to server
	private class SendMessageAsync extends AsyncTask<String, Void, Integer> {
		
		@Override
		protected Integer doInBackground (String... params) {
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		   
			if (networkInfo != null && networkInfo.isConnected()) {
		        try {
		        	URL url = new URL(Settings.API_URL + "/send_message");
		    	    JSONObject json = new JSONObject(POSTConnection (params[0], url));
		    	    
		    	    Log.i("SEND_MESSAGE", json.toString());
		    	    
		    	    if (Integer.parseInt(params[1]) != -1) {
			    	    android.os.Message msg = new android.os.Message();
			    	    msg.what = Integer.parseInt(params[1]);
			    	    msg.obj = json.getString("time");
			    	    handler.sendMessage(msg);
		    	    }
		    	    
		    	    return json.getInt("response");
		        } catch (Exception e) {
		        	Log.e("SendMessageException", e.toString());
		        	return -1;
		        }
		    } else {
		    	return -3;
		    }
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Log.i("SEND MESAGE", "OK");
			} else if (result == -1) {
				Toast.makeText(Chat.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(Chat.this, "Chave inválida para usuário. Talvez você logou em outro dispositivo?", Toast.LENGTH_SHORT).show();
			} else if (result == -3) {
				Toast.makeText(Chat.this, "Preciso de uma conexão com a internet pra logar!", Toast.LENGTH_SHORT).show();
			}
		}
	}	
	
	// Function to make the POST request to the server
	private String POSTConnection (String urlParameters, URL url) throws Exception{

		//Connection parameters
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
	    conn.setDoOutput(true);
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setRequestProperty("charset", "utf-8");
	    
	    //Send request
		DataOutputStream wr = new DataOutputStream (conn.getOutputStream ());
		wr.writeBytes (urlParameters);
		wr.flush ();
		wr.close ();
		
		//Get Response	
	    InputStream is = conn.getInputStream();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    String line;
	    StringBuffer response = new StringBuffer(); 
	    while((line = rd.readLine()) != null) {
	    	response.append(line);
	    	response.append('\r');
	    }
	    rd.close();
	    
	    return response.toString();
	}
	
	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);     
		finish();
	}
	
	@Override
	public void onDestroy() {
		if(isFinishing()) {
			if (result_ok) {
				try {
					String urlParameters = "message=[fechaOChatUniChat]" + "&user=" + Settings.me.getUserID() 
							+ "&api_key=" + URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8") + "&regId=" + sendToRegId;
					new SendMessageAsync().execute(urlParameters, "-1");
				} catch (Exception e) {
					Log.e("Error onDestroy", e.toString());
				}
			}
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server, menu);
		return true;
	}

}
