package myschoolapp.com.gsnedutech.Util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import myschoolapp.com.gsnedutech.Models.Note;


public class DatabaseHelper extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "rankrPlusDb";


    public static final String TABLE_NAME = "toDo";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_TYPE= "user_type";
    public static final String COLUMN_USER_ID= "user_id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String IS_COMPLETED = "completed";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_ID + " TEXT,"
                    + COLUMN_USER_TYPE + " TEXT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + IS_COMPLETED+ " INTEGER"
                    + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(String note,int isCompleted,String user,String uid) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_USER_TYPE, user);
        values.put(COLUMN_USER_ID, uid);
        values.put(IS_COMPLETED, isCompleted);


        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public void onTableUpdate(int id,int val){
        String selectQuery = "UPDATE "+TABLE_NAME+" SET "+IS_COMPLETED+" = "+val+" WHERE "+COLUMN_ID+" = "+id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
    }

    public List<Note> getAllNotes(String user_type) {
        List<Note> notes = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ " WHERE "+IS_COMPLETED + " = 0 AND "+ COLUMN_USER_TYPE+" = \""+user_type+"\"";


        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                note.setCompleted(cursor.getInt(cursor.getColumnIndex(IS_COMPLETED)));
                note.setUsertype(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TYPE)));
                note.setUserid(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public List<Note> getAllClosedNotes(String user_type) {
        List<Note> notes = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ " WHERE "+IS_COMPLETED + "= 1 AND "+ COLUMN_USER_TYPE+" = \""+user_type+"\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                note.setCompleted(cursor.getInt(cursor.getColumnIndex(IS_COMPLETED)));
                note.setUsertype(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TYPE)));
                note.setUserid(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}