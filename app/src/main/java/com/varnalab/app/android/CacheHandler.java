package com.varnalab.app.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hellmare on 20.07.2017 г..
 */

public class CacheHandler extends SQLiteOpenHelper {

    private String TAG = CacheHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "cacheManager";

    // Contacts table name
    private static final String TABLE_CACHE = "cache";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_CREATED = "created";

    public CacheHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CACHE + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_CONTENT + " TEXT,"
                + KEY_CREATED + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE);

        // Create tables again
        onCreate(db);
    }

    // Getting cached resource
    public String get(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Log.i(TAG, "GET: " + id);

        Cursor cursor = db.query(TABLE_CACHE,
            new String[] { KEY_CONTENT, KEY_CREATED }, KEY_ID + "=?",
            new String[] { id },
            null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // If record is older than a week
            if (cursor.getInt(1) + (60*60*24*7) < System.currentTimeMillis() / 1000) {
                Log.i(TAG, "   EXPIRED");
                return null;
            }
            Log.i(TAG, "   " + cursor.getString(0));
            return cursor.getString(0);
        }

        Log.i(TAG, "   NOT FOUND");
        return null;

    }

    // Adding new cached resource
    public void set(String id, String content) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.i(TAG, "SET: " + id + ": " + content);

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_CONTENT, content);
        values.put(KEY_CREATED, System.currentTimeMillis() / 1000);

        // Deleting Row (just in case)
        db.delete(TABLE_CACHE, KEY_ID + " = ?", new String[] { id });
        // Inserting Row
        db.insert(TABLE_CACHE, null, values);
        // Closing database connection
        db.close();
    }

    // Getting all records
    public void getAll() {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CACHE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Log.i(TAG, "ALL:");
            do {
                Log.i(TAG, "   " + cursor.getString(0) + ": " + cursor.getString(0) + " / " + cursor.getString(1));
            } while (cursor.moveToNext());
        }

    }
}