package com.example.cronogramacapilar.activities;

import androidx.appcompat.app.AppCompatActivity;

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

public class NewTreatmentActivity extends AppCompatActivity {
    private EditText lastDateField;
    private Spinner treatmentField;
    private EditText numberOfRepeatsField;
    private Spinner unitOfRepeatsField;
    private String treatmentType;
    private Date lastDate = new Date();
    private Date nextDate = new Date();
    private String repeatsUnit;
    private int repeats;
    private String observations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_treatment);
        treatmentField = findViewById(R.id.treatment_type);
        lastDateField = findViewById(R.id.last_date);
        numberOfRepeatsField = findViewById(R.id.number_of_repeats);
        unitOfRepeatsField = findViewById(R.id.unit_of_repeats);
        TreatmentFormHelper treatmentHelper = new TreatmentFormHelper(this);
        treatmentHelper.setSpinnerValues(treatmentField);
        treatmentHelper.configureRecurrenceFields(numberOfRepeatsField, unitOfRepeatsField);
        treatmentHelper.setCalendars(lastDateField);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuHelper.tintMenuIcon(NewTreatmentActivity.this, menu.findItem(R.id.cancel), R.color.icons);
        MenuHelper.tintMenuIcon(NewTreatmentActivity.this, menu.findItem(R.id.save), R.color.icons);
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
                saveNewTreatment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveNewTreatment() {
        boolean canSave = true;

        // check if type was chosen
        treatmentType = treatmentField.getSelectedItem().toString();
        if (treatmentType.equals(getResources().getString(R.string.tipo_de_tratamento))) {
            TextView errorText = (TextView) treatmentField.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Selecione um tipo de tratamento.");
            treatmentField.requestFocus();
            canSave = false;
        }

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
            new CreateTreatment().execute();
        }

        // Toast.makeText(view.getContext(), nextDate.toString(), Toast.LENGTH_LONG).show();

    }

    private class CreateTreatment extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            MainActivity.database.treatmentDao().create(treatmentType, lastDate, nextDate, repeats, repeatsUnit, observations);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            NewTreatmentActivity.this.finish();
            MainActivity.reload();
        }
    }
}
