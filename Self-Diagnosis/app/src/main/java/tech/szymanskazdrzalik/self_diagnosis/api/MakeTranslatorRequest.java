package tech.szymanskazdrzalik.self_diagnosis.api;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Locale;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;

public class MakeTranslatorRequest {


    public MakeTranslatorRequest(ChatActivity chatActivity, String text, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        String apiKey = TranslatorApiClass.getInstance(chatActivity).getKey();
        String apiLangSource = Locale.getDefault().getLanguage();
        String apiLangTarget = "en";
        String googleApiUrl = TranslatorApiClass.getInstance(chatActivity).getUrl() + "?key=" + apiKey + "&source=" + apiLangSource + "&target=" + apiLangTarget + "&q=" + text;
        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(Request.Method.GET, googleApiUrl, null, null, successListener, errorListener));
    }
}
