package tech.szymanskazdrzalik.self_diagnosis.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SampleSQLiteDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "user_profiles";
    public static final String USER_PROFILE_TABLE_NAME = "users";
    public static final String USER_COLUMN_ID = "user_id";
    public static final String USER_COLUMN_GENDER = "gender";
    public static final String USER_COLUMN_BIRTH_DATE = "birth_date";
    public static final String USER_COLUMN_NAME = "user_name";
    private static final int DATABASE_VERSION = 1;

    /**
     * {@inheritDoc}
     */
    public SampleSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_PROFILE_TABLE_NAME + " (" +
                USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_COLUMN_NAME + " TEXT," +
                USER_COLUMN_BIRTH_DATE + " DATE," +
                USER_COLUMN_GENDER + " TEXT " + ")");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_PROFILE_TABLE_NAME);
        onCreate(db);
    }
}
