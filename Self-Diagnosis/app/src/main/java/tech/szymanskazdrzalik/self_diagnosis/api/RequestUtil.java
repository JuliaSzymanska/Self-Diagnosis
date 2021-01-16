package tech.szymanskazdrzalik.self_diagnosis.api;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class RequestUtil {

    private final static RequestUtil INSTANCE = new RequestUtil();
    private JSONArray evidenceArray;
    private JSONArray conditionsArray;

    private RequestUtil() {
        evidenceArray = new JSONArray();
        conditionsArray = new JSONArray();
    }

    public static RequestUtil getInstance() {
        return INSTANCE;
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

    public JSONArray getConditionsArray() {
        return conditionsArray;
    }

    public void setConditionsArray(JSONArray conditionsArray) {
        this.conditionsArray = conditionsArray;
    }

    public JSONArray getEvidenceArray() {
        return evidenceArray;
    }

    public void addToEvidenceArray(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            evidenceArray.put(jsonArray.getJSONObject(i));
        }
    }

    public void setEvidenceArrayFromString(String string) throws JSONException {
        System.out.println("HALKO" + string);
        this.evidenceArray = new JSONArray(string);
    }

    public String getStringFromEvidenceArray() {
        return evidenceArray.toString();
    }

    public void addToEvidenceArray(JSONObject jsonObject) {
        evidenceArray.put(jsonObject);
    }

    public void resetEvidenceArray() {
        evidenceArray = new JSONArray();
    }

    public interface ChatRequestListener {
        void onDoctorMessage(String msg);

        void addUserMessage(String msg);

        void hideMessageBox();

        void addErrorMessageFromDoctor(String msg);

        void onDoctorQuestionReceived(String id, JSONArray msg);

        boolean finishDiagnose();

        void onRequestFailure();
    }
}
