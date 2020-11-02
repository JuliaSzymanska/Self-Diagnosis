package tech.szymanskazdrzalik.self_diagnosis.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static void saveUserDataToDB(Context context, String name, @NonNull Date birthDate, String gender) {
        SQLiteDatabase database = new SampleSQLiteDBHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_NAME, name);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(birthDate);
        contentValues.put(USER_COLUMN_BIRTH_DATE, date);
        contentValues.put(USER_COLUMN_GENDER, gender);
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
                USER_COLUMN_GENDER + " TEXT check(" + USER_COLUMN_GENDER + " = 'f' or " + USER_COLUMN_GENDER + ")" + ")");

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
