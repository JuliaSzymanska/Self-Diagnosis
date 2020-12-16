package tech.szymanskazdrzalik.self_diagnosis.api;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MakeDiagnoseRequest {
    private final RequestUtil.ChatRequestListener listener;

    private final Response.ErrorListener errorListener = error -> {
        System.out.println(error);
        // TODO: 16.12.2020 Make show error message / show
    };

    private final Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                JSONObject jsonObjectQuestion = response.getJSONObject("question");
                listener.onDoctorMessage(jsonObjectQuestion.getString("text"));
                listener.hideMessageBox();
                listener.onDoctorQuestionReceived(jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getString("choices"),
                        jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getJSONArray("choices"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public MakeDiagnoseRequest(ChatActivity chatActivity, JSONArray jsonArray) {

        listener = chatActivity;

        String url = ApiClass.getInstance(chatActivity).getUrl() + "/diagnosis";

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        if (!globalVariables.getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 dać tutaj wyjatek
            System.out.println("User not found!");
        }

        Map<String, String> headers = RequestUtil.getDefaultHeaders(chatActivity);

        JSONObject jsonObject = new JSONObject();
        try {
            RequestUtil.addUserDataToJsonObject(jsonObject);
            jsonObject.put("evidence", jsonArray);
            JSONObject jsonObjectExtras = new JSONObject();
            jsonObjectExtras.put("disable_groups", "true");
            jsonObject.put("extras", jsonObjectExtras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, headers, jsonObject, successListener, errorListener));

    }

    public MakeDiagnoseRequest(ChatActivity chatActivity) {

        listener = chatActivity;

        String url = ApiClass.getInstance(chatActivity).getUrl() + "/diagnosis";

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        if (!globalVariables.getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 dać tutaj wyjatek
            System.out.println("User not found!");
        }

        Map<String, String> headers = RequestUtil.getDefaultHeaders(chatActivity);

        JSONObject jsonObject = new JSONObject();
        try {
            RequestUtil.addUserDataToJsonObject(jsonObject);
            jsonObject.put("evidence", RequestUtil.getEvidenceArray());
            JSONObject jsonObjectExtras = new JSONObject();
            jsonObjectExtras.put("disable_groups", "true");
            jsonObject.put("extras", jsonObjectExtras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, headers, jsonObject, successListener, errorListener));

    }
}
