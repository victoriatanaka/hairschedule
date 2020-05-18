package com.example.cronogramacapilar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cronogramacapilar.formhelpers.TreatmentHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewTreatment extends AppCompatActivity {
    private EditText lastDate;
    private Spinner treatment;
    private EditText numberOfRepeats;
    private Spinner unitOfRepeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_treatment);
        treatment = findViewById(R.id.treatment_type);
        lastDate = findViewById(R.id.last_date);
        numberOfRepeats = findViewById(R.id.number_of_repeats);
        unitOfRepeats = findViewById(R.id.unit_of_repeats);
        TreatmentHelper treatmentHelper = new TreatmentHelper(this);
        treatmentHelper.setSpinnerValues(treatment);
        treatmentHelper.configureRecurrenceFields(numberOfRepeats, unitOfRepeats);
        treatmentHelper.setCalendars(lastDate);
    }

    public void cancel(View view) {
        finish();
    }

    public void saveNewTreatment(View view) {
        boolean canSave = true;

        // check if type was chosen
        if (treatment.getSelectedItem().toString().equals(getResources().getString(R.string.tipo_de_tratamento))) {
            TextView errorText = (TextView) treatment.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Selecione um tipo de tratamento.");
            treatment.requestFocus();
            canSave = false;
        }

        // check if repeats is correct
        if (Integer.parseInt(numberOfRepeats.getText().toString()) <= 0) {
            numberOfRepeats.setError("Número não pode ser menor que 0.");
            numberOfRepeats.requestFocus();
            canSave = false;
        }

        // calculates next date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date nextDate = new Date();
        try {
            Date parsedDate = formatter.parse(lastDate.getText().toString());
            nextDate = Treatment.calculateNextDate(parsedDate, Integer.parseInt(numberOfRepeats.getText().toString()), unitOfRepeats.getSelectedItem().toString());

        } catch (ParseException e) {
            e.printStackTrace();
            lastDate.setHintTextColor(Color.RED);
            lastDate.setHint("Selecione uma data.");
            lastDate.setError("");
            canSave = false;
        }

        Toast.makeText(view.getContext(), nextDate.toString(), Toast.LENGTH_LONG).show();

    }
}
