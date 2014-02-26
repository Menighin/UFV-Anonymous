package br.com.unichat.classes;

public class Message {
	public boolean left;
	public String message;
	public String time;

	public Message(boolean left, String comment, String time) {
		super();
		this.left = left;
		this.message = comment;
		this.time = time;
	}
}
