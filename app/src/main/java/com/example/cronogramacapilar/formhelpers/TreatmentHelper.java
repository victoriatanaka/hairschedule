package com.example.cronogramacapilar.formhelpers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.cronogramacapilar.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TreatmentHelper {
    private Context context;

    public TreatmentHelper(Context context) {
        this.context = context;
    }

    public void setSpinnerValues(Spinner treatment) {

        // Get reference of widgets from XML layout

        String[] treatmentsArray = context.getResources().getStringArray(R.array.treatments_array);
        List<String> treatmentsArrayList = Arrays.asList(treatmentsArray);

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context, R.layout.spinner_item, treatmentsArrayList) {
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
                            (context, "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setCalendars(final EditText lastDate) {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lastDate.setError(null);
                updateLabel(myCalendar, lastDate);
            }

        };

        lastDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(Calendar myCalendar, EditText lastDate) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        lastDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void configureRecurrenceFields(final EditText numberOfRepeats, final Spinner unitOfRepeats) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
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
                    adapter = ArrayAdapter.createFromResource(context,
                            R.array.time_plural_array, android.R.layout.simple_spinner_item);
                } else {
                    adapter = ArrayAdapter.createFromResource(context,
                            R.array.time_singular_array, android.R.layout.simple_spinner_item);
                }
                String previous = unitOfRepeats.getSelectedItem().toString();
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unitOfRepeats.setAdapter(adapter);
                updateUnitOfRepeatsValue(previous, unitOfRepeats);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1 && s.toString().equals("0"))
                    numberOfRepeats.setText("");
            }
        });
    }

    private void updateUnitOfRepeatsValue(String previous, Spinner unitOfRepeats) {
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
}
