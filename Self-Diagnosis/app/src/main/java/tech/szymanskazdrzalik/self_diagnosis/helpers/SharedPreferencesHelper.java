package tech.szymanskazdrzalik.self_diagnosis.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import tech.szymanskazdrzalik.self_diagnosis.db.SampleSQLiteDBHelper;

public class SharedPreferencesHelper {

    public static void loadUser(@NonNull Context context) {
        int id = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE).getInt("user_id", 1000);
        // TODO: 05.11.2020 Remove after fixing inner method
        GlobalVariables.getInstance().setCurrentUser(SampleSQLiteDBHelper.getUserByID(context, id));
    }

    public static void saveUserId(@NonNull Context context, int userId) {
        SharedPreferences.Editor editor = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE).edit();
        editor.putInt("user_id", userId);
        editor.apply();
    }

}
