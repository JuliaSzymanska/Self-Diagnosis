package tech.szymanskazdrzalik.self_diagnosis.api;

import androidx.annotation.Nullable;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public abstract class DiagnoseRequest {
    protected final RequestUtil.ChatRequestListener listener;
    private final Response.Listener<JSONObject> successListener;
    private final Response.ErrorListener errorListener;
    private final Map<String, String> headers;
    private final JSONObject requestBody;

    public DiagnoseRequest(ChatActivity chatActivity) {
        this(chatActivity, null);
    }

    public DiagnoseRequest(ChatActivity chatActivity, @Nullable String userAnswer) {
        this.successListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                boolean shouldStop;
                try {
                    shouldStop = response.getBoolean("should_stop");
                    RequestUtil.getInstance().setConditionsArray(response.getJSONArray("conditions"));
                    if (shouldStop) {
                        if (!listener.finishDiagnose()) {
                            shouldStop = false;
                        }
                    }
                    if (!shouldStop) {
                        JSONObject jsonObjectQuestion = response.getJSONObject("question");
                        listener.onDoctorMessage(jsonObjectQuestion.getString("text"));
                        listener.onDoctorQuestionReceived(jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getString("id"),
                                jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getJSONArray("choices"),
                                jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getString("name"));
                    }
                } catch (JSONException e) {
                    chatActivity.onRequestFailure();
                }
            }
        };
        this.errorListener = error -> {
            System.out.println(error);
            chatActivity.onRequestFailure();
        };
        listener = chatActivity;
        GlobalVariables globalVariables = GlobalVariables.getInstance();
        if (!globalVariables.getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 dać tutaj wyjatek
            System.out.println("User not found!");
        }
        this.headers = RequestUtil.getDefaultHeaders(chatActivity);

        RequestUtil.addLanguageToInfermedicaHeaders(headers);

        this.requestBody = new JSONObject();

        try {
            JSONArray jsonArray = new JSONArray(RequestUtil.getInstance().getEvidenceArray().toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonArray.getJSONObject(i).remove("name");
            }
            RequestUtil.addUserDataToJsonObject(requestBody);
            requestBody.put("evidence", jsonArray);
            JSONObject jsonObjectExtras = new JSONObject();
            jsonObjectExtras.put("disable_groups", "true");
            requestBody.put("extras", jsonObjectExtras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (userAnswer != null) {
            listener.addUserMessage(userAnswer);
        }
    }

    protected Response.Listener<JSONObject> getSuccessListener() {
        return successListener;
    }

    protected Response.ErrorListener getErrorListener() {
        return errorListener;
    }

    protected Map<String, String> getHeaders() {
        return headers;
    }

    protected JSONObject getRequestBody() {
        return requestBody;
    }

    protected void addAgeToRequestBody(Callable callable) throws JSONException {
        callable.call(this.requestBody);
    }

    protected interface Callable {
        void call(JSONObject jsonObject) throws JSONException;
    }
}
