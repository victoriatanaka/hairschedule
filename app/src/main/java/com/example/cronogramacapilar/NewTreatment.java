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
        setSpinnerValues();
        configureRecurrenceFields();
        setCalendars();
    }

    private void setSpinnerValues() {

        // Get reference of widgets from XML layout
        treatment = findViewById(R.id.treatment_type);

        String[] treatmentsArray = getResources().getStringArray(R.array.treatments_array);
        List<String> treatmentsArrayList = Arrays.asList(treatmentsArray);

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, treatmentsArrayList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        treatment.setAdapter(spinnerArrayAdapter);

        treatment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCalendars() {
        final Calendar myCalendar = Calendar.getInstance();

        lastDate = findViewById(R.id.last_date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lastDate.setError(null);
                updateLabel(myCalendar);
            }

        };

        lastDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NewTreatment.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(Calendar myCalendar) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        lastDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void configureRecurrenceFields() {
        numberOfRepeats = findViewById(R.id.number_of_repeats);
        unitOfRepeats = findViewById(R.id.unit_of_repeats);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(NewTreatment.this,
                R.array.time_singular_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitOfRepeats.setAdapter(adapter);

        numberOfRepeats.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    return;
                ArrayAdapter<CharSequence> adapter = null;
                if (Integer.parseInt(s.toString()) > 1) {
                    adapter = ArrayAdapter.createFromResource(NewTreatment.this,
                            R.array.time_plural_array, android.R.layout.simple_spinner_item);
                } else {
                    adapter = ArrayAdapter.createFromResource(NewTreatment.this,
                            R.array.time_singular_array, android.R.layout.simple_spinner_item);
                }
                String previous = unitOfRepeats.getSelectedItem().toString();
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unitOfRepeats.setAdapter(adapter);
                updateUnitOfRepeatsValue(previous);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1 && s.toString().equals("0"))
                    numberOfRepeats.setText("");
            }
        });
    }

    private void updateUnitOfRepeatsValue(String previous) {
        switch (previous.charAt(0)) {
            case 'd':
                unitOfRepeats.setSelection(0);
                break;
            case 's':
                unitOfRepeats.setSelection(1);
                break;
            case 'm':
                unitOfRepeats.setSelection(2);
                break;
            case 'a':
                unitOfRepeats.setSelection(3);
                break;
            default:
                Log.e("updateUnitOfRepeatsVal", "Invalid value for unit of repeats: " + previous);
        }
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
