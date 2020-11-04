package tech.szymanskazdrzalik.self_diagnosis.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SampleSQLiteDBHelper extends SQLiteOpenHelper {
    public static final DateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String DATABASE_NAME = "user_profiles";
    public static final String USER_PROFILE_TABLE_NAME = "users";
    public static final String USER_COLUMN_ID = "user_id";
    public static final String USER_COLUMN_GENDER = "gender";
    public static final String USER_COLUMN_BIRTH_DATE = "birth_date";
    public static final String USER_COLUMN_NAME = "user_name";
    public static final String USER_COLUMN_PICTURE = "user_picture";
    private static final int DATABASE_VERSION = 2;

    /**
     * {@inheritDoc}
     */
    public SampleSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    public static void saveUserDataToDB(Context context, User user) {
        SQLiteDatabase database = new SampleSQLiteDBHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_NAME, user.getName());
        String date = DB_DATE_FORMAT.format(user.getBirthDate());
        contentValues.put(USER_COLUMN_BIRTH_DATE, date);
        contentValues.put(USER_COLUMN_GENDER, user.getGender());
        contentValues.put(USER_COLUMN_PICTURE, DbBitmapUtility.getBytes(user.getPicture()));
        database.insert(USER_PROFILE_TABLE_NAME, null, contentValues);
    }

    public static Cursor getAllUsersFromDB(Context context) {

        SQLiteDatabase database = new SampleSQLiteDBHelper(context).getReadableDatabase();
        String[] projection = {
                USER_COLUMN_ID,
                USER_COLUMN_NAME,
                USER_COLUMN_BIRTH_DATE,
                USER_COLUMN_GENDER,
                USER_COLUMN_PICTURE
        };

        return database.query(
                SampleSQLiteDBHelper.USER_PROFILE_TABLE_NAME,      // The table to query
                projection,                                        // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                 // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                      // don't filter by row groups
                null                                      // don't sort
        );
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
                USER_COLUMN_PICTURE + " BLOB," +
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
