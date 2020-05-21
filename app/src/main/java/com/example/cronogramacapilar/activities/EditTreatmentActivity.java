package com.example.cronogramacapilar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.cronogramacapilar.R;
import com.example.cronogramacapilar.Treatment;
import com.example.cronogramacapilar.helpers.MenuHelper;
import com.example.cronogramacapilar.helpers.TreatmentDaoAsync;
import com.example.cronogramacapilar.helpers.TreatmentFormHelper;

import java.util.Locale;
import java.util.Objects;

public class EditTreatmentActivity extends AppCompatActivity {
    private EditText lastDateField;
    private Spinner treatmentField;
    private EditText numberOfRepeatsField;
    private Spinner unitOfRepeatsField;
    private EditText observationsField;
    private long id;
    private TreatmentFormHelper treatmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_treatment);

        treatmentField = findViewById(R.id.treatment_type);
        lastDateField = findViewById(R.id.last_date);
        numberOfRepeatsField = findViewById(R.id.number_of_repeats);
        unitOfRepeatsField = findViewById(R.id.unit_of_repeats);
        observationsField = findViewById(R.id.observations);

        // get id and treatment
        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);
        new TreatmentDaoAsync.GetTreatmentAsync(
                new Function<Treatment, Void>() {
                    @Override
                    public Void apply(Treatment treatment) {
                        return getTreatmentCallback(treatment);
                    }
                }).execute(id);
    }

    @SuppressWarnings("SameReturnValue")
    private Void getTreatmentCallback(Treatment treatment) {
        treatmentHelper = new TreatmentFormHelper(EditTreatmentActivity.this);

        // setup type field
        treatmentHelper.setSpinnerLocked(treatmentField, treatment.type);

        // setup last date field
        treatmentHelper.setCalendars(lastDateField, treatment.lastDate);

        // setup recurrence fields
        treatmentHelper.configureRecurrenceFields(numberOfRepeatsField, unitOfRepeatsField);
        numberOfRepeatsField.setText(String.format(Locale.getDefault(), "%d", treatment.repeats));
        treatmentHelper.updateUnitOfRepeatsValue(treatment.repeatsUnit, unitOfRepeatsField);

        // setup observations field
        observationsField.setText(treatment.observations);
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        MenuHelper.tintMenuIcon(EditTreatmentActivity.this, menu.findItem(R.id.save), R.color.icons);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.save:
                String observations = ((EditText) findViewById(R.id.observations)).getText().toString();
                treatmentHelper.saveTreatment(treatmentField, numberOfRepeatsField, unitOfRepeatsField, lastDateField, observations, id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
