package br.com.unichat.classes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.unichat.activities.R;

public class ChatArrayAdapter extends ArrayAdapter<Message> {
	
	private TextView messageView;
	private TextView timeView;
	private TextView confView;
	private ImageView imageView;
	private LinearLayout timeConfView;
	private List<Message> messages = new ArrayList<Message>();
	private WindowManager wm;
	private Display display;
	private RelativeLayout.LayoutParams params;
	private Context context;
	private Message message;

	
	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		
		this.context = context;
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.list_item_message, parent, false);
		}

		message = getItem(position);

		messageView = (TextView) row.findViewById(R.id.message);
		messageView.setText(message.message);
		messageView.setBackgroundResource(message.left ? R.drawable.anonymous : R.drawable.me);
		
		timeView = (TextView) row.findViewById(R.id.time);
		timeView.setText(message.time);
		
		confView = (TextView) row.findViewById(R.id.confirmation);
		confView.setText(message.conf);
		
		timeConfView = (LinearLayout) row.findViewById(R.id.timeConfWrapper);
		
		imageView = (ImageView) row.findViewById(R.id.imageUploaded);
		imageView.setOnTouchListener(null);
		imageView.setBackgroundResource(message.left ? R.drawable.anonymous : R.drawable.me);
		
		messageView.setMaxWidth(display.getWidth() - (30*display.getWidth()/100));
		imageView.setMaxWidth(display.getWidth() - (30*display.getWidth()/100));
		imageView.setMaxHeight(display.getHeight() - (70*display.getHeight()/100));
		
		if (message.left) {
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			messageView.setLayoutParams(params);
			messageView.setTextColor(Color.BLACK);
			imageView.setLayoutParams(params);
			
			if (!message.image) {
				messageView.setPadding(
						(int)(15 * context.getResources().getDisplayMetrics().density),
						(int)(5 * context.getResources().getDisplayMetrics().density), 
						(int)(10 * context.getResources().getDisplayMetrics().density), 
						(int)(6 * context.getResources().getDisplayMetrics().density));
				messageView.requestFocus();
				params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.RIGHT_OF, R.id.message);
				params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				timeConfView.setLayoutParams(params);
				timeConfView.setPadding((int)(10 * context.getResources().getDisplayMetrics().density), 0, 0, 0);
				
				messageView.setVisibility(TextView.VISIBLE);
				imageView.setVisibility(ImageView.GONE);
			} else {
				
				imageView.setPadding(
						(int)(10 * context.getResources().getDisplayMetrics().density),
						(int)(5 * context.getResources().getDisplayMetrics().density), 
						(int)(5 * context.getResources().getDisplayMetrics().density), 
						(int)(5 * context.getResources().getDisplayMetrics().density));
				imageView.requestFocus();
				
				messageView.setVisibility(TextView.GONE);
				imageView.setVisibility(ImageView.VISIBLE);
				
				params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.RIGHT_OF, R.id.imageUploaded);
				params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				timeConfView.setLayoutParams(params);
				timeConfView.setPadding((int)(10 * context.getResources().getDisplayMetrics().density), 0, 0, 0);
				
				//imageView.setImageBitmap(message.bitImage);
				imageView.setImageBitmap(new BitmapFactory().decodeFile(message.imagePath));
				imageView.setTag(message);
				
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Message messageForThisRow = (Message)v.getTag();
						Uri imgUri = Uri.parse("file://" + messageForThisRow.imagePath);
						Intent intent = new Intent(); 
						intent.setAction(android.content.Intent.ACTION_VIEW);
						intent.setData(imgUri);
						intent.setDataAndType(imgUri, "image/*");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				});
			}
			
			confView.setVisibility(TextView.INVISIBLE);
			
		} else {
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			messageView.setLayoutParams(params);
			messageView.setTextColor(Color.WHITE);
			imageView.setLayoutParams(params);
			
			if (!message.image) {
				messageView.setPadding(
						(int)(10 * context.getResources().getDisplayMetrics().density),
						(int)(5 * context.getResources().getDisplayMetrics().density), 
						(int)(15 * context.getResources().getDisplayMetrics().density), 
						(int)(6 * context.getResources().getDisplayMetrics().density));
				messageView.requestFocus();
				params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.LEFT_OF, R.id.message);
				params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				timeConfView.setLayoutParams(params);
				timeConfView.setPadding(0, 0, (int)(10 * context.getResources().getDisplayMetrics().density), 0);
				
				messageView.setVisibility(TextView.VISIBLE);
				imageView.setVisibility(ImageView.GONE);
				
			} else {
				
				imageView.setPadding(
						(int)(5 * context.getResources().getDisplayMetrics().density),
						(int)(5 * context.getResources().getDisplayMetrics().density), 
						(int)(10 * context.getResources().getDisplayMetrics().density), 
						(int)(5 * context.getResources().getDisplayMetrics().density));
				imageView.requestFocus();
				
				messageView.setVisibility(TextView.GONE);
				imageView.setVisibility(ImageView.VISIBLE);
				
				params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.LEFT_OF, R.id.imageUploaded);
				params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				timeConfView.setLayoutParams(params);
				timeConfView.setPadding(0, 0, (int)(10 * context.getResources().getDisplayMetrics().density), 0);
				
				//imageView.setImageBitmap(message.bitImage);
				imageView.setImageBitmap(new BitmapFactory().decodeFile(message.imagePath));
				// Adding the eventListener
				imageView.setTag(message);
				
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Message messageForThisRow = (Message)v.getTag();
						Uri imgUri = Uri.parse("file://" + messageForThisRow.imagePath);
						Intent intent = new Intent(); 
						intent.setAction(android.content.Intent.ACTION_VIEW);
						intent.setData(imgUri);
						intent.setDataAndType(imgUri, "image/*");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				});
			}
			confView.setVisibility(TextView.VISIBLE);
		}
		
		return row;
	}
	
	public Message getItem(int index) {
		return this.messages.get(index);
	}
	
	public int getItem (Message m) {
		return this.messages.indexOf(m);
	}
	
	public void updateMessageStatus (int i) {
		messages.get(i).conf = "✓";
		Message msg = messages.get(i);
		messages.remove(msg);
		super.remove(msg);
		messages.add(i, msg);
		super.add(msg);
	}
	
	@Override
	public void add(Message object) {
		messages.add(object);
		super.add(object);
	}
}
