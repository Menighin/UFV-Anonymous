package br.com.unichat.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	private String talkingToName = "Anônimo";
	private Bundle extras;
	private ListView conversation;
	private ConversationArrayAdapter adapter;
	private boolean connected;
	private Timer myTimer;
	private short getMessagesFrom;
	private short sentMessagesFrom;
	private Handler handler;
	private GetMessagesAsync getMessage = null;
	private boolean result_ok = true;
	
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
		
		// Handler to update time on messages sent by this user
		handler = new Handler(new Handler.Callback() { 
			@Override
			public boolean handleMessage (android.os.Message msg) { 
				adapter.updateTime(msg.what, msg.obj.toString());
				return true;
			} 
		});
		
		// Solving if the activity is either a client or server type
		extras = getIntent().getExtras();
		if (extras.getInt("type") == 0) {
			talkingTo.setText("Esperando o anônimo(a) se conectar...");
			connected = false;
			getMessagesFrom = 1;
			sentMessagesFrom = 0;
			message.setEnabled(false);
		}
		else {
			talkingTo.setText("Falando com: " + extras.getString("talkingTo"));
			talkingTo.setTextColor(getResources().getColor(R.color.uniChatGreen));
			connected = true;
			getMessagesFrom = 0;
			sentMessagesFrom = 1;
		}
		
		// Setting function to be called from time to time
		myTimer = new Timer();
		if (!connected) // Server
		    myTimer.schedule(new TimerTask() {          
		        @Override
		        public void run() {
		            new IsReadyAsync().execute();
		        }
		    }, 0, Settings.CHECK_CONVERSATION_READY_TIME);
		else // Client
			myTimer.schedule(new TimerTask() {          
		        @Override
		        public void run() {
		        	if (getMessage == null || getMessage.getStatus() == AsyncTask.Status.FINISHED) {
			            getMessage = new GetMessagesAsync();
			            getMessage.execute();
		        	}
		        }
		    }, 0, Settings.CHECK_MESSAGES_TIME);
		
		
	}

	// Function called when user hit the send button on screen
	public void sendMessage (View v) {
		if (message.getText().length() > 0) {
			Message msg = new Message(false, message.getText().toString(), "", (short)0);
			adapter.add(msg);
			Integer lastMsg = adapter.getItem(msg);
			
			try {
				String urlParameters = "conversation_id=" + Settings.CONVERSATION_ID + "&message=" + URLEncoder.encode(message.getText().toString(), "UTF-8") + 
						"&author=" + sentMessagesFrom + "&flag=0" + "&user=" + Settings.me.getUserID() + "&api_key=" +  URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8");
				message.getText().clear();
	    		new SendMessageAsync().execute(urlParameters, lastMsg.toString());
			} catch (Exception e) {
				Log.e ("SEND BUTTON", e.toString());
			}
			
			conversation.setSelection(conversation.getCount() - 1);
		}
	}
	
	// AsyncTask to check if another user connected
	private class IsReadyAsync extends AsyncTask<Void, Void, Integer> {
		
		@Override
		protected Integer doInBackground(Void...params) {
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		   
			if (networkInfo != null && networkInfo.isConnected()) {
		        try {
		        	String urlParameters = "conversation_id=" + Settings.CONVERSATION_ID + "&user=" + Settings.me.getUserID() + "&api_key=" + 
		        			URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8");
		    		URL url = new URL(Settings.API_URL + "/is_conversation_ready");
		    	    
		    	    JSONObject json = new JSONObject(POSTConnection (urlParameters, url));
		    	    
		    	    if (json.getInt("response") == 1 && json.getInt("special") == 1)
		    	    	talkingToName = json.getString("username");
		    	    
		    	    return json.getInt("response");
		        } catch (Exception e) {
		        	Log.e("IsReadyException", e.toString());
		        	return -1;
		        }
		    } else {
		    	return -3;
		    }
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(300);
				
				talkingTo.setText("Falando com: " + talkingToName);
				talkingTo.setTextColor(getResources().getColor(R.color.uniChatGreen));
				message.setEnabled(true);
				myTimer.cancel();
				myTimer = new Timer();
				myTimer.schedule(new TimerTask() {          
			        @Override
			        public void run() {
			        	if (getMessage == null || getMessage.getStatus() == AsyncTask.Status.FINISHED) {
				            getMessage = new GetMessagesAsync();
				            getMessage.execute();
			        	}
			        }
			    }, 0, Settings.CHECK_MESSAGES_TIME);
			} else if (result == -1) {
				Toast.makeText(Chat.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(Chat.this, "Este usuário fez login em outro dispositivo", Toast.LENGTH_SHORT).show();
				result_ok = false;
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);     
				finish();
			} else if (result == -3) {
				Toast.makeText(Chat.this, "Preciso de uma conexão com a internet pra logar!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	// AsyncTask to constantly check if the other user sent messages
	private class GetMessagesAsync extends AsyncTask<Void, Void, ArrayList<Message>> {
		@Override
		protected ArrayList<Message> doInBackground (Void... params) {
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		   
			ArrayList<Message> msgs = null;
			
			if (networkInfo != null && networkInfo.isConnected()) {
		        try {
		        	String urlParameters = "conversation_id=" + Settings.CONVERSATION_ID + "&author=" + getMessagesFrom + 
		        			"&user=" + Settings.me.getUserID() + "&api_key=" + URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8");
		    		URL url = new URL(Settings.API_URL + "/get_message");
		    	    
		    	    JSONObject json = new JSONObject(POSTConnection (urlParameters, url));
		    	    JSONArray jsonmsgs;
		    	    
		    	    if (json.getInt("response") == 0)
		    	    	msgs = new ArrayList<Message>();
		    	    else if (json.getInt("response") == 1) {
		    	    	msgs = new ArrayList<Message>();
		    	    	jsonmsgs = json.getJSONArray("messages");
		    	    	
		    	    	for (int i = 0; i < jsonmsgs.length(); i++) {
		    	    		JSONObject msg = jsonmsgs.getJSONObject(i);
		    	    		msgs.add(new Message(true, msg.getString("message"), msg.getString("time").substring(11, 16), (short)msg.getInt("END_FLAG")));
		    	    	}
		    	    } else if (json.getInt("response") == -2) { // API key validation fail
		    	    	msgs = new ArrayList<Message>();
		    	    	msgs.add(new Message(true, "null", "00:00", (short)2));
		    	    }
		    	    
		    	    return msgs;
		        } catch (Exception e) {
		        	Log.e("GetMessageException", e.toString());
		        	return msgs;
		        }
		    } else {
		    	return msgs;
		    }
		}
		
		@Override
		protected void onPostExecute (ArrayList<Message> msgs) {
			if (msgs == null) {
				Toast.makeText(Chat.this, "Vish, deu merda, desculpa :( (Você tá conectado na internet?)", Toast.LENGTH_SHORT).show();
			} else {
				for (Message msg : msgs) {
					if (msg.FLAG == 0)
						adapter.add(msg);
					else if (msg.FLAG == 2) {
						Toast.makeText(Chat.this, "Este usuário fez login em outro dispositivo", Toast.LENGTH_SHORT).show();
						result_ok = false;
						Intent returnIntent = new Intent();
						setResult(RESULT_CANCELED, returnIntent);
						finish();
					}
					else {
						Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(300);
						
						talkingTo.setText("Anônimo se desconectou :( ...");
						talkingTo.setTextColor(getResources().getColor(R.color.uniChatRed));
						message.setText("");
						message.setEnabled(false);
					}
				}
				if (msgs.size() > 0)
					conversation.setSelection(conversation.getCount() - 1);
			}
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
			myTimer.cancel();
			if (result_ok) {
				try {
					String urlParameters = "conversation_id=" + Settings.CONVERSATION_ID + "&message=end&author=" + sentMessagesFrom + "&flag=1"
							+ "&user=" + Settings.me.getUserID() + "&api_key=" + URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8");
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
