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

public class MainMenu extends Activity {
	
	private LinearLayout paid;
	private Button whateverBtn;
	private Button femaleBtn;
	private Button maleBtn;
	private Spinner courses;
	private char selectedSex = 'w';
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
		
		Settings.COURSES = new ArrayList<String>();
		Settings.COURSES_ID = new ArrayList<Integer>();
		Settings.COURSES.add("Qualquer");
		Settings.COURSES_ID.add(-1);
		
		new GetCoursesAsync().execute();
		
		
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
	
	//Connect button clicked
	public void connect (View v) {
		backActivated = false;
		new ConnectAsync().execute();
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
							"&wantssex=" + selectedSex + "&wantscourse=" + Settings.COURSES_ID.get(courses.getSelectedItemPosition()) + "&api_key=" + Settings.me.getAPIKey();
					URL url = new URL(Settings.API_URL + "/connect");
				    
				    JSONObject json = new JSONObject(POSTConnection(urlParameters, url));
				    
				    //Set new global id conversation
				    if (json.getInt("response") == 1 || json.getInt("response") == 0)
				    	Settings.CONVERSATION_ID = json.getInt("conversation_id");
				
				    return json.getInt("response");
		        } catch (Exception e) {
		        	Log.e("doConnectException", e.getMessage());
		        	return -3;
		        }
		    } else {
		    	return -2;
		    }
		}
		
		@Override
		protected void onPostExecute (Integer result) {
			if (result == 0 || result == 1) {
				Intent intent = new Intent(MainMenu.this, Chat.class);
				intent.putExtra("type", result);
				startActivity(intent);
			} else if (result == -2) {
				Toast.makeText(MainMenu.this, "Preciso de uma conex�o com a internet pra logar!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainMenu.this, "Deu merda. Que que c fez?", Toast.LENGTH_SHORT).show();
			}
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
					Log.e("GetCoursesAsync", e.getMessage());
					return -3;
				}
			} else {
				return -2;
			}
		}
		
		@Override
		protected void onPostExecute (Integer result) {
			if (result == 1) {
				adapter = new ArrayAdapter<String>(MainMenu.this, android.R.layout.simple_spinner_item, Settings.COURSES);
				courses.setAdapter(adapter);
			} else if (result == -2) {
				Toast.makeText(MainMenu.this, "Preciso de uma conex�o com a internet pra logar!", Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(MainMenu.this, "Problema ao carregar lista de cursos, malz. =s", Toast.LENGTH_SHORT).show();
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
					String urlParameters = "user=" + Settings.me.getUserID() + "&api_key=" + Settings.me.getAPIKey();
					URL url = new URL(Settings.API_URL + "/logout");
				    
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
					Log.e("LogoutAsync", e.getMessage());
					return -3;
				}
			} else {
				return -2;
			}
		}
		
		@Override
		protected void onPostExecute (Integer result) {
			if (result == 1) {
				SaveSharedPreferences.destroyPreferences(MainMenu.this);
				startActivity(new Intent(MainMenu.this, Login.class));
				finish();
			} else if (result == -1) {
				Toast.makeText(MainMenu.this, "Problema ao fazer logout, malz. =s", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(MainMenu.this, "Preciso de uma conex�o com a internet pra logar!", Toast.LENGTH_SHORT).show();
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