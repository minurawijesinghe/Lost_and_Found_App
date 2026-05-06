package com.example.lostandfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_and_found.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "adverts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_POST_TYPE = "postType";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_IMAGE_URI = "imageUri";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_POST_TYPE + " TEXT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_LOCATION + " TEXT, " +
                    COLUMN_IMAGE_URI + " TEXT, " +
                    COLUMN_TIMESTAMP + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertAdvert(String postType, String name, String phone, String description,
                            String category, String date, String location, String imageUri, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_TYPE, postType);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_IMAGE_URI, imageUri);
        values.put(COLUMN_TIMESTAMP, timestamp);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Cursor getAllAdverts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC", null);
    }

    public Cursor getAdvertsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (category.equals("All")) {
            return getAllAdverts();
        }
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + " = ? ORDER BY " + COLUMN_ID + " DESC", new String[]{category});
    }

    public Cursor getAdvertById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteAdvert(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
