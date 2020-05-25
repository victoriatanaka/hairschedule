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
                c.add(Calendar.DAY_OF_MONTH, numberOfRepeats * 7);
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

    public static int getTreatmentIcon(String treatmentType) {
        switch (treatmentType.toLowerCase()) {
            case "hidratação":
                return R.drawable.ic_rain_drops;
            case "reconstrução":
                return R.drawable.ic_protein_supplements;
            case "nutrição":
                return R.drawable.ic_coconut_oil;
            case "acidificação":
                return R.drawable.ic_cider;
            default:
                return R.drawable.ic_herbal_treatment;
        }
    }

    public static int getTreatmentNotificationTitle(String treatmentType) {
        switch (treatmentType.toLowerCase()) {
            case "hidratação":
                return R.string.title_notification_today_h;
            case "reconstrução":
                return R.string.title_notification_today_r;
            case "nutrição":
                return R.string.title_notification_today_n;
            case "acidificação":
                return R.string.title_notification_today_a;
            default:
                return R.string.title_notification_today;
        }
    }

}
