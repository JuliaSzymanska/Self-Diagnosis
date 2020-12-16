package tech.szymanskazdrzalik.self_diagnosis.api;


import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class RequestUtil {

    private RequestUtil() {
    }

    public static void addUserDataToJsonObject(JSONObject jsonObject) throws JSONException {
        if (!GlobalVariables.getInstance().getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 Change to user not found exception
            throw new RuntimeException();
        }
        User user = GlobalVariables.getInstance().getCurrentUser().get();
        try {
            jsonObject.put("sex", user.getFullGenderName());
            JSONObject ageJson = new JSONObject();
            ageJson.put("value", user.getAge());
            jsonObject.put("age", ageJson);
        } catch (JSONException e) {
            throw new JSONException(e);
        }
    }

    public static Map<String, String> getDefaultHeaders(Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("App-Id", ApiClass.getInstance(context).getId());
        headers.put("App-Key", ApiClass.getInstance(context).getKey());
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public interface ChatRequestListener {
        void addDoctorMessage(String msg);
        void addUserMessage(String msg);
        void hideChat();
        void addErrorMessageFromDoctor(String msg);
    }
}
