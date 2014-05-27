package br.com.unichat.classes;

import android.graphics.Bitmap;

public class Message {
	public int id;
	public boolean left;
	public boolean image;
	public String message;
	public String time;
	public String conf;
	public String imagePath;
	public Bitmap bitImage;
	public boolean read;
	public boolean wasDownloaded;

	public Message () {
		id = -1;
		left = false;
		image = false;
		message = "";
		time = "";
		conf = "·";
		imagePath = "";
		bitImage = null;
		read = false;
		wasDownloaded = true;
	}
	
	public Message (int id, String message, String time, boolean left, String imagePath, boolean image, Bitmap bitImage, String conf, boolean read, boolean wasDownloaded) {
		this.id = id;
		this.message = message;
		this.time = time;
		this.left = left;
		this.imagePath = imagePath;
		this.image = image;
		this.bitImage = bitImage;
		this.conf = conf;
		this.read = read;
		this.wasDownloaded = wasDownloaded;
	}
	
	public static class Builder {
		private int nestedId;
		private boolean nestedLeft;
		private boolean nestedImage;
		private String nestedMessage;
		private String nestedTime;
		private String nestedConf;
		private String nestedImagePath;
		private Bitmap nestedBitImage;
		private boolean nestedRead;
		private boolean nestedWasDownloaded;
		
		public Builder () {
			this.nestedId = -1;
			this.nestedLeft = false;
			this.nestedImage = false;
			this.nestedMessage = "";
			this.nestedTime = "";
			this.nestedConf = "·";
			this.nestedImagePath = "";
			this.nestedBitImage = null;
			this.nestedRead = true;
			this.nestedWasDownloaded = false;
		}
		
		public Builder id(int id) {
			this.nestedId = id;
			return this;
		}
		
		public Builder message(String message) {
			this.nestedMessage = message;
			return this;
		}
		
		public Builder time (String time) {
			this.nestedTime = time;
			return this;
		}
		
		public Builder left (boolean left) {
			this.nestedLeft = left;
			return this;
		}
		
		public Builder imagePath (String imagePath) {
			this.nestedImagePath = imagePath;
			return this;
		}
		
		public Builder image (boolean image) {
			this.nestedImage = image;
			return this;
		}
		
		public Builder bitImage (Bitmap image) {
			this.nestedBitImage = image;
			return this;
		}
		
		public Builder conf (String conf) {
			this.nestedConf = conf;
			return this;
		}
		
		public Builder read (boolean read) {
			this.nestedRead = read;
			return this;
		}
		
		public Builder wasDownloaded (boolean wasDownloaded) {
			this.nestedWasDownloaded = wasDownloaded;
			return this;
		}
		
		public Message createMessage() {
			return new Message(
					this.nestedId,
					this.nestedMessage, this.nestedTime, 
					this.nestedLeft, this.nestedImagePath, 
					this.nestedImage, this.nestedBitImage, 
					this.nestedConf, this.nestedRead, 
					this.nestedWasDownloaded);
		}
		
	}
}
