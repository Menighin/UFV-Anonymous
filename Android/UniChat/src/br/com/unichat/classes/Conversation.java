package br.com.unichat.classes;

import java.util.ArrayList;

public class Conversation {
	private int anonymID;
	private String anonymousAlias;
	private ArrayList<Message> messages;
	
	public Conversation() {
		anonymID = 0;
		anonymousAlias = "Anônimo";
		messages = new ArrayList<Message>();
	}
	
	public Conversation (int id, String alias, ArrayList<Message> messages) {
		this.anonymID = id;
		this.anonymousAlias = alias;
		this.messages = messages;
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
