package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import tech.szymanskazdrzalik.self_diagnosis.R;

public class ApiClass {

    private static final ApiClass INSTANCE = new ApiClass();

    private static final String url = "https://api.infermedica.com/v3";
    private static String id;
    private static String key;

    private ApiClass() {
    }

    public static ApiClass getInstance(Context context) {
        loadApiInfo(context);
        return INSTANCE;
    }

    public static void loadApiInfo(Context context) {
        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context)));
            id = (String) jsonObject.get("id");
            key = (String) jsonObject.get("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String loadJSONFromAsset(Context context) {
        String jsonString = "";
        try {
            System.out.println(context);
            InputStream is = context.getResources().openRawResource(R.raw.api_info);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }


}
