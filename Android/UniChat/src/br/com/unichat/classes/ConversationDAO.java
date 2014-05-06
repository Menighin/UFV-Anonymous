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
    
    public static final String TABLE_MESSAGES = "Messages";
    public static final String COLUMN_MESSAGESID = "id";
    public static final String COLUMN_USERID = "anonymous_id";
    public static final String COLUMN_MESSAGE = "message_text";
    public static final String COLUMN_TIME = "message_time";
    public static final String COLUMN_READ = "message_read";
    public static final String COLUMN_LEFT = "message_left";
    
    public static final String CREATE_CONVERSATIONS = "CREATE TABLE " + TABLE_CONVERSATIONS + " ( " 
    		+ COLUMN_CONVERSATIONSID + " INTEGER PRIMARY KEY, "
    		+ COLUMN_ALIAS + " TEXT, "
    		+ COLUMN_DATE + " TEXT)";
	
    public static final String CREATE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " ( " 
    		+ COLUMN_MESSAGESID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ COLUMN_USERID + " INTEGER, "
    		+ COLUMN_MESSAGE + " TEXT, " 
    		+ COLUMN_TIME + " TEXT, "
    		+ COLUMN_READ + " INTEGER, "
    		+ COLUMN_LEFT + " INTEGER)";
    
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
		
		db.insert(TABLE_CONVERSATIONS, null, values);
		
		// Adding messages of the conversation
		for (Message msg : conversation.getMessages()) {
			values = new ContentValues();
			values.put(COLUMN_USERID, conversation.getAnonymID());
			values.put(COLUMN_MESSAGE, msg.message);
			values.put(COLUMN_TIME, msg.time);
			values.put(COLUMN_LEFT, msg.left ? 1 : 0);
			values.put(COLUMN_READ, 1);
			
			db.insert(TABLE_MESSAGES, null, values);
		}
		
		db.close(); 
		
		Log.d("addConversation", conversation.getAnonymousAlias());
	}
    
    public void addMessages (ArrayList<Message> messages, int anonymID) {
    	SQLiteDatabase db = this.getWritableDatabase();
    
    	ContentValues values;
    	
    	// Adding messages of the conversation
		for (Message msg : messages) {
			values = new ContentValues();
			values.put(COLUMN_USERID, anonymID);
			values.put(COLUMN_MESSAGE, msg.message);
			values.put(COLUMN_TIME, msg.time);
			values.put(COLUMN_LEFT, msg.left ? 1 : 0);
			values.put(COLUMN_READ, msg.read ? 1 : 0);
			
			db.insert(TABLE_MESSAGES, null, values);
		}
		
		db.close(); 
    }
    
    public int updateMessagesRead(Conversation conversation) {
    	 
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(COLUMN_READ, 1);
     
        int i = db.update(TABLE_MESSAGES, values, COLUMN_USERID + " = ?",
                new String[] { String.valueOf(conversation.getAnonymID()) });
     
        db.close();
     
        return i;
     
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
    
    // Get All Conversations
    public ArrayList<Conversation> getAllConversations() {
        ArrayList<Conversation> conversations = new ArrayList<Conversation>();
 
        String query = "SELECT * FROM " + TABLE_CONVERSATIONS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        Conversation conversation = null;
        Message message = null;
        if (cursor.moveToFirst()) {
            do {
                conversation = new Conversation();
                conversation.setAnonymID(Integer.parseInt(cursor.getString(0)));
                conversation.setAnonymousAlias(cursor.getString(1));
                
                // Get messages of the conversations
                String queryM = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_USERID + " = " + cursor.getString(0);
                Cursor cursorM = db.rawQuery(queryM, null);
                
                if (cursorM.moveToFirst()) {
                	do {
                		message = new Message();
                		message.message = cursorM.getString(2);
                		message.time = cursorM.getString(3);
                		message.conf = "✓";
                		message.read = Integer.parseInt(cursorM.getString(4)) == 1 ? true : false;
                		message.left = Integer.parseInt(cursorM.getString(5)) == 1 ? true : false;
                		
                		conversation.addMessage(message);
                		
                	} while (cursorM.moveToNext());
                }
 
                conversations.add(conversation);
                
                Log.d("getAllConversations(i)", conversation.toString());
            } while (cursor.moveToNext());
        }

        Log.d("getAllConversations()", "hehe");
 
        // return books
        return conversations;
    }
    
    public int countConversations() {
    	String query = "SELECT COUNT(*) FROM " + TABLE_CONVERSATIONS;
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	
    	if (cursor.moveToFirst()) {
    		return Integer.parseInt(cursor.getString(0));
    	}
    	
    	return -1;
    }
    
    public void clearDatabase() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
    }
    
}
