package tech.szymanskazdrzalik.self_diagnosis.db;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.util.Date;

public class User {
    private final int id;
    private final Date birthDate;
    private final String name;
    private final String gender;
    private final Bitmap picture;

    public User(int id, String name, String birthDate, String gender, Bitmap picture) throws ParseException {
        this.id = id;
        this.birthDate = SampleSQLiteDBHelper.DB_DATE_FORMAT.parse(birthDate);
        this.gender = gender;
        this.name = name;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public User(int id, String name, Date birthDate, String gender, Bitmap picture) {
        this.id = id;
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
