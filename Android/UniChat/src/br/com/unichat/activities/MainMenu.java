package br.com.unichat.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.unichat.classes.Conversation;
import br.com.unichat.classes.ConversationArrayAdapter;
import br.com.unichat.classes.ConversationDAO;
import br.com.unichat.classes.SaveSharedPreferences;
import br.com.unichat.settings.Settings;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainMenu extends FragmentActivity {
	
	private ConversationDAO database;
	private ConversationArrayAdapter conversationAdapter;
	private ArrayList<Conversation> cv;
	private ListView conversationList;
	private LinearLayout mContentLayout;
	private ListView mConversationsLayout;
	private RelativeLayout mMenuLayout;
	private LinearLayout paid;
	private Button whateverBtn;
	private Button femaleBtn;
	private Button maleBtn;
	private Button connectBtn;
	private Button retryConnectBtn;
	private Button tab1;
	private Button tab2;
	private Spinner courses;
	private char selectedSex = 'w';
	private boolean backActivated = true;
	private boolean checkTab = true;		// True for tab 2
	private ArrayAdapter<String> adapter;
	private Intent intent;
	private TextView logout;
	private AdView mAdView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu_content);
		
		database = new ConversationDAO(this);
		
		// Get the other XML layouts
		LayoutInflater inflater = getLayoutInflater();
		mMenuLayout = (RelativeLayout) inflater.inflate(R.layout.activity_main_menu, null);
		mConversationsLayout = (ListView) inflater.inflate(R.layout.activity_conversations, null);
		
		// Find widgets on the layouts
		
		// Content widgets
		tab1 = (Button) findViewById(R.id.tab1);
		tab2 = (Button) findViewById(R.id.tab2);
		
		// Menu widgets
		whateverBtn = (Button) mMenuLayout.findViewById(R.id.whatever_btn);
		femaleBtn = (Button) mMenuLayout.findViewById(R.id.female_btn);
		maleBtn = (Button) mMenuLayout.findViewById(R.id.male_btn);
		connectBtn = (Button) mMenuLayout.findViewById(R.id.connect_btn);
		retryConnectBtn = (Button) mMenuLayout.findViewById(R.id.retry_btn);
		courses = (Spinner) mMenuLayout.findViewById(R.id.courses_spinner);
		paid = (LinearLayout) mMenuLayout.findViewById(R.id.paid_part);
		logout = (TextView) mMenuLayout.findViewById(R.id.logoutText);
		mContentLayout = (LinearLayout) findViewById(R.id.menu_content);
		
		// Conversations widgets
		conversationAdapter = new ConversationArrayAdapter(getApplicationContext(), R.layout.list_item_conversation);
		conversationList = (ListView) mConversationsLayout.findViewById(R.id.list_conversation);
		conversationList.setAdapter(conversationAdapter);
		
		// Generating list of stored conversations
		cv = new ArrayList<Conversation>();
		cv.add(new Conversation("Minhas conversas", true, -1));
		cv.addAll(database.getAllConversations(1));
		cv.add(new Conversation("Conversas comigo", true, -1));
		cv.addAll(database.getAllConversations(0));
		
		for (Conversation c : cv) {
			conversationAdapter.add(c);
			Log.d("C", c.getAnonymousAlias());
		}
		
		// Set click item list event to open conversation
		conversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (cv.get(position).getAnonymID() != -1) {
					intent = new Intent(MainMenu.this, Chat.class);
					intent.putExtra("type", "old");
					intent.putExtra("user_id", cv.get(position).getAnonymID());
					startActivityForResult(intent, 2);
				}
			}
		});
		
		conversationList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				
				if (cv.get(position).isHeader())
					return false;
				
				AlertDialog.Builder longClickConversation = new AlertDialog.Builder(MainMenu.this);
				longClickConversation.setMessage("Adicionar anônimo antes de sair?");
				final EditText alias = new EditText(MainMenu.this);
				// EditText for anonimous alias
				alias.setHint("Apelido");
				alias.setText(cv.get(position).getAnonymousAlias());
				
				longClickConversation.setView(alias);
				
				// Buttons on dialog
				longClickConversation.setPositiveButton("Renomear", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newAlias = alias.getText().toString().length() > 0 ? alias.getText().toString() : "Anônimo";
						cv.get(position).setAnonymousAlias(newAlias);
						conversationAdapter.notifyDataSetChanged();
						database.updateUserAlias(cv.get(position).getAnonymID(), newAlias);
						Toast.makeText(MainMenu.this, "Usuário renomeado", Toast.LENGTH_LONG).show();
					}
				});
				longClickConversation.setNeutralButton("Deletar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog confirmExclusion = 
								new AlertDialog.Builder(MainMenu.this)
								.setMessage("Certeza?")
								.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if (cv.get(position).isMine()) Settings.N_CONVERSATIONS--;
										database.deleteConversation(cv.get(position));
										cv.remove(position);
										conversationAdapter.remove(position);
										conversationAdapter.notifyDataSetChanged();
										Toast.makeText(MainMenu.this, "Usuário removido", Toast.LENGTH_LONG).show();
										
										//TODO: SEND REMOVE MESSAGE TO OTHER USER
									}
								})
								.setNegativeButton("Não", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// Do nothing
									}
								}).create();
						confirmExclusion.show();
						
					}
				});
				longClickConversation.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});
				
				longClickConversation.create().show();
				return true;
			}
			
		});
		
		
		// Inflate the first tab
		if(cv.size() > 2) {
			mContentLayout.addView(mConversationsLayout);
			tab1.setBackgroundResource(R.drawable.tab_button_on);
			tab2.setBackgroundResource(R.drawable.tab_button_off);
			checkTab = false;
		}
		else
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
		
		// Setting the number of stored conversations on DB
		Settings.N_CONVERSATIONS = database.countConversations();

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
	
	// On tab click
	public void changeTab (View v) {
		mContentLayout.removeAllViews();
		if (!checkTab) {
			mContentLayout.addView(mConversationsLayout);
			tab1.setBackgroundResource(R.drawable.tab_button_on);
			tab2.setBackgroundResource(R.drawable.tab_button_off);
			checkTab = true;
			
			// Updating conversations
			updateContactsList();
			
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
	
	// Retry connect button click
	public void retryConnection (View v) {
		new GetCoursesAsync().execute();
	}
	
	
	//AsyncTask to manage the connection with the API
	private class ConnectAsync extends AsyncTask<Void, Void, Integer> {
		
		@Override
		protected Integer doInBackground (Void... params) {
			
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		   
			if (networkInfo != null && networkInfo.isConnected()) {
		        try {
		        	String already_added = "";
		        	
		        	if (cv.size() > 0) {
		        		already_added = cv.get(0).getAnonymID() + "";
		        		for (int i = 1; i < cv.size(); i++)
		        			already_added += "," + cv.get(i).getAnonymID();
		        	}
		        	
		        	String urlParameters = "user=" + Settings.me.getUserID() +
							"&wantssex=" + selectedSex + "&wantscourse=" + Settings.COURSES_ID.get(courses.getSelectedItemPosition()) + 
							"&api_key=" + Settings.me.getAPIKey() + "&already_added=" + already_added;
					URL url = new URL(Settings.API_URL + "/connect2.php");
					
					String res = POSTConnection(urlParameters, url);
				    JSONObject json = new JSONObject(res);
				    
				    //If a user was found...
				    if (json.getInt("response") == 1) {
					    // Creating next intent and putting username if it is the case
					    String talkingTo = "Anônimo";
					    intent = new Intent(MainMenu.this, Chat.class);
					    if (json.getInt("special") == 1)
					    	talkingTo = json.getString("username");
					    intent.putExtra("talkingTo", talkingTo);
					    intent.putExtra("user_id", json.getInt("user_id"));
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
			if (result == 1) {
				intent.putExtra("type", "new");
				startActivityForResult(intent, 1);
			} else if (result == 0) {
				Toast.makeText(MainMenu.this, "Nenhum usuário foi encontrado com os critérios escolhidos", Toast.LENGTH_LONG).show();
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
				connectBtn.setVisibility(View.VISIBLE);
				retryConnectBtn.setVisibility(View.GONE);
			} else if (result == 0) {
				Toast.makeText(MainMenu.this, "Essa universidade não tem cursos, lol", Toast.LENGTH_SHORT).show();
			} else if (result == -1) {
				Toast.makeText(MainMenu.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(MainMenu.this, "Chave inválida para usuário. Esse usuário fez login em outro aparelho.", Toast.LENGTH_LONG).show();
			} else if (result == -3) {
				Toast.makeText(MainMenu.this, "Para conversar é necessário uma conexão com internet", Toast.LENGTH_SHORT).show();
				connectBtn.setVisibility(View.GONE);
				retryConnectBtn.setVisibility(View.VISIBLE);
			}
		}
	}
	
	// Result from Chat Activity
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_CANCELED) {    
				logout();
			}
		} else if (requestCode == 2 && resultCode == RESULT_OK) {
			updateContactsList();
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
	
	public void updateContactsList () {
		conversationList.setAdapter(null);
		conversationAdapter.notifyDataSetChanged();
		conversationAdapter = new ConversationArrayAdapter(getApplicationContext(), R.layout.list_item_conversation);
		conversationList.setAdapter(conversationAdapter);
		cv = new ArrayList<Conversation>();
		cv.add(new Conversation("Minhas conversas", true, -1));
		cv.addAll(database.getAllConversations(1));
		cv.add(new Conversation("Conversas comigo", true, -1));
		cv.addAll(database.getAllConversations(0));
		
		for (Conversation c : cv) {
			conversationAdapter.add(c);
		}
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