package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MakeNajprostszeJson {

    private ApiClass apiClass;
    private ApiRequestQueue apiRequestQueue;

    public MakeNajprostszeJson(Context context) {
        this.apiClass = ApiClass.getInstance(context);
        this.apiRequestQueue = ApiRequestQueue.getInstance(context);
        Map<String, String> headers = new HashMap<>();
        headers.put("App-Id", this.apiClass.getId());
        headers.put("App-Key", this.apiClass.getKey());
        Response.Listener<JSONObject> listener = System.out::println;
        Response.ErrorListener errorListener = Throwable::printStackTrace;
        this.apiRequestQueue.addToRequestQueue(new JSONObjectRequestWithHeaders(0, this.apiClass.getUrl() + "/info", headers, null, listener, errorListener));
    }

}
