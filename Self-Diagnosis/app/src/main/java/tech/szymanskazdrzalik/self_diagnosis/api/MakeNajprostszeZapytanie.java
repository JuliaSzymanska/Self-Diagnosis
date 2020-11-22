package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class MakeNajprostszeZapytanie {

    private ApiClass apiClass;
    private ApiRequestQueue apiRequestQueue;

    public MakeNajprostszeZapytanie(Context context) {
        this.apiClass = ApiClass.getInstance(context);
        this.apiRequestQueue = ApiRequestQueue.getInstance(context);
        Map<String, String> headers = new HashMap<>();
        headers.put("App-Id", this.apiClass.getId());
        headers.put("App-Key", this.apiClass.getKey());
        Response.Listener<JsonObject> listener = System.out::println;
        Response.ErrorListener errorListener = Throwable::printStackTrace;
//        this.apiRequestQueue.addToRequestQueue(new ApiRequest<>(this.apiClass.getUrl() + "/info", JsonObject.class, headers, listener, errorListener));
    }
}
