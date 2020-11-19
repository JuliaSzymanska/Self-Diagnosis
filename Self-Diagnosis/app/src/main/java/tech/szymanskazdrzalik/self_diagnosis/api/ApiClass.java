package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ApiClass {

    private static final ApiClass INSTANCE = new ApiClass();

    private static final String url = "https://api.infermedica.com/v3";
    private String id;
    private String key;

    private ApiClass() {
    }

    public ApiClass getInstance(Context context) {
        loadApiInfo(context);
        return INSTANCE;
    }

    public void loadApiInfo(Context context) {
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context));
            id = (String) jsonObject.get("id");
            key = (String) jsonObject.get("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset(Context context) {
        String jsonString = "";
        try {
            InputStream is = context.getAssets().open("yourfilename.json");
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


}
