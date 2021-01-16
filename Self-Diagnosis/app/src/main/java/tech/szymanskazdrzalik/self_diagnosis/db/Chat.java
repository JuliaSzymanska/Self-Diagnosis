package tech.szymanskazdrzalik.self_diagnosis.db;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Chat {

    private final Date date;
    private int id;
    private int userId;
    private String lastRequest;
    private String lastDoctorQuestion;
    private String lastDoctorQuestionId;
    private int isFinished;

    private Chat(Date date, int id, int userId, String lastRequest, String lastDoctorQuestion, String lastDoctorQuestionId, boolean isFinished) {
        this.date = date;
        this.id = id;
        this.userId = userId;
        this.lastRequest = lastRequest;
        this.lastDoctorQuestion = lastDoctorQuestion;
        this.lastDoctorQuestionId = lastDoctorQuestionId;
        this.isFinished = isFinished ? 1 : 0;
    }

    public static Builder builder(int id, int userId) {
        return new Builder(id, userId);
    }

    public String getLastDoctorQuestion() {
        return lastDoctorQuestion;
    }

    public String getLastDoctorQuestionId() {
        return lastDoctorQuestionId;
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

    public boolean getIsFinished() {
        return isFinished == 1;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished ? 1 : 0;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setLastDoctorQuestion(String lastDoctorQuestion) {
        this.lastDoctorQuestion = lastDoctorQuestion;
    }

    public void setLastDoctorQuestionId(String lastDoctorQuestionId) {
        this.lastDoctorQuestionId = lastDoctorQuestionId;
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

    public static final class Builder {
        private Date date = null;
        private final int chatId;
        private final int userId;
        private String lastRequest = null;
        private String lastDoctorQuestion = null;
        private String lastDoctorQuestionId = null;
        private int isFinished = 0;

        private Builder(int id, int userId) {
            this.chatId = id;
            this.userId = userId;
        }


        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder lastRequest(String lastRequest) {
            this.lastRequest = lastRequest;
            return this;
        }

        public Builder lastDoctorQuestionAndId(@NonNull String lastDoctorQuestion, @NonNull String lastDoctorQuestionId) {
            this.lastDoctorQuestion = lastDoctorQuestion;
            this.lastDoctorQuestionId = lastDoctorQuestionId;
            return this;
        }


        public Builder isFinished(boolean isFinished) {
            this.isFinished = isFinished ? 1 : 0;
            return this;
        }

        public Chat build() {
            if (date == null) {
                this.date = new Date();
            }
            if (lastRequest == null) {
                this.lastRequest = "";
            }
            if (lastDoctorQuestion == null || lastDoctorQuestionId == null) {
                this.lastDoctorQuestion = "";
                this.lastDoctorQuestionId = "";
            }
            return new Chat(this.date, this.chatId, this.userId, this.lastRequest, this.lastDoctorQuestion, this.lastDoctorQuestionId, this.isFinished == 1);
        }
    }
}
