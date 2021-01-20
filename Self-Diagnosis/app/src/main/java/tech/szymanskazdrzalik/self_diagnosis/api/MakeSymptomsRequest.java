package tech.szymanskazdrzalik.self_diagnosis.api;

import android.app.VoiceInteractor;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;

public class MakeSymptomsRequest {

    private Response.ErrorListener errorListener;
    private final RequestUtil.ChatRequestListener listener;

    private final Response.Listener<JSONObject> successListener = response -> {
        System.out.println("Odpowiedz:");
        System.out.println(response);
    };

    public MakeSymptomsRequest(ChatActivity chatActivity) {

        this.errorListener = error -> {
//            chatActivity.onRequestFailure();
            System.out.println(error);
        };

        listener = chatActivity;

        String url = "https://api.infermedica.com/v3/symptoms/s_1558?age.value=21&age.unit=year";

        Map<String, String> headers = RequestUtil.getDefaultHeaders(chatActivity);

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(Request.Method.GET, url, headers, null, successListener, errorListener));
        System.out.println("wyslano zapytanie");
    }


}
