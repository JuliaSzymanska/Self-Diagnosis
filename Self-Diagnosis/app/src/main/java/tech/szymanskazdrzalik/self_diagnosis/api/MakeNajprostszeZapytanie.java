package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

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
        this.apiRequestQueue.addToRequestQueue(new ApiRequest(this.apiClass.getUrl() + "/info", headers, null, null));
    }
}
