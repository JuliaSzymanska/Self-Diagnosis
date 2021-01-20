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
    private final Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            boolean shouldStop = false;
            try {
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
                            jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getJSONArray("choices"),
                            jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Response.ErrorListener errorListener;

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

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, headers, jsonObject, successListener, errorListener));

    }

    public MakeDiagnoseRequest(ChatActivity chatActivity, String userAnswer) {

        this.errorListener = error -> {
            chatActivity.onRequestFailure();
        };

        listener = chatActivity;

        String url = ApiClass.getInstance(chatActivity).getUrl() + "/diagnosis";

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        if (!globalVariables.getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 dać tutaj wyjatek
            System.out.println("User not found!");
        }

        Map<String, String> headers = RequestUtil.getDefaultHeaders(chatActivity);

        RequestUtil.addLanguageToHeaders(headers);

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray(RequestUtil.getInstance().getEvidenceArray().toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonArray.getJSONObject(i).remove("name");
            }
            RequestUtil.addUserDataToJsonObject(jsonObject);
            jsonObject.put("evidence", jsonArray);
            JSONObject jsonObjectExtras = new JSONObject();
            jsonObjectExtras.put("disable_groups", "true");
            jsonObject.put("extras", jsonObjectExtras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listener.addUserMessage(userAnswer);

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

        this.errorListener = error -> {
            chatActivity.onRequestFailure();
            // TODO: 16.12.2020 Make show error message / show
        };

        Map<String, String> headers = RequestUtil.getDefaultHeaders(chatActivity);

        RequestUtil.addLanguageToHeaders(headers);


        JSONObject jsonObject = new JSONObject();


        try {
            JSONArray jsonArray = new JSONArray(RequestUtil.getInstance().getEvidenceArray().toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonArray.getJSONObject(i).remove("name");
            }
            RequestUtil.addUserDataToJsonObject(jsonObject);
            jsonObject.put("evidence", jsonArray);
            JSONObject jsonObjectExtras = new JSONObject();
            jsonObjectExtras.put("disable_groups", "true");
            jsonObject.put("extras", jsonObjectExtras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, headers, jsonObject, successListener, errorListener));

    }

    // TODO: 16.12.2020 Wyrzucić wspólne elementy wszystkich 3 metod do jednej metody
}
