package com.example.cronogramacapilar.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cronogramacapilar.R;
import com.example.cronogramacapilar.Treatment;
import com.example.cronogramacapilar.helpers.MenuHelper;
import com.example.cronogramacapilar.helpers.TreatmentFormHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditTreatmentActivity extends AppCompatActivity {
    private EditText lastDateField;
    private Spinner treatmentField;
    private EditText numberOfRepeatsField;
    private Spinner unitOfRepeatsField;
    private String treatmentType;
    private Date lastDate = new Date();
    private Date nextDate = new Date();
    private String repeatsUnit;
    private int repeats;
    private long id;
    private String observations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_treatment);

        // setup type field
        treatmentField = findViewById(R.id.treatment_type);
        lastDateField = findViewById(R.id.last_date);
        numberOfRepeatsField = findViewById(R.id.number_of_repeats);
        unitOfRepeatsField = findViewById(R.id.unit_of_repeats);
        TreatmentFormHelper treatmentHelper = new TreatmentFormHelper(this);
        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);
        treatmentType = intent.getStringExtra("type");
        treatmentHelper.setSpinnerLocked(treatmentField, treatmentType);
        treatmentHelper.configureRecurrenceFields(numberOfRepeatsField, unitOfRepeatsField);
        treatmentHelper.setCalendars(lastDateField);
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
        repeats = Integer.parseInt(numberOfRepeatsField.getText().toString());
        if (repeats <= 0) {
            numberOfRepeatsField.setError("Número não pode ser menor que 0.");
            numberOfRepeatsField.requestFocus();
            canSave = false;
        }

        // calculates next date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        repeatsUnit = unitOfRepeatsField.getSelectedItem().toString();

        try {
            lastDate = formatter.parse(lastDateField.getText().toString());
            nextDate = Treatment.calculateNextDate(lastDate, repeats, repeatsUnit);

        } catch (ParseException e) {
            e.printStackTrace();
            lastDateField.setHintTextColor(Color.RED);
            lastDateField.setHint("Selecione uma data.");
            lastDateField.setError("");
            canSave = false;
        }

        if (canSave) {
            observations = ((EditText) findViewById(R.id.observations)).getText().toString();
            new SaveTreatment().execute(id);
        }

        // Toast.makeText(view.getContext(), nextDate.toString(), Toast.LENGTH_LONG).show();

    }

    private class SaveTreatment extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... longs) {
            MainActivity.database.treatmentDao().save(lastDate, nextDate, repeats, repeatsUnit, observations, longs[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            MainActivity.reload();
        }
    }
}
