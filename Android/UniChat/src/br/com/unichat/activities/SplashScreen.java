package br.com.unichat.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import br.com.unichat.classes.SaveSharedPreferences;
import br.com.unichat.settings.Settings;

/*
 * Activity que mostra a tela inicial do aplicativo. O tempo est� definido na classe Settings.java
 */

public class SplashScreen extends Activity {
	
	private Handler h;
	private Runnable r;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);
		
		h = new Handler();
		
		if (SaveSharedPreferences.getLoggedState(SplashScreen.this, getAppVersion(SplashScreen.this))) {
    		SaveSharedPreferences.retrievePreferences(SplashScreen.this);
    		
    		r = new Runnable() {
    			@Override
    			public void run() {
    				startActivity(new Intent(SplashScreen.this, MainMenu.class));
    				finish();
    			}
    		};
    	} else {
    		r = new Runnable() {
    			@Override
    			public void run() {
    				startActivity(new Intent(SplashScreen.this, Login.class));
    				finish();
    			}
    		};
    	}
		h.postDelayed(r, Settings.SPLASH_TIME);
	}
	
	private int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch(Exception ex) {
			throw new RuntimeException("Não foi possível recuperar a app version: " + ex);
		}
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
			h.removeCallbacks(r);
	}	

}
