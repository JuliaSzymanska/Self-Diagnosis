package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MakeDiagnoseRequest {


    public MakeDiagnoseRequest(Context context, JSONArray jsonArray) {
        String url = ApiClass.getInstance(context).getUrl() + "/diagnosis";

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        if (!globalVariables.getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 dać tutaj wyjatek
            System.out.println("User not found!");
        }
        User user = globalVariables.getCurrentUser().get();


        Map<String, String> headers = new HashMap<>();
        headers.put("App-Id", ApiClass.getInstance(context).getId());
        headers.put("App-Key", ApiClass.getInstance(context).getKey());
        headers.put("Content-Type", "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sex", user.getFullGenderName());
            JSONObject ageJson = new JSONObject();
            ageJson.put("value", user.getAge());
            jsonObject.put("age", ageJson);
            jsonObject.put("evidence", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        Response.Listener<JSONObject> listener = System.out::println;
        Response.ErrorListener errorListener = System.out::println;

        ApiRequestQueue.getInstance(context).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, headers, jsonObject, listener, errorListener));

    }
}
