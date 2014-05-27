package br.com.unichat.classes;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ConversationDAO extends SQLiteOpenHelper {
	
	// Database data
	public static final String DATABASE_NAME = "UniChatDB";
	
	public static final String TABLE_CONVERSATIONS = "Conversations";
    public static final String COLUMN_CONVERSATIONSID = "anonymous_id";
    public static final String COLUMN_ALIAS = "alias";
    public static final String COLUMN_REGID = "reg_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_IS_MINE = "is_mine";
    public static final String COLUMN_CONVERSATION_IMG = "conversation_img";
    
    public static final String TABLE_MESSAGES = "Messages";
    public static final String COLUMN_MESSAGESID = "id";
    public static final String COLUMN_USERID = "anonymous_id";
    public static final String COLUMN_MESSAGE = "message_text";
    public static final String COLUMN_TIME = "message_time";
    public static final String COLUMN_READ = "message_read";
    public static final String COLUMN_LEFT = "message_left";
    public static final String COLUMN_IS_IMAGE = "message_is_image";
    public static final String COLUMN_IMAGE_PATH = "message_image_path";
    public static final String COLUMN_WAS_DOWNLOADED = "message_was_downloaded";
    
    public static final String CREATE_CONVERSATIONS = "CREATE TABLE " + TABLE_CONVERSATIONS + " ( " 
    		+ COLUMN_CONVERSATIONSID + " INTEGER PRIMARY KEY, "
    		+ COLUMN_ALIAS + " TEXT, "
    		+ COLUMN_DATE + " TEXT, " 
    		+ COLUMN_IS_MINE + " INTEGER, "
    		+ COLUMN_CONVERSATION_IMG + " INTEGER)";
	
    public static final String CREATE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " ( " 
    		+ COLUMN_MESSAGESID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ COLUMN_USERID + " INTEGER, "
    		+ COLUMN_MESSAGE + " TEXT, " 
    		+ COLUMN_TIME + " TEXT, "
    		+ COLUMN_READ + " INTEGER, "
    		+ COLUMN_LEFT + " INTEGER, "
    		+ COLUMN_IS_IMAGE + " INTEGER, "
    		+ COLUMN_IMAGE_PATH + " TEXT, " 
    		+ COLUMN_WAS_DOWNLOADED + " INTEGER)";
    
    public ConversationDAO (Context context) {
    	super (context, DATABASE_NAME, null, 1);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create books table
    	Log.e("CREATE1", CREATE_CONVERSATIONS);
        db.execSQL(CREATE_CONVERSATIONS);
        db.execSQL(CREATE_MESSAGES);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
 
        // create fresh books table
        this.onCreate(db);
    }
    
    public void addConversation(Conversation conversation) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		// Adding conversation on DB
		ContentValues values = new ContentValues();
		values.put(COLUMN_CONVERSATIONSID, conversation.getAnonymID());
		values.put(COLUMN_ALIAS, conversation.getAnonymousAlias()); 
		values.put(COLUMN_DATE, conversation.getDate());
		values.put(COLUMN_IS_MINE, conversation.isMine() ? 1 : 0);
		values.put(COLUMN_CONVERSATION_IMG, conversation.getImgId());
		
		db.insert(TABLE_CONVERSATIONS, null, values);
		
		// Adding messages of the conversation
		for (Message msg : conversation.getMessages()) {
			values = new ContentValues();
			values.put(COLUMN_USERID, conversation.getAnonymID());
			values.put(COLUMN_MESSAGE, msg.message);
			values.put(COLUMN_TIME, msg.time);
			values.put(COLUMN_LEFT, msg.left ? 1 : 0);
			values.put(COLUMN_READ, 1);
			values.put(COLUMN_IS_IMAGE, msg.image ? 1 : 0);
			values.put(COLUMN_IMAGE_PATH, msg.imagePath);
			values.put(COLUMN_WAS_DOWNLOADED, msg.wasDownloaded ? 1 : 0);
			
			db.insert(TABLE_MESSAGES, null, values);
		}
		
		db.close(); 
		
		Log.d("addConversation", conversation.getAnonymousAlias());
	}
    
    public int updateMessagesRead(int conversationId) {
    	 
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(COLUMN_READ, 1);
     
        int i = db.update(TABLE_MESSAGES, values, COLUMN_USERID + " = ?",
                new String[] { String.valueOf(conversationId) });
     
        db.close();
     
        return i;
    }
    
    public void updateImageDownloaded (int messageId, String imgPath) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(COLUMN_WAS_DOWNLOADED, 1);
    	values.put(COLUMN_IMAGE_PATH, imgPath);
    	
    	db.update(TABLE_MESSAGES, values, COLUMN_MESSAGESID + " = ?", new String[] { String.valueOf(messageId) });
    }
    
    public void updateUserAlias (int conversationId, String alias) {
    	SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALIAS, alias);
     
        db.update(TABLE_CONVERSATIONS, values, COLUMN_CONVERSATIONSID + " = ?",
                new String[] { String.valueOf(conversationId) });

        db.close();
    }
    
    public void deleteConversation(Conversation conversation) {
    	 
        SQLiteDatabase db = this.getWritableDatabase();
 
        db.delete(TABLE_CONVERSATIONS,
                COLUMN_CONVERSATIONSID + " = ?",
                new String[] { String.valueOf(conversation.getAnonymID()) });
        
        db.delete(TABLE_MESSAGES, //table name
                COLUMN_USERID + " = ?",  // selections
                new String[] { String.valueOf(conversation.getAnonymID()) }); //selections args
 
        db.close();
 
        //log
        Log.d("deleteConversation", conversation.getAnonymousAlias());
 
    }
    
    //Get conversation
    public Conversation getConversation (int id) {
    	Conversation c = null;
    	Message m = null;
    	String query = "SELECT * FROM " + TABLE_CONVERSATIONS + " WHERE " + COLUMN_CONVERSATIONSID + " = " + id;
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	
    	if (cursor.moveToFirst()) {
    		c = new Conversation();
    		c.setAnonymID(Integer.parseInt(cursor.getString(0)));
    		c.setAnonymousAlias(cursor.getString(1));
    		c.setDate(cursor.getString(2));
    		c.isMine(Integer.parseInt(cursor.getString(3)) == 1 ? true : false);
    		c.setImgId(cursor.getInt(4));
    		
    		// Get the messages
    		String queryM = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_USERID + " = " + cursor.getString(0);
    		Cursor cursorM = db.rawQuery(queryM, null);
    		
    		if (cursorM.moveToFirst()) {
    			do {
    				m = new Message();
    				m.id = cursorM.getInt(0);
    				m.message = cursorM.getString(2);
            		m.time = cursorM.getString(3);
            		m.conf = "✓";
            		m.read = Integer.parseInt(cursorM.getString(4)) == 1 ? true : false;
            		m.left = Integer.parseInt(cursorM.getString(5)) == 1 ? true : false;
            		m.image = Integer.parseInt(cursorM.getString(6)) == 1 ? true : false;
            		m.imagePath = m.image ? cursorM.getString(7) : "";
            		m.wasDownloaded = Integer.parseInt(cursorM.getString(8)) == 1 ? true : false;
            		c.addMessage(m);
    			} while(cursorM.moveToNext());
    		}
    	}
    	db.close();
    	return c;
    }
    
    // Get All Conversations
    public ArrayList<Conversation> getAllConversations() {
    	return getAllConversations("SELECT * FROM " + TABLE_CONVERSATIONS);
    }
    
    public ArrayList<Conversation> getAllConversations(int isMine) {
    	return getAllConversations("SELECT * FROM " + TABLE_CONVERSATIONS + " WHERE " + COLUMN_IS_MINE + " = " + isMine);
    }
    
    public ArrayList<Conversation> getAllConversations(String query) {
        ArrayList<Conversation> conversations = new ArrayList<Conversation>();
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        Conversation conversation = null;
        Message message = null;
        if (cursor.moveToFirst()) {
            do {
                conversation = new Conversation();
                conversation.setAnonymID(Integer.parseInt(cursor.getString(0)));
                conversation.setAnonymousAlias(cursor.getString(1));
                conversation.setDate(cursor.getString(2));
        		conversation.isMine(Integer.parseInt(cursor.getString(3)) == 1 ? true : false);
        		conversation.setImgId(cursor.getInt(4));
        		
                // Get messages of the conversations
                String queryM = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_USERID + " = " + cursor.getString(0);
                Cursor cursorM = db.rawQuery(queryM, null);
                
                if (cursorM.moveToFirst()) {
                	do {
                		message = new Message();
                		message.id = cursorM.getInt(0);
                		message.message = cursorM.getString(2);
                		message.time = cursorM.getString(3);
                		message.conf = "✓";
                		message.read = Integer.parseInt(cursorM.getString(4)) == 1 ? true : false;
                		message.left = Integer.parseInt(cursorM.getString(5)) == 1 ? true : false;
                		message.image = Integer.parseInt(cursorM.getString(6)) == 1 ? true : false;
                		message.imagePath = message.image ? cursorM.getString(7) : "";
                		message.wasDownloaded = Integer.parseInt(cursorM.getString(8)) == 1 ? true : false;
                		
                		conversation.addMessage(message);
                		
                	} while (cursorM.moveToNext());
                }
 
                conversations.add(conversation);
                
                Log.d("getAllConversations(i)", conversation.toString());
            } while (cursor.moveToNext());
        }

        Log.d("getAllConversations()", "hehe");
        db.close();
        return conversations;
    }
    
    public int countConversations() {
    	String query = "SELECT COUNT(*) FROM " + TABLE_CONVERSATIONS;
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	
    	if (cursor.moveToFirst()) {
    		return Integer.parseInt(cursor.getString(0));
    	}
    	
    	db.close();
    	
    	return -1;
    }
    
    public void clearDatabase() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.close();
    }
    
    public void updateMessages (int user, ArrayList<Message> msgs, String date) {
    	SQLiteDatabase db = this.getWritableDatabase();
        
    	ContentValues values;
    	
    	// Adding messages of the conversation
		for (Message msg : msgs) {
			values = new ContentValues();
			values.put(COLUMN_USERID, user);
			values.put(COLUMN_MESSAGE, msg.message);
			values.put(COLUMN_TIME, msg.time);
			values.put(COLUMN_LEFT, msg.left ? 1 : 0);
			values.put(COLUMN_READ, msg.read ? 1 : 0);
			values.put(COLUMN_IS_IMAGE, msg.image ? 1 : 0);
			values.put(COLUMN_IMAGE_PATH, msg.imagePath);
			values.put(COLUMN_WAS_DOWNLOADED, msg.wasDownloaded ? 1 : 0);
			
			db.insert(TABLE_MESSAGES, null, values);
		}
		
		db.close(); 
    }
    
    public int addMessage (int user, Message msg) {
    	SQLiteDatabase db = this.getWritableDatabase();
     
    	ContentValues values;

		values = new ContentValues();
		values.put(COLUMN_USERID, user);
		values.put(COLUMN_MESSAGE, msg.message);
		values.put(COLUMN_TIME, msg.time);
		values.put(COLUMN_LEFT, msg.left ? 1 : 0);
		values.put(COLUMN_READ, msg.read ? 1 : 0);
		values.put(COLUMN_IS_IMAGE, msg.image ? 1 : 0);
		values.put(COLUMN_IMAGE_PATH, msg.imagePath);
		values.put(COLUMN_WAS_DOWNLOADED, msg.wasDownloaded ? 1 : 0);
		
		long id = db.insert(TABLE_MESSAGES, null, values);
		
		db.close();
		
		return (int)id;
    }
}
