package tech.szymanskazdrzalik.self_diagnosis.api;

import androidx.annotation.Nullable;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;

public class MakeCovidRequest extends DiagnoseRequest {
    public MakeCovidRequest(ChatActivity chatActivity, @Nullable String userAnswer) {
        super(chatActivity, userAnswer);
        String url = InfermedicaApiClass.getInstance(chatActivity).getUrl() + "/covid19" + "/diagnosis";

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, this.getHeaders(), this.getRequestBody(), this.getSuccessListener(), this.getErrorListener()));
    }

    public MakeCovidRequest(ChatActivity chatActivity) {
        super(chatActivity);
    }
}
