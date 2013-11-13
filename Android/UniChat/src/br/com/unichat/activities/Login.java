package br.com.unichat.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.com.unichat.classes.SaveSharedPreferences;
import br.com.unichat.classes.User;
import br.com.unichat.settings.Settings;

import br.com.unichat.activities.R;

public class Login extends Activity {

	private EditText user;
	private EditText password;
	private Button login;
	private LoginAsync loginAsync = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		//Setting variables
		user = (EditText) findViewById(R.id.login_user);
		password = (EditText) findViewById(R.id.login_password);
		login = (Button) findViewById(R.id.login_btn);
	}
	
	public void loginButton (View v) {
		boolean valid = true;
		View focusView = null;
		
		//Validating info
		if (password.getText().toString().length() <= 0) {
			password.setError("Digite uma senha");
			valid = false;
			focusView = password;
		} 
		
		if (user.getText().toString().length() <= 0) {
			user.setError("Digite um usu�rio");
			valid = false;
			focusView = user;
		}
		
		//Action!
		if (valid) {
			user.setEnabled(false);
			password.setEnabled(false);
			login.setEnabled(false);
			loginAsync = new LoginAsync();
			loginAsync.execute();
		} else {
			focusView.requestFocus();
		}
		
	}
	
	private class LoginAsync extends AsyncTask<Void, Void, Integer> {
		
		@Override
		protected Integer doInBackground (Void... params) {
			
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		   
			if (networkInfo != null && networkInfo.isConnected()) {
		        try {
		        	if (!isCancelled())
		        		return doLogin();
		        	else
		        		return -2;
		        } catch (Exception e) {
		        	Log.e("doLoginException", e.getMessage());
		        	Log.e("doLoginException", e.toString());
		        	return -2;
		        }
		    } else {
		    	return -5;
		    }
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			
			if (result == 1) {
				startActivity(new Intent(Login.this, MainMenu.class));
				finish();
			} else {
				user.setEnabled(true);
				password.setEnabled(true);
				login.setEnabled(true);
	        	if (result == 0) {
	        		Toast.makeText(Login.this, "Usu�rio n�o validado. Verifique seu email @ufv.com.br", Toast.LENGTH_SHORT).show();
	        	} else if (result == -1) {
	        		Toast.makeText(Login.this, "Usu�rio ou senha inv�lidos", Toast.LENGTH_SHORT).show();
	        	} else if (result == -2) {
	        		Toast.makeText(Login.this, "Esse usu�rio j� est� logado em outro dispositivo", Toast.LENGTH_SHORT).show();
	        	} else if (result == -5) {
	        		Toast.makeText(Login.this, "Preciso de uma conex�o com a internet pra logar!", Toast.LENGTH_SHORT).show();
	        	}
			}
		}
		
	}
	
	private int doLogin() throws Exception {
		
		String urlParameters = "username=" + URLEncoder.encode(user.getText().toString(), "UTF-8") + "&password=" + password.getText().toString();
		URL url = new URL(Settings.API_URL + "/login");
		
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
	    
	    //Set new global user
	    if (json.getInt("response") == 1) {
	    	Settings.me = new User (
	    			json.getInt("id"), json.getString("username"), json.getInt("courseID"), json.getString("sex"), json.getInt("universityID"), json.getString("apikey"));
	    	SaveSharedPreferences.createPreferences(
	    			Login.this, true, json.getString("username"), json.getInt("id"), json.getInt("courseID"), json.getInt("universityID"), json.getString("sex"), json.getString("apikey"));
	    }
	    return json.getInt("response");
	    
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (loginAsync != null) {
			loginAsync.cancel(true);
		}
	}

}
