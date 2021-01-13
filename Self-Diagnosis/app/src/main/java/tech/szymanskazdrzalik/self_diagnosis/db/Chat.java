package tech.szymanskazdrzalik.self_diagnosis.db;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Chat {

    private final Date date;
    private int id;
    private int userId;
    private String lastRequest;

    public Chat(int id, int userId, String lastRequest) {
        this.id = id;
        this.userId = userId;
        this.lastRequest = lastRequest;
        this.date = new Date();
    }

    public Chat(int id, int userId, Date date, String lastRequest) {
        this.id = id;
        this.userId = userId;
        this.lastRequest = lastRequest;
        this.date = date;
    }

    public Chat(int id, int userId, JSONObject lastRequest) {
        this.id = id;
        this.userId = userId;
        this.lastRequest = lastRequest.toString();
        this.date = new Date();
    }

    public Chat(int id, int userId, Date date, JSONObject lastRequest) {
        this.id = id;
        this.userId = userId;
        this.lastRequest = lastRequest.toString();
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    // TODO: 11.01.2021 TEST
    public JSONObject getLastRequestJSONObject() throws JSONException {
        return new JSONObject(lastRequest);
    }
}
