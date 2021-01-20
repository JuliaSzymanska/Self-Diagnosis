package tech.szymanskazdrzalik.self_diagnosis.api;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;

public class MakeTranslatorRequest {

    private Response.ErrorListener errorListener;
    private RequestUtil.ChatRequestListener listener;

    private final Response.Listener<JSONObject> successListener = response -> {
        System.out.println("Odpowiedz:");
        System.out.println(response);
    };

    public MakeTranslatorRequest(ChatActivity chatActivity, String text) {

        this.errorListener = error -> {
//            chatActivity.onRequestFailure();
            System.out.println(error);
        };
        String apiKey = TranslatorApiClass.getInstance(chatActivity).getKey();
        String apiLangSource = "pl";
        String apiLangTarget = "en";
        String apiWord = text;
        String googleApiUrl = "https://www.googleapis.com/language/translate/v2?key=" + apiKey + "&source=" + apiLangSource + "&target=" + apiLangTarget + "&q=" + apiWord;

        listener = chatActivity;

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(Request.Method.GET, googleApiUrl, null, null, successListener, errorListener));
        System.out.println("wyslano zapytanie");
    }

}

/*
    kHttpClient client = new OkHttpClient();
    String apiKey = "My API key";
    String apiLangSource = "en";
    String apiLangTarget = "de";
    String apiWord = "Hello";
    String googleApiUrl = "https://www.googleapis.com/language/translate/v2?key=" + apiKey + "&source=" + apiLangSource + "&target=" + apiLangTarget + "&q=" + apiWord;
    Request request = new Request.Builder().url(googleApiUrl).build();

    Log.d(TAG, "API STRING" + googleApiUrl);

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
@Override
public void onFailure(Request request, IOException e) {
        Log.d(TAG , "HTTP CALL FAIL");
        }

@Override
public void onResponse(Response response) throws IOException {
        Log.d(TAG , response.body().string());

        }
        });

 */