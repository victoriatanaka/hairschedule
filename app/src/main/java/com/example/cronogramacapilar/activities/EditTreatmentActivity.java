package com.example.cronogramacapilar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.content.Intent;
import android.graphics.Color;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Callable;

public class EditTreatmentActivity extends AppCompatActivity {
    private EditText lastDateField;
    private Spinner treatmentField;
    private EditText numberOfRepeatsField;
    private Spinner unitOfRepeatsField;
    private EditText observationsField;
    private long id;
    private Treatment treatment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_treatment);

        treatmentField = findViewById(R.id.treatment_type);
        lastDateField = findViewById(R.id.last_date);
        numberOfRepeatsField = findViewById(R.id.number_of_repeats);
        unitOfRepeatsField = findViewById(R.id.unit_of_repeats);
        observationsField = findViewById(R.id.observations);

        // get id and treatment
        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);
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
        TreatmentFormHelper treatmentHelper = new TreatmentFormHelper(EditTreatmentActivity.this);

        // setup type field
        treatmentHelper.setSpinnerLocked(treatmentField, treatment.type);

        // setup last date field
        treatmentHelper.setCalendars(lastDateField, treatment.lastDate);

        // setup recurrence fields
        treatmentHelper.configureRecurrenceFields(numberOfRepeatsField, unitOfRepeatsField);
        numberOfRepeatsField.setText(Integer.toString(treatment.repeats));
        treatmentHelper.updateUnitOfRepeatsValue(treatment.repeatsUnit, unitOfRepeatsField);

        // setup observations field
        observationsField.setText(treatment.observations);
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuHelper.tintMenuIcon(EditTreatmentActivity.this, menu.findItem(R.id.cancel), R.color.icons);
        MenuHelper.tintMenuIcon(EditTreatmentActivity.this, menu.findItem(R.id.save), R.color.icons);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cancel:
                finish();
                return true;
            case R.id.save:
                saveTreatment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveTreatment() {
        boolean canSave = true;

        // check if repeats is correct
        treatment.repeats = Integer.parseInt(numberOfRepeatsField.getText().toString());
        if (treatment.repeats <= 0) {
            numberOfRepeatsField.setError("Número não pode ser menor que 0.");
            numberOfRepeatsField.requestFocus();
            canSave = false;
        }

        // calculates next date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        treatment.repeatsUnit = unitOfRepeatsField.getSelectedItem().toString();

        try {
            treatment.lastDate = formatter.parse(lastDateField.getText().toString());
            treatment.nextDate = Treatment.calculateNextDate(treatment.lastDate, treatment.repeats, treatment.repeatsUnit);

        } catch (ParseException e) {
            e.printStackTrace();
            lastDateField.setHintTextColor(Color.RED);
            lastDateField.setHint("Selecione uma data.");
            lastDateField.setError("");
            canSave = false;
        }

        if (canSave) {
            treatment.observations = observationsField.getText().toString();
            new TreatmentDaoAsync().new SaveTreatmentAsync(
                    new Callable<Void>() {
                        public Void call() {
                            finish();
                            MainActivity.reload();
                            return null;
                        }
                    }, treatment).execute();

        }

        // Toast.makeText(view.getContext(), nextDate.toString(), Toast.LENGTH_LONG).show();

    }

}
