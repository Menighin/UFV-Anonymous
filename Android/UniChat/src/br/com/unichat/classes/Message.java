package br.com.unichat.classes;

import android.graphics.Bitmap;

public class Message {
	public boolean left;
	public boolean image;
	public String message;
	public String time;
	public String conf;
	public String imagePath;
	public Bitmap bitImage;

	public Message(boolean left, String comment, String time) {
		super();
		this.left = left;
		this.message = comment;
		this.time = time;
		this.conf = "·";
		this.image = false;
		this.bitImage = null;
	}
	
	public Message(boolean left, String comment, String time, boolean image, Bitmap bitImage, String path) {
		super();
		this.left = left;
		this.message = comment;
		this.time = time;
		this.conf = "·";
		this.image = image;
		this.bitImage = bitImage;
		this.imagePath = path;
	}
}
