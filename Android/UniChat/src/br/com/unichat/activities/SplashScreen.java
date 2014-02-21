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

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import br.com.unichat.classes.SaveSharedPreferences;
import br.com.unichat.settings.Settings;

import br.com.unichat.activities.R;

/*
 * Activity que mostra a tela inicial do aplicativo. O tempo est� definido na classe Settings.java
 */

public class SplashScreen extends Activity {
	
	private Handler hand;
	private Runnable r;
	private Intent i;
	
	private GoogleCloudMessaging googleCloud;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);
		
		if(this.googleCloud == null) {
			this.googleCloud = GoogleCloudMessaging.getInstance(getApplicationContext());
		}
		
		String registrationKey = getRegistrationKey(getApplicationContext());
		if(registrationKey == null) {
			new RegisterInGCM().execute();
		}
		Log.i("Informações do GCM", registrationKey);
		
		hand = new Handler();
		r = new Runnable () {
			@Override
            public void run() {
        		i = new Intent(SplashScreen.this, Login.class);
                startActivity(i);
                finish();
        	}
		};
		
		if (SaveSharedPreferences.getLoggedState(SplashScreen.this)) {
    		SaveSharedPreferences.setUser(SplashScreen.this);
    		Settings.COURSES = new ArrayList<String>();
    		Settings.COURSES_ID = new ArrayList<Integer>();
    		Settings.COURSES.add("Qualquer");
    		Settings.COURSES_ID.add(-1);
    		new GetCoursesAsync().execute();
    	} else
    		hand.postDelayed(r, Settings.SPLASH_TIME);
	}
	
	/**
	 * Método que extrai do SharedPreferences da aplicação a registration key do GCM.
	 * @param context Context da aplicação.
	 * @return Null caso não seja encontrada a registration key ou retorna o valor da user key
	 */
	private String getRegistrationKey(Context context) {
		/* Pega o SharedPreferences da APP. Esse objeto salvará a registration key do GCM. Será usado SharedPreferences para não
		 * precisar criar um banco de dados apenas para guardar essa key. Não pode ser guardada em uma constante no Settings
		 * porque cada usuário terá a sua. */
		final SharedPreferences preferences = getSharedPreferences();
		String registrationId = preferences.getString(Settings.GOOGLE_REGISTER_KEY, "");
		
		if(registrationId.length() == 0) {
			Log.i("Informações do GCM", "Nenhuma user key encontrada.");
			return null;
		}
		
		int appVersion = preferences.getInt(Settings.APP_VERSION, Integer.MIN_VALUE);
		int currenteVersion = getAppVersion(getApplicationContext());
		if(appVersion != currenteVersion) {
			Log.i("Informações do GCM", "Aplicativo atualizado, uma nova chave será gerada.");
			return null;
		}
		
		return registrationId;
	}
	
	/**
	 * Guarda no SharedPreferences da aplicação a registration key do GCM.
	 * @param context Context da aplicação
	 * @param registrationKey Registration key para ser armazenada
	 */
	private void storeRegistrationKey(Context context, String registrationKey) {
		final SharedPreferences preferences = getSharedPreferences();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(Settings.GOOGLE_REGISTER_KEY, registrationKey);
		editor.putInt(Settings.APP_VERSION, getAppVersion(getApplicationContext()));
		editor.commit();
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	private int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch(Exception ex) {
			throw new RuntimeException("Não foi possível recuperar a app version: " + ex);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private SharedPreferences getSharedPreferences() {
		return getSharedPreferences(SplashScreen.class.getSimpleName(), Context.MODE_PRIVATE);
	}	
	
	public class RegisterInGCM extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try
			{
				String registrationId;
				if(googleCloud == null) {
					googleCloud = GoogleCloudMessaging.getInstance(getApplicationContext());
				}
				
				registrationId = googleCloud.register(Settings.PROJECT_NUMBER);
				Log.i("Informações do GCM", registrationId);
				
				storeRegistrationKey(getApplicationContext(), registrationId);
				
			}catch(Exception ex) {
				Log.i("Informações do GCM", ex.toString());
			}
			return null;
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
				i = new Intent(SplashScreen.this, MainMenu.class);
				startActivity(i);
				finish();
			} else if (result == 0) {
				Toast.makeText(SplashScreen.this, "Essa universidade não tem cursos, lol", Toast.LENGTH_SHORT).show();
			} else if (result == -1) {
				Toast.makeText(SplashScreen.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(SplashScreen.this, "Você fez login em outro aparelho recentemente. Loga denovo. :)", Toast.LENGTH_SHORT).show();
				i = new Intent(SplashScreen.this, Login.class);
				startActivity(i);
				finish();
			} else if (result == -3) {
				Toast.makeText(SplashScreen.this, "Preciso de uma conexão com a internet pra logar!", Toast.LENGTH_SHORT).show();
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
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}
	
	@Override
	public void onBackPressed () {
		super.onBackPressed();
		if (r != null)
			hand.removeCallbacks(r);
	}	

}
