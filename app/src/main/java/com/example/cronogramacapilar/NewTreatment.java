package com.example.cronogramacapilar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cronogramacapilar.formhelpers.TreatmentHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTreatment extends AppCompatActivity {
    private EditText lastDateField;
    private Spinner treatmentField;
    private EditText numberOfRepeatsField;
    private Spinner unitOfRepeatsField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_treatment);
        treatmentField = findViewById(R.id.treatment_type);
        lastDateField = findViewById(R.id.last_date);
        numberOfRepeatsField = findViewById(R.id.number_of_repeats);
        unitOfRepeatsField = findViewById(R.id.unit_of_repeats);
        TreatmentHelper treatmentHelper = new TreatmentHelper(this);
        treatmentHelper.setSpinnerValues(treatmentField);
        treatmentHelper.configureRecurrenceFields(numberOfRepeatsField, unitOfRepeatsField);
        treatmentHelper.setCalendars(lastDateField);
    }

    public void cancel(View view) {
        finish();
    }

    public void saveNewTreatment(View view) {
        boolean canSave = true;

        // check if type was chosen
        String treatment = treatmentField.getSelectedItem().toString();
        if (treatment.equals(getResources().getString(R.string.tipo_de_tratamento))) {
            TextView errorText = (TextView) treatmentField.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Selecione um tipo de tratamento.");
            treatmentField.requestFocus();
            canSave = false;
        }

        // check if repeats is correct
        int repeats = Integer.parseInt(numberOfRepeatsField.getText().toString());
        if (repeats <= 0) {
            numberOfRepeatsField.setError("Número não pode ser menor que 0.");
            numberOfRepeatsField.requestFocus();
            canSave = false;
        }

        // calculates next date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date lastDate = new Date();
        Date nextDate = new Date();
        String repeatsUnit = unitOfRepeatsField.getSelectedItem().toString();

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
            String observations = ((EditText) findViewById(R.id.observations)).getText().toString();
            MainActivity.database.treatmentDao().create(treatment, lastDate, nextDate, repeats, repeatsUnit, observations);
            finish();
        }

        // Toast.makeText(view.getContext(), nextDate.toString(), Toast.LENGTH_LONG).show();

    }
}
