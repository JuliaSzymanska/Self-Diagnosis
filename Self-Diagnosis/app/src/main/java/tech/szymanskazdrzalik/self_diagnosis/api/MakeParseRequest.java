package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;
import tech.szymanskazdrzalik.self_diagnosis.db.Chat;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MakeParseRequest {

    private final ApiClass apiClass;
    private final ApiRequestQueue apiRequestQueue;
    private final String url;
    private final Response.ErrorListener errorListener = error -> {
        System.out.println(error);
        // TODO: 16.12.2020 Make show error message / show
    };
    private final Context context;
    private final String userMessage;
    private final RequestUtil.ChatRequestListener chatRequestListener;
    private ChatActivity chatActivity;
    private final Response.Listener<JSONObject> successListener = response -> {
        try {
            JSONArray jsonArrayFromResponse = response.getJSONArray("mentions");
            JSONArray jsonArrayToRequest = new JSONArray();
            for (int i = 0; i < jsonArrayFromResponse.length(); i++) {
                JSONObject jsonObject = jsonArrayFromResponse.getJSONObject(i);
                if (jsonObject.getString("type").equals("symptom")) {
                    JSONObject clearJsonObject = new JSONObject();
                    clearJsonObject.put("id", jsonObject.getString("id"));
                    clearJsonObject.put("choice_id", jsonObject.getString("choice_id"));
                    clearJsonObject.put("source", "initial");
                    jsonArrayToRequest.put(clearJsonObject);
                }
            }
            RequestUtil.getInstance().addToEvidenceArray(jsonArrayToRequest);

            new MakeDiagnoseRequest(chatActivity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public MakeParseRequest(ChatActivity chatActivity, String text) {
        this.chatActivity = chatActivity;
        this.chatRequestListener = chatActivity;
        this.context = chatActivity;
        this.apiClass = ApiClass.getInstance(context);
        this.apiRequestQueue = ApiRequestQueue.getInstance(context);
        this.url = this.apiClass.getUrl() + "/parse";
        this.userMessage = text;

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        if (!globalVariables.getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 Add user not found exception
            System.out.println("User not found");
        }

        Map<String, String> headers = RequestUtil.getDefaultHeaders(context);

        JSONObject jsonObject = new JSONObject();
        try {
            RequestUtil.addUserDataToJsonObject(jsonObject);
            jsonObject.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        chatRequestListener.addUserMessage(text);

        this.apiRequestQueue.addToRequestQueue(new JSONObjectRequestWithHeaders(Request.Method.POST, this.url, headers, jsonObject, this.successListener, this.errorListener));
    }


}
