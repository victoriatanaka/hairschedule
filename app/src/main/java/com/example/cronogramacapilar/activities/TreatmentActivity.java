package com.example.cronogramacapilar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.cronogramacapilar.R;
import com.example.cronogramacapilar.Treatment;
import com.example.cronogramacapilar.helpers.MenuHelper;
import com.example.cronogramacapilar.helpers.TreatmentDaoAsync;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Callable;


public class TreatmentActivity extends AppCompatActivity {
    private TextView typeView;
    private TextView lastDateView;
    private TextView nextDateView;
    private TextView frequencyView;
    private TextView observationsView;
    private Treatment treatment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        Intent intent = getIntent();
        typeView = findViewById(R.id.treatment_type);
        lastDateView = findViewById(R.id.last_date);
        nextDateView = findViewById(R.id.next_date);
        frequencyView = findViewById(R.id.frequency);
        observationsView = findViewById(R.id.observations);
        long id = intent.getLongExtra("id", 0);
        new TreatmentDaoAsync().new GetTreatmentAsync(
                new Function<Treatment, Void>() {
                    @Override
                    public Void apply(Treatment treatment) {
                        return getTreatmentCallback(treatment);
                    }
                }).execute(id);
    }

    private Void getTreatmentCallback(Treatment treatment) {
        this.treatment = treatment;
        typeView.setText(treatment.type);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        lastDateView.setText("Última aplicação: " + formatter.format(treatment.lastDate));
        nextDateView.setText("Próxima aplicação: " + formatter.format(treatment.nextDate));
        frequencyView.setText("Repete a cada " + treatment.repeats + " " + treatment.repeatsUnit);
        observationsView.setText("Observações:\n" + treatment.observations);
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MenuHelper.tintMenuIcon(TreatmentActivity.this, menu.findItem(R.id.delete), R.color.icons);
        MenuHelper.tintMenuIcon(TreatmentActivity.this, menu.findItem(R.id.edit), R.color.icons);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                new TreatmentDaoAsync().new DeleteTreatmentAsync(
                        new Callable<Void>() {
                            public Void call() {
                                finish();
                                return null;
                            }
                        }).execute(treatment.id);
                return true;
            case R.id.edit:
                Intent intent = new Intent(this, EditTreatmentActivity.class);
                intent.putExtra("id", treatment.id);
                this.startActivity(intent);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (treatment != null) {
            new TreatmentDaoAsync().new GetTreatmentAsync(
                    new Function<Treatment, Void>() {
                        @Override
                        public Void apply(Treatment treatment) {
                            return getTreatmentCallback(treatment);
                        }
                    }).execute(treatment.id);
        }
    }
}
