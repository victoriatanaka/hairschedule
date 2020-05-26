package com.example.cronogramacapilar.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.cronogramacapilar.R;
import com.example.cronogramacapilar.TreatmentsAdapter;
import com.example.cronogramacapilar.TreatmentsDatabase;
import com.example.cronogramacapilar.fragments.MarkAsCompleteBottomFragments;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static TreatmentsDatabase database;
    private TreatmentsAdapter adapter;
    private Snackbar snackbar;
    private ShowcaseView showCaseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = Room
                .databaseBuilder(getApplicationContext(), TreatmentsDatabase.class, "treatments")
                .allowMainThreadQueries()
                .build();

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideShowcase();
                Intent intent = new Intent(view.getContext(), NewTreatmentActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        //database.treatmentDao().deleteAll();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new TreatmentsAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void hideShowcase() {
        if (showCaseView != null)
            showCaseView.hide();
    }

    public void createShowcase() {
        if (showCaseView != null) {
            showCaseView.show();
            return;
        }
        Button button = new Button(this);
        button.setText("");
        button.setEnabled(false);
        button.setVisibility(View.GONE);

        showCaseView = new ShowcaseView.Builder(this)
                .setContentTitle(this.getString(R.string.showcase_create_treatment_title))
                .setContentText(this.getString(R.string.showcase_create_treatment_text))
                .replaceEndButton(button)
                .setTarget(new ViewTarget(R.id.fab, this))
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.reload();
        dismissSnackbar();
    }

    private void dismissSnackbar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        assert item != null;
        return super.onOptionsItemSelected(item);
    }

    public void setSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarmes";
            String description = "Canal para lembretes de tratamentos";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(new long[]{200, 250, 200, 250});
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }
}
