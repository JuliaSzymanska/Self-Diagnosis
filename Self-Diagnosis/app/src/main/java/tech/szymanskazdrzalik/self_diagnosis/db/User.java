package tech.szymanskazdrzalik.self_diagnosis.db;

import android.graphics.Bitmap;
import android.os.Build;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public int getAge() {
        Calendar now = new GregorianCalendar();
        Calendar birth = new GregorianCalendar();
        now.setTime(new Date());
        now.setTime(this.birthDate);
        int result = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (birth.get(Calendar.MONTH) > now.get(Calendar.MONTH)) {
            result--;
        } else if (birth.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
            if (birth.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH)) {
                result--;
            }
        }
        return result;
    }
}
