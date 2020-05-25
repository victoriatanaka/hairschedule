package com.example.cronogramacapilar.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.cronogramacapilar.Broadcasts;
import com.example.cronogramacapilar.Treatment;

import java.util.Calendar;

public final class NotificationHelper {

    public static void createNotification(Treatment treatment, Context context) {
        createNotificationReminder(treatment, context);
        createNotificationLate(treatment, context);
    }

    private static void createNotificationReminder(Treatment treatment, Context context) {
        Intent intent = new Intent(context, Broadcasts.ReminderBroadcast.class);
        intent.putExtra("type", treatment.type);
        intent.putExtra("nextDate", treatment.nextDate);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) treatment.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(treatment.nextDate);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);

        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()/1000,
        //      AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private static void createNotificationLate(Treatment treatment, Context context) {
        Intent intent = new Intent(context, Broadcasts.LateReminderBroadcast.class);
        intent.putExtra("type", treatment.type);
        intent.putExtra("nextDate", treatment.nextDate);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) treatment.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(treatment.nextDate);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);

        assert alarmManager != null;
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()/1000, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    static void cancelNotification(long id, Context context) {
        cancelNotificationReminder(id, context);
        cancelNotificationLate(id, context);
    }

    private static void cancelNotificationLate(long id, Context context) {
        Intent intent = new Intent(context, Broadcasts.LateReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
    }

    private static void cancelNotificationReminder(long id, Context context) {
        Intent intent = new Intent(context, Broadcasts.ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
    }
}
