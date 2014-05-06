package br.com.unichat.classes;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Conversation {
	private int anonymID;
	private String anonymousAlias;
	private String date;
	private ArrayList<Message> messages;
	
	public Conversation() {
		anonymID = 0;
		anonymousAlias = "Anônimo";
		date = DateFormat.getDateInstance().format(new Date());
		messages = new ArrayList<Message>();
	}
	
	public Conversation (int id, String alias, String date, ArrayList<Message> messages) {
		this.anonymID = id;
		this.anonymousAlias = alias;
		this.date = date;
		this.messages = messages;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getAnonymID() {
		return anonymID;
	}

	public void setAnonymID(int anonymID) {
		this.anonymID = anonymID;
	}

	public String getAnonymousAlias() {
		return anonymousAlias;
	}

	public void setAnonymousAlias(String anonymousAlias) {
		this.anonymousAlias = anonymousAlias;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}
	
	public void addMessage (Message msg) {
		this.messages.add(msg);
	}
	
	public Message getLastMessage() {
		if (messages.size() > 0)
			return messages.get(messages.size() - 1);
		else
			return new Message();
	}

	@Override
	public String toString() {
		String res = "Conversation [anonymID=" + anonymID + ", anonymousAlias="
				+ anonymousAlias + ", messages=[";
		
		for (Message msg : messages) {
			res += msg.message + ", ";
		}
		res += "]";
		return res;
	}
	
}
