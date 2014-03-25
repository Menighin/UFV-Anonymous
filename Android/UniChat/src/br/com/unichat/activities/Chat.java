package br.com.unichat.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import br.com.unichat.classes.ConversationArrayAdapter;
import br.com.unichat.classes.Message;
import br.com.unichat.settings.Settings;

public class Chat extends Activity {
	
	private EditText message;
	private Button sendBtn;
	private Button imgBtn;
	private TextView talkingTo;
	private String sendToRegId;
	private Bundle extras;
	private ListView conversation;
	private ConversationArrayAdapter adapter;
	private Handler handler;
	private boolean result_ok = true;
	private boolean connected = false;
	private BroadcastReceiver messageReceiver;
	private Point p;
	private PopupWindow popup;
	private Uri imgUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		
		message = (EditText) findViewById(R.id.message);
		sendBtn = (Button) findViewById(R.id.send_btn);
		imgBtn = (Button) findViewById(R.id.img_btn);
		talkingTo = (TextView) findViewById(R.id.talking_to);
		adapter = new ConversationArrayAdapter(getApplicationContext(), R.layout.list_item_message);
		conversation = (ListView) findViewById(R.id.list_messages);
		conversation.setAdapter(adapter);
		
		handler = new Handler(new Handler.Callback() { 
			@Override
			public boolean handleMessage (android.os.Message msg) { 
				adapter.updateMessageStatus(msg.what);
				return true;
			} 
		});
		
		// Solving if the activity is either a client or server type
		extras = getIntent().getExtras();
		if (extras.getInt("type") == 0) {
			talkingTo.setText("Esperando o anônimo(a) se conectar...");
			message.setEnabled(false);
		} else {
			talkingTo.setText("Falando com: " + extras.getString("talkingTo"));
			talkingTo.setTextColor(getResources().getColor(R.color.uniChatGreen));
			sendToRegId = extras.getString("sendToRegId");
		}
		
