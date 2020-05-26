package com.example.cronogramacapilar;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.example.cronogramacapilar.activities.MainActivity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

public class Broadcasts {
    public static class ReminderBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = Objects.requireNonNull(intent.getExtras()).getString("type");
            String title;
            String text;
            Date nextDate = (Date) intent.getSerializableExtra("nextDate");
            long daysUntil = 0;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate now = LocalDate.now();
                LocalDate nextLDate = Objects.requireNonNull(nextDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                daysUntil = now.until(nextLDate, ChronoUnit.DAYS);
            }

            if (daysUntil != 0)
                return;

            title = context.getResources().getString(Treatment.getTreatmentNotificationTitle(Objects.requireNonNull(type)), type.toLowerCase());
            text = "Vamos fazer " + type.toLowerCase() + " hoje?";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.ic_spa)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(createMainPendingIntent(context));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.notify((type + "today").hashCode() * 31, builder.build());
        }
    }

    public static class LateReminderBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = Objects.requireNonNull(intent.getExtras()).getString("type");
            String title;
            String text;
            Date nextDate = (Date) intent.getSerializableExtra("nextDate");
            long daysUntil = 0;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate now = LocalDate.now();
                LocalDate nextLDate = Objects.requireNonNull(nextDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                daysUntil = now.until(nextLDate, ChronoUnit.DAYS);
            }

            title = context.getResources().getString(R.string.title_notification_late, Objects.requireNonNull(type).toLowerCase());
            text = "Seu tratamento de " + type.toLowerCase() + " está " + context.getResources().getQuantityString(R.plurals.n_days_late, (int) -daysUntil, -daysUntil) + "! \uD83D\uDE2F";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.ic_spa)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(createMainPendingIntent(context));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.notify((type + "late").hashCode() * 31, builder.build());
        }
    }

    private static PendingIntent createMainPendingIntent(Context context) {
        // Create an Intent for the activity you want to start
        Intent mainIntent = new Intent(context, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(mainIntent);
        // Get the PendingIntent containing the entire back stack
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
