package br.com.unichat.activities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import br.com.unichat.classes.Base64;

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
	private Bitmap bitmap;
	
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
     			String[] filePathColumn = { MediaStore.Images.Media.DATA };

     			Cursor cursor = getContentResolver().query(selectedImage,
     					filePathColumn, null, null, null);
     			cursor.moveToFirst();

     			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
     			String picturePath = cursor.getString(columnIndex);
     			cursor.close();

     			decodeFile(picturePath);        
             }
         }
         else if (requestCode == 2) { // Request from camera
        	 if (resultCode == RESULT_OK) {
        		 decodeFile(imgUri.getPath());
        	 }
         }
     }
	
	 public void decodeFile(String filePath) {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 1024;

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
			bitmap = BitmapFactory.decodeFile(filePath, o2);
						
			Message msg = new Message(false, message.getText().toString(), DateFormat.getTimeInstance().format(new Date()).substring(0,5), true, bitmap, filePath);
			adapter.add(msg);
			Integer lastMsg = adapter.getItem(msg);
			
			// Upload to server
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
	private class SendImageAsync extends AsyncTask<String, Void, Integer> implements OnDismissListener {
		private ProgressDialog dialog = new ProgressDialog(Chat.this);
		private boolean cancelled = false;
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Enviando imagem...");
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(true);
			dialog.setOnDismissListener(this);
			dialog.show();
		}
		
		public void onDismiss(DialogInterface dialog) {
			cancelled = true;
			Toast.makeText(Chat.this, "Envio cancelado", Toast.LENGTH_LONG).show();
			this.cancel(true);
		}
		
		protected void onCancelled() {
			cancel(true);
		}
		
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
					
					String urlParameters = "message=[uniImg]" + json.getString("imgName") + "&user=" + Settings.me.getUserID() 
							+ "&api_key=" + URLEncoder.encode(Settings.me.getAPIKey(), "UTF-8") + "&regId=" + sendToRegId + "&conversation_id=" + Settings.CONVERSATION_ID ;
					if (!cancelled)
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
			dialog.dismiss();
			popup.dismiss();
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
