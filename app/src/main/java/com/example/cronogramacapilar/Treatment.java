package com.example.cronogramacapilar;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "treatments")
public class Treatment implements Cloneable {
    @PrimaryKey
    public long id;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "last_date")
    public Date lastDate;

    @ColumnInfo(name = "next_date")
    public Date nextDate;

    @ColumnInfo(name = "repeats")
    public int repeats;

    @ColumnInfo(name = "repeats_unit")
    public String repeatsUnit;

    @ColumnInfo(name = "observations")
    public String observations;

    @NonNull
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static Date calculateNextDate(Date lastDate, int numberOfRepeats, String unitOfRepeats) {
        Calendar c = Calendar.getInstance();
        c.setTime(lastDate);
        switch (unitOfRepeats.charAt(0)) {
            case 'd':
                c.add(Calendar.DAY_OF_MONTH, numberOfRepeats);
                break;
            case 's':
                c.add(Calendar.DAY_OF_MONTH, numberOfRepeats*7);
                break;
            case 'm':
                c.add(Calendar.MONTH, numberOfRepeats);
                break;
            case 'a':
                c.add(Calendar.YEAR, numberOfRepeats);
                break;
            default:
                Log.e("updateUnitOfRepeatsVal", "Invalid value for unit of repeats: " + unitOfRepeats);
        }
        return c.getTime();
    }
}
