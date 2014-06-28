package br.com.unichat.activities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import br.com.unichat.classes.Base64;
import br.com.unichat.classes.Conversation;
import br.com.unichat.classes.ChatArrayAdapter;
import br.com.unichat.classes.ConversationDAO;
import br.com.unichat.classes.Message;
import br.com.unichat.settings.Settings;

public class Chat extends Activity {
	
	private Conversation chat;
	private ConversationDAO database;
	private EditText message;
	private Button sendBtn;
	private Button imgBtn;
	private TextView talkingTo;
	private Bundle extras;
	private ListView conversation;
	private ChatArrayAdapter adapter;
	private Handler handler;
	private BroadcastReceiver messageReceiver;
	private Point p;
	private PopupWindow popup;
	private Uri imgUri;
	private Bitmap bitmap;
	private EditText alias;
	private AlertDialog confirmQuit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		
		chat = new Conversation();
		database = new ConversationDAO(this);
		
		message = (EditText) findViewById(R.id.message);
		sendBtn = (Button) findViewById(R.id.send_btn);
		imgBtn = (Button) findViewById(R.id.img_btn);
		talkingTo = (TextView) findViewById(R.id.talking_to);
		adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.list_item_message);
		conversation = (ListView) findViewById(R.id.list_messages);
		conversation.setAdapter(adapter);
		
		handler = new Handler(new Handler.Callback() { 
			@Override
			public boolean handleMessage (android.os.Message msg) { 
				adapter.updateMessageStatus(msg.what);
				return true;
			} 
		});
		
		imgBtn.setEnabled(true);
		
		// Getting info from intent
		extras = getIntent().getExtras();
		Settings.CURRENT_CONVERSATION_ID = extras.getInt("user_id");
		if (extras.getString("type").equals("new")) { // If this chat was created by a new conversation request
			talkingTo.setText("Falando com: " + extras.getString("talkingTo"));
			chat.setAnonymID(extras.getInt("user_id"));
			chat.setAnonymousAlias(extras.getString("talkingTo"));
			chat.setDate(DateFormat.getDateTimeInstance().format(new Date()));
			chat.setClosed(false);
		} else { // If this is a chat already saved on local database
			chat = database.getConversation(extras.getInt("user_id"));
			talkingTo.setText("Falando com: " + chat.getAnonymousAlias());
			
			for (Message m : chat.getMessages()) {
				adapter.add(m);
			}
			
			conversation.setSelection(chat.getLastReadMessage());
			
			database.updateMessagesRead(extras.getInt("user_id"));
			
			if (chat.isClosed()) {
				message.setEnabled(false);
				switch(new Random().nextInt(3)) {
					case 0: message.setHint(chat.getAnonymousAlias() + " deletou você =/"); break;
					case 1: message.setHint(chat.getAnonymousAlias() + " não te quer mais =/"); break;
					case 2: message.setHint(chat.getAnonymousAlias() + " te excluiu =/"); break;
				}
			}
		}
		
		// If not connected, dont let'em talk
		ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if (networkInfo == null || !networkInfo.isConnected()) {
			message.setEnabled(false);
			switch(new Random().nextInt(3)) {
				case 0: message.setHint("Preciso de internet pra isso!"); break;
				case 1: message.setHint("Liga o 3G!"); break;
				case 2: message.setHint("Liga a Wifi!"); break;
			}
		}
		
		// Dealing with received message
		this.messageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();
				try {
					Date date = new Date();
					DateFormat format = DateFormat.getTimeInstance();
					JSONObject messageJSON = new JSONObject(bundle.getString("message"));
					if (messageJSON.getInt("user_from") == chat.getAnonymID()) {
						if (messageJSON.getString("message").length() > 12 && messageJSON.getString("message").substring(0, 12).equals("[UniChatImg]")) {
							Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
							v.vibrate(300);
							
							Message message = new Message.Builder()
								.left(true)
								.message(messageJSON.getString("message").substring(12))
								.time(format.format(date).substring(0, 5))
								.image(true)
								.wasDownloaded(false)
								.imagePath("")
								.createMessage();
							
							if (extras.get("type").equals("old")) message.id = database.addMessage(chat.getAnonymID(), message); //newMessages.add(message);
							adapter.add(message);
							chat.addMessage(message);
							
						} else if (messageJSON.getString("message").equals("[uniChatFechaSsaPorra]")) {
							database.updateConversationClosed(chat.getAnonymID());
							message.setEnabled(false);
							message.setHint(chat.getAnonymousAlias() + " deletou você =/");
						} else {
							Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
							v.vibrate(300);
							
							Message message = new Message.Builder()
								.left(true)
								.message(messageJSON.getString("message"))
								.time(format.format(date).substring(0, 5))
								.createMessage();
							
							if (extras.get("type").equals("old")) message.id = database.addMessage(chat.getAnonymID(), message); //newMessages.add(message);
							adapter.add(message);
							chat.addMessage(message);
						}
					}
				} catch (Exception e) {
					Log.e("RECEIVE MESSAGE", "INVALID JSON:" + bundle.getString("message"));
				}
				
				// Focus on last received message
				if (conversation.getCount() > 1)
					conversation.setSelection(conversation.getCount() - 1);

			}
		};		
		
		// Changing the send button icon whenever the user starts typing
		message.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (message.getText().length() > 0)
					sendBtn.setBackgroundResource(R.drawable.send_btn);
				else
					sendBtn.setBackgroundResource(R.drawable.send_off);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		// Set click item list event for images
		conversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {			
				if (adapter.getItem(position).image && adapter.getItem(position).left && !adapter.getItem(position).wasDownloaded) {
					final Message msg = (Message)arg1.findViewById(R.id.imageUploaded).getTag();
					try {
						AlertDialog.Builder confirmDownload = new AlertDialog.Builder(Chat.this);
						confirmDownload.setMessage("Baixar imagem?");
						
						// Buttons on dialog
						confirmDownload.setPositiveButton("Baixar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(Chat.this, "Baixando a imagem...", Toast.LENGTH_LONG).show();
								new GetImageAsync().execute(msg.message, position + "", msg.id + "");
							}
						});
						confirmDownload.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// Do nothing
							}
						});
						confirmDownload.create().show();
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("GETASYNCIMAGE", e.toString());
					}
					
				}
			}
		});
		
		// Creating confirm exit dialog box
		// EditText for anonymous alias
		alias = new EditText(this);
		alias.setHint("Apelido");
		alias.setText("");
		confirmQuit = new AlertDialog.Builder(this)
					.setMessage("Adicionar anônimo antes de sair?")
					.setView(alias)
					.setPositiveButton("Salvar e sair", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (database.countMyConversations() < Settings.MAX_STORED_CONVERSATIONS) {
								if (alias.getText().toString().length() > 0) {
									chat.setAnonymousAlias(alias.getText().toString());
								}
								chat.setImgId(new Random().nextInt(8));
								database.addConversation(chat);
								Settings.N_CONVERSATIONS++;
								voltarTela();
							} else {
								Toast.makeText(Chat.this, "Lista cheia! Exclua um anônimo para poder adicionar outro!", Toast.LENGTH_LONG).show();
							}
							confirmQuit.dismiss();
						}
					})
					.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							confirmQuit.dismiss();
						}
					})
					.create();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(this.messageReceiver, new IntentFilter(Chat.class.getName()));
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if (popup != null)
			popup.dismiss();
	}
	
	// Function called when user hit the send button on screen
	public void showPopUp (View v) {
		// Getting location of the popup window
		int [] location = new int[2];
		imgBtn.getLocationOnScreen(location);
		p = new Point();
 	    p.x = location[0];
	    p.y = location[1];
		
		int popupWidth = (int) (150 * getResources().getDisplayMetrics().density);
		int popupHeight = (int)(100 * getResources().getDisplayMetrics().density);
		
		// Changing button icon
		imgBtn.setBackgroundResource(R.drawable.img_on);

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) Chat.this.findViewById(R.id.popup);
		LayoutInflater layoutInflater = (LayoutInflater)Chat.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = layoutInflater.inflate(R.layout.send_image_popup, viewGroup);

		// Creating the PopupWindow
		popup = new PopupWindow(Chat.this);
		popup.setContentView(layout);
		popup.setWidth(popupWidth);
		popup.setHeight(popupHeight);
		popup.setFocusable(true);
		
		// Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
		int OFFSET_X = (int)(1 * getResources().getDisplayMetrics().density);
		int OFFSET_Y = (int)(15 * getResources().getDisplayMetrics().density);
		 
		popup.setBackgroundDrawable(new BitmapDrawable());
		
		// Displaying the popup at the specified location, + offsets
		popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y - OFFSET_Y - popupHeight);
		
		// Changing photo ico back to gray
		popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				imgBtn.setBackgroundResource(R.drawable.img_off);
			}
		});
		
	}
	
	public void sendMessage (View v) {
		if (message.getText().length() > 0) {
			Message msg = new Message.Builder()
				.left(false)
				.message(message.getText().toString())
				.time(DateFormat.getTimeInstance().format(new Date()).substring(0,5))
				.createMessage();
			
			adapter.add(msg);
			chat.addMessage(msg);
			
			if (extras.getString("type").equals("old")) msg.id = database.addMessage(chat.getAnonymID(), msg); //newMessages.add(msg); // Add if new message in old conversation for update later
			
			Integer lastMsg = adapter.getItem(msg);
			
			try {
				String urlParameters = "message=" + URLEncoder.encode(message.getText().toString(), "UTF-8")
						+ "&user=" + Settings.me.getUserID() + "&api_key=" +  URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8") + "&user_to=" + chat.getAnonymID();
				
				message.getText().clear();
	    		new SendMessageAsync().execute(urlParameters, lastMsg.toString());
			} catch (Exception e) {
				Log.e ("SEND BUTTON", e.toString());
			}
			
			conversation.setSelection(conversation.getCount() - 1);
		}
	}
	
	// Function called when user click the image button
	public void sendImageFromGallery (View v) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
	}
	
	// Function called when user click the image button
	public void sendImageFromCamera (View v) {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		// Creating file to save
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "UniChat Imagens");
		
		// Create UniChat directory if it doesn't exists
		if (!mediaStorageDir.exists()){
	        if (!mediaStorageDir.mkdirs()){
	            Log.e("IMAGE FROM CAMERA", "failed to create directory");
	        }
	    }
		
		// Create file itself
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
		
		imgUri = Uri.fromFile(mediaFile);
		
	    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
		
        startActivityForResult(cameraIntent, 2);
	}
	
	 @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         
         if (requestCode == 1) { // Request from gallery
             if (resultCode == RESULT_OK) {
            	Uri selectedImage = data.getData();
     			String[] filePathColumn = { MediaStore.Images.Media.DATA };

     			Cursor cursor = getContentResolver().query(selectedImage,
     					filePathColumn, null, null, null);
     			cursor.moveToFirst();

     			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
     			String picturePath = cursor.getString(columnIndex);
     			cursor.close();

     			decodeFile(picturePath); 
     			
             }
         } else if (requestCode == 2) { // Request from camera
        	 if (resultCode == RESULT_OK) {
        		 decodeFile(imgUri.getPath());
        		 // Update gallery
      			 sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imgUri.getPath()))));
        	 }
         }
     }
	
	 public void decodeFile(String filePath) {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, o);
			
			// The new size we want to scale to
			final int REQUIRED_SIZE = 207;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(filePath, o2);
			
			Message msg = new Message.Builder()
				.left(false)
				.message(message.getText().toString())
				.time(DateFormat.getTimeInstance().format(new Date()).substring(0,5))
				.image(true)
				.bitImage(bitmap)
				.imagePath(filePath).createMessage();
			
			adapter.add(msg);
			chat.addMessage(msg);
			conversation.setSelection(conversation.getCount() - 1);
			if (extras.get("type").equals("old"))  msg.id = database.addMessage(chat.getAnonymID(), msg);
			Integer lastMsg = adapter.getItem(msg);
		
			// Upload to server
			popup.dismiss();
			Toast.makeText(Chat.this, "Enviando sua imagem...", Toast.LENGTH_LONG).show();
			new SendImageAsync().execute(lastMsg.toString());
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
				//Log.i("SEND MESAGE", "OK");
			} else if (result == -1) {
				Toast.makeText(Chat.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(Chat.this, "Chave inválida para usuário. Talvez você logou em outro dispositivo?", Toast.LENGTH_SHORT).show();
			} else if (result == -3) {
				Toast.makeText(Chat.this, "Preciso de uma conexão com a internet pra logar!", Toast.LENGTH_SHORT).show();
			}
		}
	}	
	
	// AsyncTask to send the Image to the server
	private class SendImageAsync extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground (String... params) {
			ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			
			String webAddressToPost = Settings.API_URL + "/up_image.php";
			
			if (networkInfo != null && networkInfo.isConnected()) {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpContext localContext = new BasicHttpContext();
					HttpPost httpPost = new HttpPost(webAddressToPost);

					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bitmap.compress(CompressFormat.JPEG, 100, bos);
					byte[] data = bos.toByteArray();
					String file = Base64.encodeBytes(data);
					entity.addPart("file", new StringBody(file));
					entity.addPart("user", new StringBody(Settings.me.getUserID() + ""));
					entity.addPart("api_key", new StringBody(URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8")));

					httpPost.setEntity(entity);
					HttpResponse response = httpClient.execute(httpPost, localContext);
					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
					
					String sResponse = reader.readLine();
					
					JSONObject json = new JSONObject(sResponse);
					
					String urlParameters = "message=[UniChatImg]" + URLEncoder.encode(json.getString("imgName"), "UTF-8")
							+ "&user=" + Settings.me.getUserID() + "&api_key=" + URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8") + "&user_to=" + chat.getAnonymID();
					
					new SendMessageAsync().execute(urlParameters, params[0]);
					
					return json.getInt("response");
					
				} catch (Exception e) {
					Log.e("Erro em SendImageAsync", e.toString());
					return -2;
				}
		    } else {
		    	return -3;
		    }
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				Toast.makeText(Chat.this, "Imagem enviada com sucesso", Toast.LENGTH_LONG).show();
			} else if (result == -1) {
				Toast.makeText(Chat.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_LONG).show();
			} else if (result == -2) {
				Toast.makeText(Chat.this, "Chave inválida para usuário. Talvez você logou em outro dispositivo?", Toast.LENGTH_LONG).show();
			} else if (result == -3) {
				Toast.makeText(Chat.this, "Preciso de uma conexão com a internet pra logar!", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public class GetImageAsync extends AsyncTask<String, Void, Integer> {
		private String filename;
		private int position;
		private int messageId;
		private Bitmap bitmapDownloaded;
		
		@Override
		protected Integer doInBackground(String... params) {
			
			filename = params[0];
			position = Integer.parseInt(params[1]);
			messageId = Integer.parseInt(params[2]);
			
			InputStream in = null;
	        int response = -1;
	        
	        try {
		        URL url = new URL(Settings.API_URL + "/images/" + filename + ".jpg");
		        URLConnection conn = url.openConnection();
	                   
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            httpConn.setAllowUserInteraction(false);
	            httpConn.setInstanceFollowRedirects(true);
	            httpConn.setRequestMethod("GET");
	            httpConn.connect();
	  
	            response = httpConn.getResponseCode();                
	            if (response == HttpURLConnection.HTTP_OK) {
	                in = httpConn.getInputStream();
	                bitmapDownloaded = BitmapFactory.decodeStream(in);
	                in.close();
	            }
	        }
	        catch (Exception e) {
	            Log.e("GetImageAsync", "deu ruim");
	            e.printStackTrace();
	        }
	        
	        return 1;
			
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			
			// Saving picture on SD Card
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
		              Environment.DIRECTORY_PICTURES), "UniChat Imagens");
			
			// Create UniChat directory if it doesn't exists
			if (!mediaStorageDir.exists()){
		        if (!mediaStorageDir.mkdirs()){
		            Log.e("IMAGE DOWNLOADED", "failed to create directory");
		        }
		    }
			
			String fname = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
			File file = new File (mediaStorageDir, fname);
			if (file.exists ()) file.delete (); 
			try {
			       FileOutputStream out = new FileOutputStream(file);
			       bitmapDownloaded.compress(Bitmap.CompressFormat.JPEG, 90, out);
			       out.flush();
			       out.close();
			       // Update gallery
	      		   sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

			} catch (Exception e) {
				Log.e("GetImageAsync", "Error saving downloaded image");
				e.printStackTrace();
			}
			adapter.updateImageDownloaded(position, file.getAbsolutePath());
			adapter.notifyDataSetChanged();
			Log.e("MESSAGE ID", messageId + "");
			database.updateImageDownloaded(messageId, file.getAbsolutePath());
		}
		
	}
	
	// Function to make the POST request to the server and send a message
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
		if (extras.getString("type").equals("new")) {
			confirmQuit.show();
		} else {
			voltarTela();
		}
	}
	
	private void voltarTela() {
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);     
		finish();
	}
	
	@Override
	public void onDestroy() {
		Settings.CURRENT_CONVERSATION_ID = -1;
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server, menu);
		return true;
	}

}
