package br.com.unichat.classes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
	private TextView headerView;
	private TextView dateView;
	private ArrayList<Conversation> conversations = new ArrayList<Conversation>();
	private Conversation conversation;
	
	public ConversationArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row;
		conversation = getItem(position);
		
		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (!conversation.isHeader())
			row = inflater.inflate(R.layout.list_item_conversation, parent, false);
		else
			row = inflater.inflate(R.layout.list_item_conversation_separator, parent, false);
		
		if (!conversation.isHeader()) {
			aliasView = (TextView) row.findViewById(R.id.conversation_list_alias);
			aliasView.setText(conversation.getAnonymousAlias());
			
			messageView = (TextView) row.findViewById(R.id.conversation_list_last);
			Message lastMessage = conversation.getLastMessage();
			if (lastMessage.message.length() > 20)
				messageView.setText(lastMessage.message.subSequence(0, 20) + "...");
			else
				messageView.setText(lastMessage.message);
			if (lastMessage.image)
				messageView.setText("Imagem");
			if (!lastMessage.read) {
				messageView.setTypeface(null, Typeface.BOLD);
				messageView.setTextColor(Color.parseColor("#555555"));
			}
			imageView = (ImageView) row.findViewById(R.id.conversation_list_img);
			switch (conversation.getImgId()) {
				case 0: imageView.setImageResource(R.drawable.conversation0); break;
				case 1: imageView.setImageResource(R.drawable.conversation1); break;
				case 2: imageView.setImageResource(R.drawable.conversation2); break;
				case 3: imageView.setImageResource(R.drawable.conversation3); break;
				case 4: imageView.setImageResource(R.drawable.conversation4); break;
				case 5: imageView.setImageResource(R.drawable.conversation5); break;
				case 6: imageView.setImageResource(R.drawable.conversation6); break;
				case 7: imageView.setImageResource(R.drawable.conversation7); break;
			}
			
			dateView = (TextView) row.findViewById(R.id.conversation_list_date);
			dateView.setText(conversation.getDate().substring(0, conversation.getDate().length() - 3));
		} else {
			headerView = (TextView) row.findViewById(R.id.conversation_separator);
			headerView.setText(conversation.getAnonymousAlias());
		}
	
		return row;
	}
	
	public Conversation getItem(int index) {
		return this.conversations.get(index);
	}
	
	@Override
	public void add(Conversation c) {
		conversations.add(c);
		super.add(c);
	}
	
	public void remove(int index) {
		conversations.remove(index);
	}
	
	@Override
    public int getCount(){
        return conversations != null ? conversations.size() : 0;
    }
	
}
