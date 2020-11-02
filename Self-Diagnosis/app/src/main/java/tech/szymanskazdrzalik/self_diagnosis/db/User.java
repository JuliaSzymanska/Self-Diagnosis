package tech.szymanskazdrzalik.self_diagnosis.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private final Date birthDate;
    private final String name;
    private final String gender;

    public User(String name, String birthDate, String gender) throws ParseException {
        this.birthDate = SampleSQLiteDBHelper.DB_DATE_FORMAT.parse(birthDate);
        this.gender = gender;
        this.name = name;
    }

    public User(String name, Date birthDate, String gender) {
        this.birthDate = birthDate;
        this.name = name;
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }
}
