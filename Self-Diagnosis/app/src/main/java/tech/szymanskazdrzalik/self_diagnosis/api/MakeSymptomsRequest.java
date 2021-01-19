package tech.szymanskazdrzalik.self_diagnosis.api;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;

public class MakeSymptomsRequest {

    private Response.ErrorListener errorListener;
    private final RequestUtil.ChatRequestListener listener;

    private final Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            System.out.println(response);
        }
    };

    public MakeSymptomsRequest(ChatActivity chatActivity) {

        listener = chatActivity;

        String url = ApiClass.getInstance(chatActivity).getUrl() + "/concepts?types=symptom";

        Map<String, String> headers = RequestUtil.getDefaultHeaders(chatActivity);

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, headers, null, successListener, errorListener));

    }


}
