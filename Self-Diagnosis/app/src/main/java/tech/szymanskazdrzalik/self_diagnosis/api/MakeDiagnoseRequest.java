package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MakeDiagnoseRequest {


    public MakeDiagnoseRequest(Context context, JSONArray jsonArray) {
        String url = ApiClass.getInstance(context).getUrl() + "/diagnosis";

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        if (!globalVariables.getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 dać tutaj wyjatek
            System.out.println("User not found!");
        }

        Map<String, String> headers = RequestUtil.getDefaultHeaders(context);

        JSONObject jsonObject = new JSONObject();
        try {
            RequestUtil.addUserDataToJsonObject(jsonObject);
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
