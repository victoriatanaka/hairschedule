package com.example.cronogramacapilar.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cronogramacapilar.R;
import com.example.cronogramacapilar.Treatment;
import com.example.cronogramacapilar.helpers.MenuHelper;
import com.example.cronogramacapilar.helpers.TreatmentDaoAsync;
import com.example.cronogramacapilar.helpers.TreatmentFormHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

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
    private TreatmentFormHelper treatmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_treatment);
        treatmentField = findViewById(R.id.treatment_type);
        lastDateField = findViewById(R.id.last_date);
        numberOfRepeatsField = findViewById(R.id.number_of_repeats);
        unitOfRepeatsField = findViewById(R.id.unit_of_repeats);
        treatmentHelper = new TreatmentFormHelper(this);
        treatmentHelper.setSpinnerValues(treatmentField);
        treatmentHelper.configureRecurrenceFields(numberOfRepeatsField, unitOfRepeatsField);
        treatmentHelper.setCalendars(lastDateField);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MenuHelper.tintMenuIcon(NewTreatmentActivity.this, menu.findItem(R.id.save), R.color.icons);
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
                treatmentHelper.saveNewTreatment(treatmentField, numberOfRepeatsField, unitOfRepeatsField, lastDateField, observations);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
