package br.com.unichat.classes;

import br.com.unichat.settings.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SaveSharedPreferences {
	
	static final String PREF_IS_LOGGED = "isLoged";
	static final String PREF_USER_NAME= "username";
	static final String PREF_USER_ID = "userID";
	static final String PREF_USER_COURSE_ID = "courseID";
	static final String PREF_USER_UNIVERSITY = "university";
	static final String PREF_USER_SEX = "userSex";
	static final String PREF_USER_API_KEY ="APIKey";
	
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void createPreferences(Context ctx, boolean logged, String userName, int userID, int courseID, int university, String sex, String APIKey) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_IS_LOGGED, logged);
        editor.putString(PREF_USER_NAME, userName);
        editor.putInt(PREF_USER_ID, userID);
        editor.putInt(PREF_USER_COURSE_ID, courseID);
        editor.putInt(PREF_USER_UNIVERSITY, university);
        editor.putString(PREF_USER_SEX, sex);
        editor.putString(PREF_USER_API_KEY, APIKey);
        editor.commit();
    }
    
    public static void destroyPreferences (Context ctx) {
    	Editor editor = getSharedPreferences(ctx).edit();
    	editor.clear();
    	editor.commit();
    }

    public static boolean getLoggedState(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_IS_LOGGED, false);
    }
    
    public static void setUser(Context ctx) {
    	
    	Settings.me = new User (
    			getSharedPreferences(ctx).getInt(PREF_USER_ID, 0),
    			getSharedPreferences(ctx).getString(PREF_USER_NAME, ""),
    			getSharedPreferences(ctx).getInt(PREF_USER_COURSE_ID, 0),
    			getSharedPreferences(ctx).getString(PREF_USER_SEX, ""),
    			getSharedPreferences(ctx).getInt(PREF_USER_UNIVERSITY, 0),
    			getSharedPreferences(ctx).getString(PREF_USER_API_KEY, "")
    			);
    }
    
}
