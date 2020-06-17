package ants.mobile.ants_insight.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InsightDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "ants.db";
    private static final String TABLE_NAME = "events";

    public InsightDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + "events (_id INTEGER PRIMARY KEY AUTOINCREMENT, event_data TEXT, time TEXT, event_type int)");
    }

    public void insertData(String eventData, Long time, int eventType) {
        ContentValues e1 = new ContentValues();
        e1.put("event_data", eventData);
        e1.put("time", time);
        e1.put("event_type", eventType);
        this.getWritableDatabase().insert(TABLE_NAME, (String) null, e1);
    }

    public boolean deleteRow(Long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int kq = db.delete(TABLE_NAME, "time = ?", new String[]{String.valueOf(time)});
        return kq > 0;
    }

    public void deleteAll() {
        this.getWritableDatabase().delete("events", null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        onCreate(db);
    }

}
