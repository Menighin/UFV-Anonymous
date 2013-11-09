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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.unichat.classes.SaveSharedPreferences;
import br.com.unichat.settings.Settings;

import br.com.unichat.activities.R;

public class MainMenu extends Activity {
	
	private LinearLayout paid;
	private Button whateverBtn;
	private Button femaleBtn;
	private Button maleBtn;
	private Spinner courses;
	private int selectedSex = 0;
	private Handler handler;
	private Toast toast;
	private boolean backActivated = true;
	private ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_menu);
		
		whateverBtn = (Button) findViewById(R.id.whatever_btn);
		femaleBtn = (Button) findViewById(R.id.female_btn);
		maleBtn = (Button) findViewById(R.id.male_btn);
		courses = (Spinner)findViewById(R.id.courses_spinner);
		paid = (LinearLayout) findViewById(R.id.paid_part);
		
		if (Settings.FREE_VERSION)
			paid.setVisibility(View.GONE);
		
		handler = new Handler (new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				toast = Toast.makeText(MainMenu.this, (String) msg.obj, Toast.LENGTH_SHORT);
				toast.show();
				return true;
			}
		});
		
		Settings.COURSES = new ArrayList<String>();
		Settings.COURSES.add("Qualquer");
		
		new getCoursesAsync().execute();
	}
	
	//Function to make the 3 buttons work like a RadioButton
	public void btnSelected (View v) {
		switch (v.getId()) {
			case R.id.female_btn:
				femaleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.female_pressed, 0, 0);
				whateverBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.whatever, 0, 0);
				maleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.male, 0, 0);
				selectedSex = 1;
				break;
			case R.id.male_btn:
				femaleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.female, 0, 0);
				whateverBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.whatever, 0, 0);
				maleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.male_pressed, 0, 0);
				selectedSex = 2;
				break;
			case R.id.whatever_btn:
				femaleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.female, 0, 0);
				whateverBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.whatever_pressed, 0, 0);
				maleBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.male, 0, 0);
				selectedSex = 0;
				break;
		}
	}
	
	//Connect button clicked
	public void connect (View v) {
		backActivated = false;
		new ConnectAsync().execute();
	}
	
	//AsyncTask to manage the connection with the API
	private class ConnectAsync extends AsyncTask<Void, Void, Integer> {
		
		@Override
		protected Integer doInBackground (Void... params) {
			Message msg = new Message();
			
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		   
			if (networkInfo != null && networkInfo.isConnected()) {
		        try {
		        	return doConnect();
		        } catch (Exception e) {
		        	Log.e("doConnectException", e.getMessage());
		        	return -2;
		        }
		    } else {
		    	msg.obj = "Preciso de uma conexão com a internet pra logar!";
		    	handler.sendMessage(msg);
		    	return -2;
		    }
		}
		
		@Override
		protected void onPostExecute (Integer result) {
			if (result == 0 || result == 1) {
				Intent intent = new Intent(MainMenu.this, Chat.class);
				intent.putExtra("type", result);
				startActivity(intent);
			} else {
				Toast.makeText(MainMenu.this, "Deu merda. Que que c fez?", Toast.LENGTH_SHORT).show();
			}
			backActivated = true;
		}
		
	}
	
	private int doConnect() throws Exception {
		String urlParameters = "user=" + Settings.me.getUserID() +
					"&wantssex=w&wantscourse=1" + "&api_key=" + Settings.me.getAPIKey();
		URL url = new URL(Settings.API_URL + "/connect");
		
		//Connection parameters
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
	    conn.setDoOutput(true);
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    
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
	    
	    JSONObject json = new JSONObject(response.toString());
	    
	    //Set new global id conversation
	    if (json.getInt("response") == 1 || json.getInt("response") == 0)
	    	Settings.CONVERSATION_ID = json.getInt("conversation_id");
		
		return json.getInt("response");
	}
	
	
	public class getCoursesAsync extends AsyncTask <Void, Void, Integer> {
		@Override
		protected Integer doInBackground (Void... Params) {
			try {
				return doGetCourses();
			} catch (Exception e) {
				Log.e("doGetCourses", e.getMessage());
				return -2;
			}
		}
		
		@Override
		protected void onPostExecute (Integer result) {
			if (result == 1) {
				adapter = new ArrayAdapter<String>(MainMenu.this, android.R.layout.simple_spinner_item, Settings.COURSES);
				courses.setAdapter(adapter);
			} else
				Toast.makeText(MainMenu.this, "Problema ao carregar lista de cursos, malz. =s", Toast.LENGTH_SHORT).show();
		}
	}
	
	public Integer doGetCourses() throws Exception {
		String urlParameters = "user=" + Settings.me.getUserID() +
				"&university_id=" + Settings.me.getUniversity() + "&api_key=" + Settings.me.getAPIKey();
		URL url = new URL(Settings.API_URL + "/get_courses");
		
		//Connection parameters
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
	    conn.setDoOutput(true);
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    
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
	    
	    JSONObject json = new JSONObject(response.toString());
		
	    JSONArray jsoncourses;
	    
	    if (json.getInt("response") == 1) {
	    	jsoncourses = json.getJSONArray("courses");
	    	
	    	for (int i = 0; i < jsoncourses.length(); i++) {
	    		JSONObject course = jsoncourses.getJSONObject(i);
	    		Settings.COURSES.add(course.getString("name"));
	    	}
	    }
	    
		return json.getInt("response");
	}
	
	public void logout () {
		SaveSharedPreferences.destroyPreferences(MainMenu.this);
		startActivity(new Intent(MainMenu.this, Login.class));
		finish();
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
