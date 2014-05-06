package br.com.unichat.activities;

import br.com.unichat.classes.ChatArrayAdapter;
import br.com.unichat.classes.Conversation;
import br.com.unichat.classes.ConversationDAO;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SavedChat extends Activity {

	private Conversation chat;
	private ConversationDAO database;
	private EditText message;
	private Button sendBtn;
	private Button imgBtn;
	private TextView talkingTo;
	private Bundle extras;
	private ListView conversation;
	private ChatArrayAdapter adapter;
	private Point p;
	private PopupWindow popup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
	}
	
}
