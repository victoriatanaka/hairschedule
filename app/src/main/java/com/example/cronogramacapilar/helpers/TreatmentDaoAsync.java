package com.example.cronogramacapilar.helpers;

import android.os.AsyncTask;

import androidx.arch.core.util.Function;

import com.example.cronogramacapilar.Treatment;
import com.example.cronogramacapilar.activities.EditTreatmentActivity;
import com.example.cronogramacapilar.activities.MainActivity;
import com.example.cronogramacapilar.activities.NewTreatmentActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

public final class TreatmentDaoAsync {

    public class DeleteTreatmentAsync extends AsyncTask<Long, Void, Void> {
        private Callable<Void> callback;

        public DeleteTreatmentAsync(Callable<Void> callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            MainActivity.database.treatmentDao().delete(longs[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                callback.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SaveTreatmentAsync extends AsyncTask<Void, Void, Void> {
        private Callable<Void> callback;
        private Treatment treatment;

        public SaveTreatmentAsync(Callable<Void> callback, Treatment treatment) {
            this.callback = callback;
            this.treatment = treatment;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainActivity.database.treatmentDao().save(setTimeToZero(treatment.lastDate), setTimeToZero(treatment.nextDate), treatment.repeats, treatment.repeatsUnit, treatment.observations, treatment.id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                callback.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class GetTreatmentAsync extends AsyncTask<Long, Void, Treatment> {
        private Function<Treatment, Void> callback;

        public GetTreatmentAsync(Function<Treatment, Void> callback) {
            this.callback = callback;
        }

        @Override
        protected Treatment doInBackground(Long... longs) {
            return MainActivity.database.treatmentDao().get(longs[0]);
        }

        @Override
        protected void onPostExecute(Treatment treatment) {
            callback.apply(treatment);
        }
    }

    public class CreateTreatmentAsync extends AsyncTask<Void, Void, Void> {
        private Callable<Void> callback;
        private String type;
        private Date lastDate;
        private Date nextDate;
        private String repeatsUnit;
        private int repeats;
        private String observations;


        public CreateTreatmentAsync(Callable<Void> callback, String type, Date lastDate, Date nextDate, String repeatsUnit, int repeats, String observations) {
            this.callback = callback;
            this.type = type;
            this.lastDate = lastDate;
            this.nextDate = nextDate;
            this.repeatsUnit = repeatsUnit;
            this.repeats = repeats;
            this.observations = observations;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainActivity.database.treatmentDao().create(type, setTimeToZero(lastDate), setTimeToZero(nextDate), repeats, repeatsUnit, observations);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                callback.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Date setTimeToZero(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
