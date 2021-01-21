package tech.szymanskazdrzalik.self_diagnosis.api;

import androidx.annotation.Nullable;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MakeDiagnoseRequest extends DiagnoseRequest{


    public MakeDiagnoseRequest(ChatActivity chatActivity, @Nullable String userAnswer) {
        super(chatActivity, userAnswer);

        String url = InfermedicaApiClass.getInstance(chatActivity).getUrl() + "/v3" + "/diagnosis";
        try {
            this.addAgeToRequestBody(RequestUtil::addAgeToJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, this.getHeaders(), this.getRequestBody(), this.getSuccessListener(), this.getErrorListener()));
    }

    public MakeDiagnoseRequest(ChatActivity chatActivity) {
        this(chatActivity, null);
    }
}
