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
            boolean shouldStop = false;
            try {
                System.out.println(response);
                try {
                    shouldStop = response.getBoolean("should_stop");
                    RequestUtil.getInstance().setConditionsArray(response.getJSONArray("conditions"));
                    if (shouldStop) {
                        if (!listener.finishDiagnose()) {
                            shouldStop = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO: 16.12.2020 To znaczy że nie znaleziono pola should_stop, zrobić coś mądrego z tym
                    e.printStackTrace();
                }
                if (!shouldStop) {
                    JSONObject jsonObjectQuestion = response.getJSONObject("question");
                    listener.onDoctorMessage(jsonObjectQuestion.getString("text"));
                    listener.hideMessageBox();
                    listener.onDoctorQuestionReceived(jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getString("id"),
                            jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getJSONArray("choices"));
                }
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

    public MakeDiagnoseRequest(ChatActivity chatActivity, String userAnswer) {

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
            jsonObject.put("evidence", RequestUtil.getInstance().getEvidenceArray());
            JSONObject jsonObjectExtras = new JSONObject();
            jsonObjectExtras.put("disable_groups", "true");
            jsonObject.put("extras", jsonObjectExtras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        listener.addUserMessage(userAnswer);

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, headers, jsonObject, successListener, errorListener));

    }

    // TODO: 16.12.2020 Wyrzucić wspólne elementy wszystkich 3 metod do jednej metody

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
            jsonObject.put("evidence", RequestUtil.getInstance().getEvidenceArray());
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
