package tech.szymanskazdrzalik.self_diagnosis.db;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private final Date birthDate;
    private final String name;
    private final String gender;
    private final Bitmap picture;

    public User(String name, String birthDate, String gender, Bitmap picture) throws ParseException {
        this.birthDate = SampleSQLiteDBHelper.DB_DATE_FORMAT.parse(birthDate);
        this.gender = gender;
        this.name = name;
        this.picture = picture;
    }

    public User(String name, Date birthDate, String gender, Bitmap picture) {
        this.birthDate = birthDate;
        this.name = name;
        this.gender = gender;
        this.picture = picture;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getName() {
        return name;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public String getGender() {
        return gender;
    }
}
