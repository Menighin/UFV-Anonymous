package br.com.unichat.classes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.unichat.activities.R;

public class ConversationArrayAdapter extends ArrayAdapter<Message> {
	
	private TextView messageView;
	private TextView timeView;
	private List<Message> messages = new ArrayList<Message>();
	private WindowManager wm;
	private Display display;
	private RelativeLayout.LayoutParams params;
	private Context context;

	
	public ConversationArrayAdapter(Context context, int textViewResourceId) {
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

		Message message = getItem(position);

		messageView = (TextView) row.findViewById(R.id.message);
		messageView.setText(message.message);
		messageView.setBackgroundResource(message.left ? R.drawable.anonymous : R.drawable.me);
		
		timeView = (TextView) row.findViewById(R.id.time);
		timeView.setText(message.time);
		
		messageView.setMaxWidth(display.getWidth() - (30*display.getWidth()/100));
		
		if (message.left) {
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			messageView.setLayoutParams(params);
			messageView.setPadding(
					(int)(15 * context.getResources().getDisplayMetrics().density),
					(int)(5 * context.getResources().getDisplayMetrics().density), 
					(int)(10 * context.getResources().getDisplayMetrics().density), 
					(int)(6 * context.getResources().getDisplayMetrics().density));
			messageView.requestFocus();
			
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.message);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			timeView.setLayoutParams(params);
			timeView.setPadding((int)(10 * context.getResources().getDisplayMetrics().density), 0, 0, 0);
			
		} else {
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			messageView.setLayoutParams(params);
			messageView.setPadding(
					(int)(10 * context.getResources().getDisplayMetrics().density),
					(int)(5 * context.getResources().getDisplayMetrics().density), 
					(int)(15 * context.getResources().getDisplayMetrics().density), 
					(int)(6 * context.getResources().getDisplayMetrics().density));
			messageView.requestFocus();
			
			params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.LEFT_OF, R.id.message);
			params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			timeView.setLayoutParams(params);
			timeView.setPadding(0, 0, (int)(10 * context.getResources().getDisplayMetrics().density), 0);
			
			
		}
		
		return row;
	}
	
	public Message getItem(int index) {
		return this.messages.get(index);
	}
	
	public int getItem (Message m) {
		return this.messages.indexOf(m);
	}
	
	public void updateTime (int i, String time) {
		messages.get(i).time = time;
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
