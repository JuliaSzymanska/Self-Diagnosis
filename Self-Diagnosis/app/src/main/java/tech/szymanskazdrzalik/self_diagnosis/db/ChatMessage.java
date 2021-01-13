package tech.szymanskazdrzalik.self_diagnosis.db;

import java.text.ParseException;
import java.util.Date;

public class ChatMessage {
    private int id;
    private int chatId;
    private Date date;
    private String message;
    private int isUserMessage;

    public ChatMessage(int id, int chatId, String date, String message, int isUserMessage) throws ParseException {
        this.id = id;
        this.date = SampleSQLiteDBHelper.DB_DATE_USER_FORMAT.parse(date);
        this.chatId = chatId;
        this.message = message;
        this.isUserMessage = isUserMessage;
    }

    public ChatMessage(int id, int chatId, Date date, String message, int isUserMessage) {
        this.id = id;
        this.chatId = chatId;
        this.date = date;
        this.message = message;
        this.isUserMessage = isUserMessage;
    }

    public int getIsUserMessage() {
        return isUserMessage;
    }

    public void setIsUserMessage(int userMessage) {
        isUserMessage = userMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
