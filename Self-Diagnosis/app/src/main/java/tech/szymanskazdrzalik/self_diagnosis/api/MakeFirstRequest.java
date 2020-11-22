package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import com.android.volley.Response;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MakeFirstRequest {

    private ApiClass apiClass;
    private ApiRequestQueue apiRequestQueue;
    private String url;

    public MakeFirstRequest(Context context, String text) {
        this.apiClass = ApiClass.getInstance(context);
        this.apiRequestQueue = ApiRequestQueue.getInstance(context);
        this.url = this.apiClass.getUrl() + "/diagnosis";

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        User user = globalVariables.getCurrentUser();

        Map<String, String> headers = new HashMap<>();
        headers.put("App-Id", this.apiClass.getId());
        headers.put("App-Key", this.apiClass.getKey());

        Map<String, String> body = new HashMap<>();
        body.put("sex", user.getFullGenderName());
        Map<String, Integer> age = new HashMap<>();
        age.put("value", user.getAge());
        body.put("age", this.IntegerMapToString(age));
        body.put("text", text);

        Response.Listener<JsonObject> listener = System.out::println;
        Response.ErrorListener errorListener = Throwable::printStackTrace;
        this.apiRequestQueue.addToRequestQueue(new ApiRequest<>(this.url, JsonObject.class, headers, body, listener, errorListener));
    }

    private String StringMapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder("{");
        for (String key : map.keySet()) {
            stringBuilder.append("\"").append(key).append("\": \"").append(map.get(key)).append("\", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()).append("}");
        return stringBuilder.toString();
    }

    private String IntegerMapToString(Map<String, Integer> map) {
        StringBuilder stringBuilder = new StringBuilder("{");
        for (String key : map.keySet()) {
            stringBuilder.append("\"").append(key).append("\": ").append(map.get(key)).append(", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()).append("}");
        return stringBuilder.toString();
    }

}
