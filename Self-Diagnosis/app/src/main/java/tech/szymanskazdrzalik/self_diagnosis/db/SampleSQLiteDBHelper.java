package tech.szymanskazdrzalik.self_diagnosis.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SampleSQLiteDBHelper extends SQLiteOpenHelper {
    public static final DateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String DATABASE_NAME = "user_profiles";
    public static final String USER_PROFILE_TABLE_NAME = "users";
    public static final String USER_COLUMN_ID = "user_id";
    public static final String USER_COLUMN_GENDER = "gender";
    public static final String USER_COLUMN_BIRTH_DATE = "birth_date";
    public static final String USER_COLUMN_NAME = "user_name";
    public static final String USER_COLUMN_PICTURE = "user_picture";
    private static final int DATABASE_VERSION = 4;

    /**
     * {@inheritDoc}
     */
    public SampleSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    public static void saveUserDataToDB(Context context, User user) {
        // TODO: 05.11.2020 make not break with null date
        // TODO: 05.11.2020 sprawdzic
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

    // TODO: 05.11.2020 TEST ME
    public static User getUserByID(Context context, int id) {
        // TODO: 05.11.2020 make it not break when id not exists

        SQLiteDatabase database = new SampleSQLiteDBHelper(context).getReadableDatabase();
        String[] projection = {
                USER_COLUMN_ID,
                USER_COLUMN_NAME,
                USER_COLUMN_BIRTH_DATE,
                USER_COLUMN_GENDER,
                USER_COLUMN_PICTURE
        };

        String selection = SampleSQLiteDBHelper.USER_COLUMN_ID + " like ?";

        String[] selectionArgs = {"%" + id + "%"};

        Cursor cursor = database.query(
                SampleSQLiteDBHelper.USER_PROFILE_TABLE_NAME,      // The table to query
                projection,                                        // The columns to return
                selection,                                     // The columns for the WHERE clause
                selectionArgs,                                 // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                      // don't filter by row groups
                null                                      // don't sort
        );

        int retId = cursor.getInt(cursor.getColumnIndex(USER_COLUMN_ID));
        String retName = cursor.getString(cursor.getColumnIndex(USER_COLUMN_NAME));
        String retBirthDate = cursor.getString(cursor.getColumnIndex(USER_COLUMN_BIRTH_DATE));
        String retGender = cursor.getString(cursor.getColumnIndex(USER_COLUMN_GENDER));
        Bitmap retBitmap = DbBitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex(USER_COLUMN_PICTURE)));
        try {
            return new User(retId, retName, retBirthDate, retGender, retBitmap);
        } catch (ParseException e) {
            e.printStackTrace();
            // FIXME: 05.11.2020
            return null;
        }
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
                USER_COLUMN_GENDER + " TEXT check(" + USER_COLUMN_GENDER + " in ('M', 'm', 'F', 'f'))" + ")");

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
