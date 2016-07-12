package edu.scu.vbhadana.photonotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBAdapter {

    public static final String ID_COLUMN = "_id";
    public static final String CAPTION_COLUMN = "CAPTION";
    public static final String FILE_PATH_COLUMN = "IMAGE_PATH";
    public static final String LONGITUDE_COLUMN = "LONGITUDE";
    public static final String LATITUDE_COLUMN = "LATITUDE";
    public static final String ADDRESS_COLUMN = "ADDRESS";
    public static final String AUDIO_PATH_COLUMN = "AUDIO_PATH";


    public static final String DATABASE_TABLE = "PhotoAlbum";
    private SQLiteDatabase db;
    public Context context;
    private SQLiteOpenHelper photoNotesDatabaseHelper;
    private Observer observer;
    private static DBAdapter dbAdapterStaticInstance;

    public static DBAdapter getInstance(Context context) {
        synchronized (DBAdapter.class) {
            if(dbAdapterStaticInstance == null) {
                dbAdapterStaticInstance = new DBAdapter(context);
            }
            return dbAdapterStaticInstance;
        }
    }

    private DBAdapter(Context context) {
            this.context = context;
            photoNotesDatabaseHelper = new PhotoNotesDatabaseHelper(context);
    }

    public interface Observer {
        void onInserted();
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    public DBAdapter open() {
        db = photoNotesDatabaseHelper.getWritableDatabase();
        return this;
    }

    // Return all rows in the database.
    public Cursor getAllRows() {
        Cursor cursor = db.query(true, "PhotoAlbum", new String[]{"_id", "CAPTION", "IMAGE_PATH"},
                null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Get a specific row
    public Cursor getRow(long rowId) throws SQLException {
        Cursor cursor = db.query(true, "PhotoAlbum", new String[]{"_id", "CAPTION", "IMAGE_PATH", "LONGITUDE", "LATITUDE", "ADDRESS", "AUDIO_PATH"},
                ID_COLUMN + "=" + rowId, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    // Insert a row in the table
    public long insertRow(String caption, String path, Double longitude, Double latitude, String address, String audioPath) {
        ContentValues intialValues = new ContentValues();
        intialValues.put(CAPTION_COLUMN, caption);
        intialValues.put(FILE_PATH_COLUMN, path);
        intialValues.put(LONGITUDE_COLUMN, longitude);
        intialValues.put(LATITUDE_COLUMN, latitude);
        intialValues.put(ADDRESS_COLUMN, address);
        intialValues.put(AUDIO_PATH_COLUMN, audioPath);

        long r = db.insert(DATABASE_TABLE, null, intialValues);
        if(observer != null) {
            observer.onInserted();
        }
        return r;

    }

    public void deleteRow(String caption, String path) {
        db.delete(DATABASE_TABLE, String.format(
                        "%1$s=? and %2$s=?", CAPTION_COLUMN, FILE_PATH_COLUMN),
                new String[]{caption, path});
    }
}
