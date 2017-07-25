package com.varnalab.app.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class CacheHandler extends SQLiteOpenHelper {

    private final String TAG = CacheHandler.class.getSimpleName();

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

    CacheHandler(Context context) {
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
    String get(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String result = null;

        Log.d(TAG, "GET: " + id);

        Cursor cursor = db.query(TABLE_CACHE,
            new String[] { KEY_CONTENT, KEY_CREATED }, KEY_ID + "=?",
            new String[] { id },
            null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getInt(1) < System.currentTimeMillis() / 1000) {
                Log.d(TAG, "   EXPIRED");
            } else {
                result = cursor.getString(0);
                Log.d(TAG, "   " + result);
            }
        } else {
            Log.d(TAG, "   NOT FOUND");
        }

        if (cursor != null) {
            cursor.close();
        }

        // Closing database connection
        db.close();

        return result;
    }

    void set(String id, String content) {
        // default = 1 week
        int expireAfter = 7 * 24 * 60 * 60;
        this.set(id, content, expireAfter);
    }

    // Adding new cached resource
    void set(String id, String content, int expireAfter) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d(TAG, "SET: " + id + ": " + content);

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_CONTENT, content);
        values.put(KEY_CREATED, (System.currentTimeMillis() / 1000) + expireAfter);

        // Deleting Row (just in case)
        db.delete(TABLE_CACHE, KEY_ID + " = ?", new String[] { id });

        // Inserting Row
        db.insert(TABLE_CACHE, null, values);

        // Closing database connection
        db.close();
    }

    void remove(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d(TAG, "DEL: " + id);

        // Deleting Row (just in case)
        db.delete(TABLE_CACHE, KEY_ID + " = ?", new String[] { id });

        // Closing database connection
        db.close();
    }

    // Getting all records
    void getAll() {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CACHE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Log.d(TAG, "ALL:");
            do {
                Log.d(TAG, "   " + cursor.getString(0) + ": " + cursor.getString(0) + " / " + cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // Closing cursor
        cursor.close();

        // Closing database connection
        db.close();
    }

}