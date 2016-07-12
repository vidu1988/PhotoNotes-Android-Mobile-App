package edu.scu.vbhadana.photonotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhotoNotesDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "PhotoAlbum";
    public static final int DB_VERSION = 1;

    public PhotoNotesDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PHOTOALBUM (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CAPTION TEXT, "
                + "IMAGE_PATH TEXT, "
                + "LONGITUDE REAL, "
                + "LATITUDE REAL, "
                + "ADDRESS TEXT, "
                + "AUDIO_PATH TEXT); ");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

}
