package tech.szymanskazdrzalik.self_diagnosis.api;

import org.json.JSONObject;

import java.io.InputStream;

public class ApiClass {

    private static final ApiClass INSTANCE = new ApiClass();

    private static final String url = "https://api.infermedica.com/v3";
    private String id;
    private String key;

    private ApiClass() {
    }

    public ApiClass getInstance() {
        return INSTANCE;
    }

    private void loadApiInfo(){
        JSONObject jsonObject = new JSONObject()
    }

}
