package br.com.unichat.classes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.unichat.activities.R;

public class ConversationArrayAdapter extends ArrayAdapter<Conversation> {
	
	private ImageView imageView;
	private TextView aliasView;
	private TextView messageView;
	private TextView dateView;
	private ArrayList<Conversation> conversations = new ArrayList<Conversation>();
	private Conversation conversation;
	
	public ConversationArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.list_item_conversation, parent, false);
		}
		
		conversation = getItem(position);
		Log.d("POSITION", position +"");
		
		aliasView = (TextView) row.findViewById(R.id.conversation_list_alias);
		aliasView.setText(conversation.getAnonymousAlias());
		
		messageView = (TextView) row.findViewById(R.id.conversation_list_last);
		messageView.setText(conversation.getLastMessage().message + "...");
		
		dateView = (TextView) row.findViewById(R.id.conversation_list_date);
		dateView.setText(conversation.getDate());
	
		return row;
	}
	
	public Conversation getItem(int index) {
		return this.conversations.get(index);
	}
	
	@Override
	public void add(Conversation c) {
		Log.d("Adding", c.getAnonymousAlias());
		conversations.add(c);
		super.add(c);
	}
	
	@Override
    public int getCount(){
        return conversations != null ? conversations.size() : 0;
    }
	
}