		// Dealing with received message
		this.messageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();
				//String messageString = bundle.getString("message");
				try {
					JSONObject messageJSON = new JSONObject(bundle.getString("message"));
					
					if(messageJSON.getString("message").equals("[fechaOChatUniChat]")) {
						Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(300);
	
						talkingTo.setText("Anônimo se desconectou :( ...");
						talkingTo.setTextColor(getResources().getColor(R.color.uniChatRed));
						message.setText("");
						message.setEnabled(false);
					} else if (messageJSON.getString("message").equals("[abreOChatUniChat]")) {
						Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(300);
						
						talkingTo.setText("Falando com: " + messageJSON.getString("user"));
						talkingTo.setTextColor(getResources().getColor(R.color.uniChatGreen));
						message.setEnabled(true);
						connected = true;
						sendToRegId = messageJSON.getString("regId");
						
					} else {
						Date date = new Date();
						DateFormat format = DateFormat.getTimeInstance();
						Message message = new Message(true, messageJSON.getString("message"), format.format(date).substring(0, 5));
						adapter.add(message);
					}
				} catch (Exception e) {
					Log.e("RECEIVE MESSAGE", "INVALID JSON");
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
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(this.messageReceiver, new IntentFilter(Chat.class.getName()));
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
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
		
		int popupWidth = 300;
		int popupHeight = 200;
		
		// Changing button icon
		imgBtn.setBackgroundResource(R.drawable.img_on);

		// Inflate the popup_layout.xml
		LinearLayout viewGroup = (LinearLayout) findViewById(R.id.popup);
		LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = layoutInflater.inflate(R.layout.send_image_popup, viewGroup);
		
		// Creating the PopupWindow
		popup = new PopupWindow();
		popup.setContentView(layout);
		popup.setWidth(popupWidth);
		popup.setHeight(popupHeight);
		popup.setFocusable(true);
		 
		// Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
		int OFFSET_X = 1;
		int OFFSET_Y = 30;
		 
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
			Message msg = new Message(false, message.getText().toString(), DateFormat.getTimeInstance().format(new Date()).substring(0,5));
			adapter.add(msg);
			Integer lastMsg = adapter.getItem(msg);
			
			try {
				String urlParameters = "message=" + URLEncoder.encode(message.getText().toString(), "UTF-8")
						+ "&user=" + Settings.me.getUserID() + "&api_key=" +  URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8")
						+ "&regId=" + sendToRegId + "&conversation_id=" + Settings.CONVERSATION_ID ;
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
		Log.d("DATA", timeStamp);
		
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
		
		
		imgUri = Uri.fromFile(mediaFile);
		Log.d("FILE URI", imgUri.toString());
		
	    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
		
        startActivityForResult(cameraIntent, 2);
	}
	
	 @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         
         if (requestCode == 1) { // Request from gallery
             if (resultCode == RESULT_OK) {
                 Uri selectedImage = data.getData();
                 String oi = "oi";
                 
                 Cursor cursor = null;
                 try { 
                   String[] proj = { MediaStore.Images.Media.DATA };
                   cursor = getContentResolver().query(selectedImage,  proj, null, null, null);
                   int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                   cursor.moveToFirst();
                   oi = cursor.getString(column_index);
                   cursor.close();
                 } finally {
                   if (cursor != null) {
                     cursor.close();
                   }
                 }
                
                 Toast.makeText(this, "Selecionada:\n" +
                         oi, Toast.LENGTH_LONG).show();
                 Log.d("IMG SELECT", selectedImage.getPath());
                 Log.d("IMG SELECT", selectedImage.toString());
                 Log.d("IMG SELECT", oi);
                 
                 new SendImageAsync().execute(oi);
            
             }
         }
         else if (requestCode == 2) { // Request from camera
        	 if (resultCode == RESULT_OK) {
        		 Toast.makeText(this, "Show, fera: " + imgUri.getPath(), Toast.LENGTH_LONG).show();
        		 new SendImageAsync().execute(imgUri.getPath());
        	 }
         }
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
				Log.i("SEND MESAGE", "OK");
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
		   
			if (networkInfo != null && networkInfo.isConnected()) {
		        try {
		        	URL url = new URL("http://10.0.2.2/up_image.php");
		    	    //JSONObject json = new JSONObject(sendImageToServer (params[0], url));
		    	    String oi = sendImageToServer (params[0], url);
		    	    if (Integer.parseInt(params[1]) != -1) {
			    	    android.os.Message msg = new android.os.Message();
			    	    msg.what = Integer.parseInt(params[1]);
			    	    handler.sendMessage(msg);
		    	    }
		    	    
		    	    return 1;
		    	    //return json.getInt("response");
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
				Log.i("SEND MESAGE", "OK");
			} else if (result == -1) {
				Toast.makeText(Chat.this, "Ocorreu um erro no servidor, malz =S", Toast.LENGTH_SHORT).show();
			} else if (result == -2) {
				Toast.makeText(Chat.this, "Chave inválida para usuário. Talvez você logou em outro dispositivo?", Toast.LENGTH_SHORT).show();
			} else if (result == -3) {
				Toast.makeText(Chat.this, "Preciso de uma conexão com a internet pra logar!", Toast.LENGTH_SHORT).show();
			}
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
	
	private String sendImageToServer (String sourceFileUri, URL url) throws Exception {

       /* DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost("http://10.0.2.2/up_image.php");
        
        File file = new File (sourceFileUri);

        MultipartEntity mpEntity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        mpEntity.addPart("form_file", new FileBody(file, "image/jpeg"));

        httppost.setEntity(mpEntity);

        HttpResponse response;
        try {

            response = httpclient.execute(httppost);

            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
		
		String fileName = sourceFileUri;
		
		Log.d("FILENAME ON ASYNC", fileName);
		
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri);
        int serverResponseCode = 0;
        
        if (!sourceFile.isFile())
        	return "-4";
        else {  
               // open a URL connection to the Servlet
               FileInputStream fileInputStream = new FileInputStream(sourceFile);
                
               // Open a HTTP  connection to  the URL
               conn = (HttpURLConnection) url.openConnection(); 
               conn.setDoInput(true); // Allow Inputs
               conn.setDoOutput(true); // Allow Outputs
               conn.setUseCaches(false); // Don't use a Cached Copy
               conn.setRequestMethod("POST");
               conn.setRequestProperty("Connection", "Keep-Alive");
               conn.setRequestProperty("ENCTYPE", "multipart/form-data");
               conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
               conn.setRequestProperty("uploaded_file", fileName); 
               
               Log.d("OI", "oi");  
               
               dos = new DataOutputStream(conn.getOutputStream());
      
               dos.writeBytes(twoHyphens + boundary + lineEnd); 
               dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                                         + fileName + "\"" + lineEnd);
                
               dos.writeBytes(lineEnd);
      
               // create a buffer of  maximum size
               bytesAvailable = fileInputStream.available(); 
      
               bufferSize = Math.min(bytesAvailable, maxBufferSize);
               buffer = new byte[bufferSize];
               
               // read file and write it into form...
               bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
               
               Log.d("OI", "oi2");  
               
               while (bytesRead > 0) {
                 Log.d("BYTES", "oi");   
                 dos.write(buffer, 0, bufferSize);
                 bytesAvailable = fileInputStream.available();
                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                  
                }
      
               // send multipart form data necesssary after file data...
               dos.writeBytes(lineEnd);
               dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
      
               // Responses from the server (code and message)
               serverResponseCode = conn.getResponseCode();
               String serverResponseMessage = conn.getResponseMessage();
                 
               Log.i("uploadFile", "HTTP Response is : "
                       + serverResponseMessage + ": " + serverResponseCode);
                
               if(serverResponseCode == 200){
                    
                   runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Chat.this, "File Upload Complete.", 
                                         Toast.LENGTH_SHORT).show();
                        }
                    });                
               }    
                
               //close the streams //
               fileInputStream.close();
               dos.flush();
               dos.close();
        }
		
		return "1";
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("Tem certeza que quer sair? Vai perder ein?!");
		dialog.setPositiveButton("Quero sair!", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				voltarTela();
			}
		});
		dialog.setNegativeButton("Verdade! Vo esperar!", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		dialog.create();
		
		if(message.isEnabled()) {
			dialog.show();
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
		if(isFinishing()) {
			// Only send a finish message if the other user didn't disconect first
			if (result_ok && !connected) {
				try {
					String urlParameters = "message=[fechaOChatUniChat]" + "&user=" + Settings.me.getUserID() 
							+ "&api_key=" + URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8") + "&regId=" + sendToRegId + "&conversation_id=" + Settings.CONVERSATION_ID ;
					new SendMessageAsync().execute(urlParameters, "-1");
				} catch (Exception e) {
					Log.e("Error onDestroy", e.toString());
				}
			}
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server, menu);
		return true;
	}

}
