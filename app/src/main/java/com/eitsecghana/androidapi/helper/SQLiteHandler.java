package com.eitsecghana.androidapi.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {


   private static final String TAG = SQLiteHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "android_api";
    private static final String TABLE_USER = "users";

    //create login table column

  private static final String KEY_ID = "id";
  private static final String KEY_NAME = "name";
  private static final String KEY_EMAIL = "email";
  private static final String KEY_UID = "uid";
  private static final String KEY_CREATED_AT = "created_at";


    public SQLiteHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
     String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + " ( " + KEY_ID +
             " INT PRIMARY KEY, " + KEY_NAME + " TEXT, " + KEY_EMAIL + "UNIQUE TEXT, " + KEY_UID + " TEXT, " +
             KEY_CREATED_AT +" TEXT" + ")";
      db.execSQL(CREATE_LOGIN_TABLE);

      Log.d(TAG, "database table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     //drop table if exist
     db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

     //CREATE TABLE
     onCreate(db);

    }
    //adding user to details in database

   public void addUser(String username, String email, String uid, String created_at){
     SQLiteDatabase db = this.getWritableDatabase();
     ContentValues values = new ContentValues();
     values.put(KEY_NAME, username);
     values.put(KEY_EMAIL, email);
     values.put(KEY_UID, uid);
     values.put(KEY_CREATED_AT, created_at);

     //inserting rows
    long id = db.insert(TABLE_USER, null, values);
    db.close();

    Log.d(TAG, "new user inserted");
   }



    //getting user data from the database
    public HashMap<String, String> getUserDetails() {
     HashMap<String, String> userDetails = new HashMap<String, String>();
     String selectQuery = "SELECT * FROM " + TABLE_USER;

    SQLiteDatabase db = this.getReadableDatabase();
     Cursor cursor = db.rawQuery(selectQuery, null);

     //moving to first row
     cursor.moveToFirst();
     if (cursor.getCount() > 0){
      userDetails.put("name", cursor.getString(1));
      userDetails.put("email", cursor.getString(2));
      userDetails.put("uid", cursor.getString(3));
      userDetails.put("created_at", cursor.getString(4));
     }

     cursor.close();
     db.close();
    return userDetails;
    }

    //delete user

  public void deleteUser(){
     SQLiteDatabase db = this.getWritableDatabase();
     db.delete(TABLE_USER, null, null);
     db.close();

     Log.d(TAG, "all user is deleted");
  }

}
