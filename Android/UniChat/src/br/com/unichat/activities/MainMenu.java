package br.com.unichat.activities;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.unichat.classes.Conversation;
import br.com.unichat.classes.ConversationDAO;
import br.com.unichat.classes.SaveSharedPreferences;
import br.com.unichat.settings.Settings;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


public class MainMenu extends FragmentActivity {
	
	private ConversationDAO database;
	private ArrayList<Conversation> conversations;
	private ScrollView mContentLayout;
	private LinearLayout mConversationsLayout;
	private RelativeLayout mMenuLayout;
	private LinearLayout paid;
	private Button whateverBtn;
	private Button femaleBtn;
	private Button maleBtn;
	private Button connectBtn;
	private Button tab1;
	private Button tab2;
	private Spinner courses;
	private char selectedSex = 'w';
	private boolean backActivated = true;
	private boolean checkTab = true;		// True for tab 2
	private ArrayAdapter<String> adapter;
	private Intent intent;
	private TextView logout;
	private TextView test;
	private AdView mAdView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu_content);
		
		database = new ConversationDAO(this);
		conversations = null;
		
		// Get the other XML layouts
		LayoutInflater inflater = getLayoutInflater();
		mMenuLayout = (RelativeLayout) inflater.inflate(R.layout.activity_main_menu, null);
		mConversationsLayout = (LinearLayout) inflater.inflate(R.layout.activity_conversations, null);
		
		// Find widgets on the layouts
		
		// Content widgets
		tab1 = (Button) findViewById(R.id.tab1);
		tab2 = (Button) findViewById(R.id.tab2);
		
		// Menu widgets
		whateverBtn = (Button) mMenuLayout.findViewById(R.id.whatever_btn);
		femaleBtn = (Button) mMenuLayout.findViewById(R.id.female_btn);
		maleBtn = (Button) mMenuLayout.findViewById(R.id.male_btn);
		connectBtn = (Button) mMenuLayout.findViewById(R.id.connect_btn);
		courses = (Spinner) mMenuLayout.findViewById(R.id.courses_spinner);
		paid = (LinearLayout) mMenuLayout.findViewById(R.id.paid_part);
		logout = (TextView) mMenuLayout.findViewById(R.id.logoutText);
		mContentLayout = (ScrollView) findViewById(R.id.menu_content);
		
		// Conversations widgets
		test = (TextView) mConversationsLayout.findViewById(R.id.textView1);
		
		// Add first tab
		mContentLayout.addView(mMenuLayout);
		
		
		if (Settings.FREE_VERSION)
			paid.setVisibility(View.GONE);
		
		if (Settings.COURSES == null) {
			Settings.COURSES = new ArrayList<String>();
			Settings.COURSES_ID = new ArrayList<Integer>();
			Settings.COURSES.add("Qualquer");
			Settings.COURSES_ID.add(-1);
			new GetCoursesAsync().execute();
		} else {
			adapter = new ArrayAdapter<String>(MainMenu.this, android.R.layout.simple_spinner_item, Settings.COURSES);
			courses.setAdapter(adapter);
		}
		
		mAdView = (AdView) findViewById(R.id.adView);
		mAdView.loadAd(new AdRequest.Builder().build());
	}
	
	//Function to make the 3 buttons work like a RadioButton
	public void btnSelected (View v) {
		switch (v.getId()) {
			case R.id.female_btn:
				femaleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.female_pressed, 0, 0);
				whateverBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.whatever, 0, 0);
				maleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.male, 0, 0);
				selectedSex = 'f';
				break;
			case R.id.male_btn:
				femaleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.female, 0, 0);
				whateverBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.whatever, 0, 0);
				maleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.male_pressed, 0, 0);
				selectedSex = 'm';
				break;
			case R.id.whatever_btn:
				femaleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.female, 0, 0);
				whateverBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.whatever_pressed, 0, 0);
				maleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.male, 0, 0);
				selectedSex = 'w';
				break;
		}
	}
	
	public void changeTab (View v) {
		mContentLayout.removeAllViews();
		if (!checkTab) {
			mContentLayout.addView(mConversationsLayout);
			tab1.setBackgroundResource(R.drawable.tab_button_on);
			tab2.setBackgroundResource(R.drawable.tab_button_off);
			checkTab = true;
			
			ArrayList<Conversation> cv = database.getAllConversations();
			test.setText(cv.size() + "");
			
		} else {
			mContentLayout.addView(mMenuLayout);
			tab1.setBackgroundResource(R.drawable.tab_button_off);
			tab2.setBackgroundResource(R.drawable.tab_button_on);
			checkTab = false;
		}
	}
	
	// Connect button clicked
	public void connect (View v) {
		backActivated = false;
		connectBtn.setEnabled(false);
		new ConnectAsync().execute();
	}
	
	// Logout text clicked
	public void logoutText (View v) {
		logout.setTextColor(getResources().getColor(R.color.uniChatRed));
		logout();
	}
	
	//AsyncTask to manage the connection with the API
	private class ConnectAsync extends AsyncTask<Void, Void, Integer> {
		
		@Override
		protected Integer doInBackground (Void... params) {
			
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		   
			if (networkInfo != null && networkInfo.isConnected()) {
		        try {
		        	String urlParameters = "user=" + Settings.me.getUserID() +
							"&wantssex=" + selectedSex + "&wantscourse=" + Settings.COURSES_ID.get(courses.getSelectedItemPosition()) + "&api_key=" + Settings.me.getAPIKey()
							+ "&regId=" + Settings.me.getGCMRegisterKey();
					URL url = new URL(Settings.API_URL + "/connect");
					
					String res = POSTConnection(urlParameters, url);
				    JSONObject json = new JSONObject(res);
				    
				    //Set new global id conversation
				    if (json.getInt("response") == 1 || json.getInt("response") == 0) {
				    	Settings.CONVERSATION_ID = json.getInt("conversation_id");
				    
					    // Creating next intent and putting username if it is the case
					    String talkingTo = "Anônimo";
					    intent = new Intent(MainMenu.this, Chat.class);
					    if (json.getInt("response") == 1 && json.getInt("special") == 1)
					    	talkingTo = json.getString("username");
					    intent.putExtra("talkingTo", talkingTo);
					    if (json.getInt("response") == 1) {
					    	intent.putExtra("sendToRegId", json.getString("regId"));
					    	intent.putExtra("userId", json.getString("userId"));
					    }
				    }
				    
				    return json.getInt("response");
		        } catch (Exception e) {
		        	Log.e("doConnectException", e.toString());
		        	return -3;
		        }
		    } else {
		    	return -3;
		    }
		}
		
		@Override
		protected void onPostExecute (Integer result) {
			if (result == 0 || result == 1) {
				intent.putExtra("type", result);
				startActivityForResult(intent, 1);
			} else if (result == -1) {
				Toast.makeText(MainMenu.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(MainMenu.this, "Chave inválida para usuário. Esse usuário fez login em outro aparelho.", Toast.LENGTH_LONG).show();
			} else if (result == -3) {
				Toast.makeText(MainMenu.this, "Ou a net caiu ou tá ruim pra caramba... Tenta denovo...", Toast.LENGTH_SHORT).show();
			}
			connectBtn.setEnabled(true);
			backActivated = true;
		}
		
	}
	
	public class GetCoursesAsync extends AsyncTask <Void, Void, Integer> {
		@Override
		protected Integer doInBackground (Void... Params) {
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			
			if (networkInfo != null && networkInfo.isConnected()) {
				try {
					String urlParameters = "user=" + Settings.me.getUserID() + "&api_key=" + Settings.me.getAPIKey();
					URL url = new URL(Settings.API_URL + "/get_courses");
				    
				    JSONObject json = new JSONObject(POSTConnection(urlParameters, url));
					
				    JSONArray jsoncourses;
				    
				    if (json.getInt("response") == 1) {
				    	jsoncourses = json.getJSONArray("courses");
				    	
				    	for (int i = 0; i < jsoncourses.length(); i++) {
				    		JSONObject course = jsoncourses.getJSONObject(i);
				    		Settings.COURSES.add(course.getString("name"));
				    		Settings.COURSES_ID.add(course.getInt("id"));
				    	}
				    }
				    
					return json.getInt("response");
				} catch (Exception e) {
					Log.e("GetCoursesAsync", e.toString());
					return -1;
				}
			} else {
				return -3;
			}
		}
		
		@Override
		protected void onPostExecute (Integer result) {
			if (result == 1) {
				adapter = new ArrayAdapter<String>(MainMenu.this, android.R.layout.simple_spinner_item, Settings.COURSES);
				courses.setAdapter(adapter);
			} else if (result == 0) {
				Toast.makeText(MainMenu.this, "Essa universidade não tem cursos, lol", Toast.LENGTH_SHORT).show();
			} else if (result == -1) {
				Toast.makeText(MainMenu.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(MainMenu.this, "Chave inválida para usuário. Esse usuário fez login em outro aparelho.", Toast.LENGTH_LONG).show();
			} else if (result == -3) {
				Toast.makeText(MainMenu.this, "Preciso de uma conexão com a internet pra logar!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	// Result from Chat Activity
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_CANCELED) {    
				logout();
			}
		}
	}
	
	public void logout () {
		new LogoutAsync().execute();
	}
	
	public class LogoutAsync extends AsyncTask <Void, Void, Integer> {
		@Override
		protected Integer doInBackground (Void... Params) {
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			
			if (networkInfo != null && networkInfo.isConnected()) {
				try {
					String urlParameters = "user=" + Settings.me.getUserID() + "&api_key=" + Settings.me.getAPIKey() + "&username=" + Settings.me.getUsername();
					URL url = new URL(Settings.API_URL + "/logout");
				    
				    JSONObject json = new JSONObject(POSTConnection(urlParameters, url));
				    
					return json.getInt("response");
				} catch (Exception e) {
					Log.e("LogoutAsync", e.toString());
					return -3;
				}
			} else {
				return -3;
			}
		}
		
		@Override
		protected void onPostExecute (Integer result) {
			if (result == 1 || result == -2) {
				SaveSharedPreferences.destroyPreferences(MainMenu.this);
				startActivity(new Intent(MainMenu.this, Login.class));
				finish();
			} else if (result == -1) {
				Toast.makeText(MainMenu.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_LONG).show();
			} else if (result == -3) {
				Toast.makeText(MainMenu.this, "Preciso de uma conexão com a internet pra logar!", Toast.LENGTH_SHORT).show();
			}
		
		}
	}
	
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_logout:
	        logout();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {
		if (backActivated)
			super.onBackPressed();
	}
	
}