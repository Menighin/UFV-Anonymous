package br.com.unichat.settings;

import java.util.ArrayList;

import br.com.unichat.classes.User;

public class Settings {
	
	//public static final String API_URL = "http://www.unichat.com.br:9001";
	public static final String API_URL = "http://107.170.81.127:9001";
	//public static final String API_URL = "http://10.0.2.2";
	public static final int SPLASH_TIME = 3000;
	public static final int CHECK_CONVERSATION_READY_TIME = 4000;
	public static final int CHECK_MESSAGES_TIME = 4000;
	public static final int MAX_STORED_CONVERSATIONS = 5;
	public static final boolean FREE_VERSION = false;
	public static ArrayList<String> COURSES = null;
	public static ArrayList<Integer> COURSES_ID = null;
	public static User me;
	public static int CONVERSATION_ID;
	public static int N_CONVERSATIONS = -1;
	public static int CURRENT_CONVERSATION_ID = -1;
	
	public static final String ID_GCM = "";
	public static final String PROJECT_NUMBER = "1054618264919";
	public static final String PROJECT_API_KEY = "AIzaSyCBoxJI3zZOees6L3dUcZiXlPNZfJ_sELA";
	public static final String GOOGLE_REGISTER_KEY = "registration_id";
	public static int APP_VERSION = 0;
}
