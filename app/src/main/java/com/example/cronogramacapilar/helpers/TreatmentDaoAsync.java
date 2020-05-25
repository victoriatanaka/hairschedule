package com.example.cronogramacapilar.helpers;

import android.os.AsyncTask;

import androidx.arch.core.util.Function;

import com.example.cronogramacapilar.Treatment;
import com.example.cronogramacapilar.activities.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public final class TreatmentDaoAsync {

    public static class DeleteTreatmentAsync extends AsyncTask<Long, Void, Void> {
        private final Callable<Void> callback;

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

    public static class SaveTreatmentAsync extends AsyncTask<Void, Void, Void> {
        private final Callable<Void> callback;
        private final Treatment treatment;

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

    public static class GetTreatmentAsync extends AsyncTask<Long, Void, Treatment> {
        private final Function<Treatment, Void> callback;

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

    public static class GetAllTreatmentsAsync extends AsyncTask<Long, Void, List<Treatment>> {
        private final Function<List<Treatment>, Void> callback;

        public GetAllTreatmentsAsync(Function<List<Treatment>, Void> callback) {
            this.callback = callback;
        }

        @Override
        protected List<Treatment> doInBackground(Long... longs) {
            return MainActivity.database.treatmentDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Treatment> treatments) {
            callback.apply(treatments);
        }
    }

    public static class CreateTreatmentAsync extends AsyncTask<Void, Void, Treatment> {
        private final Function<Treatment, Void> callback;
        private final String type;
        private final Date lastDate;
        private final Date nextDate;
        private final String repeatsUnit;
        private final int repeats;
        private final String observations;


        public CreateTreatmentAsync(Function<Treatment, Void> callback, String type, Date lastDate, Date nextDate, String repeatsUnit, int repeats, String observations) {
            this.callback = callback;
            this.type = type;
            this.lastDate = lastDate;
            this.nextDate = nextDate;
            this.repeatsUnit = repeatsUnit;
            this.repeats = repeats;
            this.observations = observations;
        }

        @Override
        protected Treatment doInBackground(Void... voids) {
            long id = MainActivity.database.treatmentDao().create(type, setTimeToZero(lastDate), setTimeToZero(nextDate), repeats, repeatsUnit, observations);
            return MainActivity.database.treatmentDao().get(id);
        }

        @Override
        protected void onPostExecute(Treatment treatment) {
            callback.apply(treatment);
        }
    }

    private static Date setTimeToZero(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
