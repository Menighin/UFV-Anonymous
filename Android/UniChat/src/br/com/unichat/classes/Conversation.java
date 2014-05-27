package br.com.unichat.classes;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Conversation {
	private int anonymID;
	private String anonymousAlias;
	private String date;
	private boolean isMine;
	private boolean isHeader;
	private ArrayList<Message> messages;
	private int imgId;
	
	public Conversation() {
		anonymID = 0;
		anonymousAlias = "Anônimo";
		isMine = true;
		isHeader = false;
		date = DateFormat.getDateInstance().format(new Date());
		messages = new ArrayList<Message>();
		imgId = 1;
	}
	
	public Conversation (int id, String alias, String date, boolean isMine, ArrayList<Message> messages, int imgId) {
		this.anonymID = id;
		this.anonymousAlias = alias;
		this.date = date;
		this.isMine = isMine;
		this.messages = messages;
		this.isHeader = false;
		this.imgId = imgId;
	}
	
	public Conversation (String alias, boolean isHeader, int id) {
		this.anonymousAlias = alias;
		this.isHeader = isHeader;
		this.anonymID = id;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public boolean isMine() {
		return isMine;
	}
	
	public void isMine(boolean bool) {
		isMine = bool;
	}
	
	public boolean isHeader() {
		return isHeader;
	}
	
	public void isHeader(boolean bool) {
		this.isHeader = bool;
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
	
	public Message getMessage(int pos) {
		return messages.get(pos);
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
	
	public int getLastReadMessage() {
		int i = 0;
		for (i = 0; i < messages.size(); i++) {
			if (!messages.get(i).read) {
				return i;
			}
		}
		return i - 1;
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
