package example.com.bus2.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "BLE";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PARTICIPANTS = "participants";
    private static final String KEY_ID = "id";
    private static final String TIME = "time";
    private static final String NAME = "name";
    private static final String ADDRESSES = "addresses";

    private static final String CREATE_TABLE_PARTI = "CREATE TABLE "
            + TABLE_PARTICIPANTS + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + TIME + " TEXT,"+
            NAME + " TEXT,"+ADDRESSES + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("table", CREATE_TABLE_PARTI);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PARTI);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_PARTICIPANTS + "'");
        onCreate(db);
    }

    public void addParticipant(String t, String n, String a) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Creating content values
        ContentValues values = new ContentValues();
        values.put(TIME, t);
        values.put(NAME, n);
        values.put(ADDRESSES, a);
        // insert row in students table
        db.insert(TABLE_PARTICIPANTS, null, values);
    }
    public ArrayList<String> getAllParticipants() {
        ArrayList<String> participants = new ArrayList<String>();
        String name="";
        String time="";
        String addresses="";
        String selectQuery = "SELECT  * FROM " + TABLE_PARTICIPANTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                time = c.getString(c.getColumnIndex(TIME));
                name = c.getString(c.getColumnIndex(NAME));
                addresses = c.getString(c.getColumnIndex(ADDRESSES));
                String data = name+","+time+","+addresses;
                // adding to Students list
                participants.add(data);
            } while (c.moveToNext());
            Log.d("array", participants.toString());
        }
        return participants;
    }
}