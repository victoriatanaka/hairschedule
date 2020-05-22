package com.example.cronogramacapilar.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cronogramacapilar.R;
import com.example.cronogramacapilar.Treatment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TreatmentFormHelper {
    private final Context context;

    public TreatmentFormHelper(Context context) {
        this.context = context;
    }

    public void setTreatmentSpinner(Spinner treatmentField) {
        setSpinnerValues(treatmentField);
        setSpinnerWithEditText(treatmentField);
    }

    private void setSpinnerValues(Spinner treatmentSpinner) {

        // Get reference of widgets from XML layout

        String[] treatmentsArray = context.getResources().getStringArray(R.array.treatments_array);
        List<String> treatmentsArrayList = Arrays.asList(treatmentsArray);

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context, R.layout.spinner_item_treatment, treatmentsArrayList) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
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
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_treatment);
        treatmentSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void setSpinnerWithEditText(Spinner treatmentSpinner) {
        treatmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals(parent.getResources().getString(R.string.other_treatment))) {
                    view.getRootView().findViewById(R.id.other_treatment).setVisibility(View.VISIBLE);
                } else
                    view.getRootView().findViewById(R.id.other_treatment).setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setOtherTreatmentFilter(final EditText otherTreatmentField) {
        otherTreatmentField.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else if (c == '\n') {
                        otherTreatmentField.clearFocus();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(imm).hideSoftInputFromWindow(otherTreatmentField.getWindowToken(), 0);
                        keepOriginal = false;
                    } else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                Pattern ps = Pattern.compile("^[\\p{L}\\d\\t ]+$");
                Matcher ms = ps.matcher(String.valueOf(c));
                return ms.matches();
            }
        }});
    }

    public void setSpinnerLocked(Spinner treatmentSpinner, String treatmentType) {

        List<String> treatmentsArrayList = Collections.singletonList(treatmentType);

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_item_disabled, treatmentsArrayList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_disabled);

        treatmentSpinner.setAdapter(spinnerArrayAdapter);

        treatmentSpinner.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showTipSpinnerLocked();
                }
                return true;
            }
        });
        treatmentSpinner.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    showTipSpinnerLocked();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void showTipSpinnerLocked() {
        Toast.makeText(context, "Não é possível editar este campo.",
                Toast.LENGTH_LONG).show();
    }

    public Calendar setCalendars(final EditText lastDate) {
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

        return myCalendar;
    }

    public void setCalendars(final EditText lastDate, Date initialDate) {
        Calendar myCalendar = setCalendars(lastDate);
        myCalendar.setTime(initialDate);
        updateLabel(myCalendar, lastDate);
    }

    private void updateLabel(Calendar myCalendar, EditText lastDate) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        String dateFormatted = sdf.format(myCalendar.getTime());
        lastDate.setText(dateFormatted);
        int height = lastDate.getHeight();
        lastDate.setWidth(dpToPx(130, context));
        lastDate.setHeight(height);
        lastDate.setBackgroundResource(R.color.spinner);
        lastDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lastDate.setTextAppearance(android.R.style.TextAppearance_Holo_Medium);
        }
    }

    private static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    public void configureRecurrenceFields(final EditText numberOfRepeats, final Spinner unitOfRepeats) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.time_singular_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        unitOfRepeats.setAdapter(adapter);

        numberOfRepeats.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    return;
                ArrayAdapter<CharSequence> adapter;
                if (Integer.parseInt(s.toString()) > 1) {
                    adapter = ArrayAdapter.createFromResource(context,
                            R.array.time_plural_array, R.layout.spinner_item);
                } else {
                    adapter = ArrayAdapter.createFromResource(context,
                            R.array.time_singular_array, R.layout.spinner_item);
                }
                String previous = unitOfRepeats.getSelectedItem().toString();
                adapter.setDropDownViewResource(R.layout.spinner_item);
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

    public void updateUnitOfRepeatsValue(String previous, Spinner unitOfRepeats) {
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

    public void saveTreatment(Spinner treatmentField, EditText numberOfRepeatsField, Spinner unitOfRepeatsField, EditText lastDateField, String observations, long id) {
        Treatment treatment = constructTreatment(treatmentField, numberOfRepeatsField, unitOfRepeatsField, lastDateField);
        if (treatment != null) {
            treatment.observations = observations;
            treatment.id = id;
            new TreatmentDaoAsync.SaveTreatmentAsync(
                    new Callable<Void>() {
                        public Void call() {
                            ((Activity) context).finish();
                            return null;
                        }
                    }, treatment).execute();
        }
    }

    public void saveNewTreatment(Spinner treatmentField, EditText numberOfRepeatsField, Spinner unitOfRepeatsField, EditText lastDateField, String observations) {
        Treatment treatment = constructTreatment(treatmentField, numberOfRepeatsField, unitOfRepeatsField, lastDateField);
        if (treatment != null) {
            new TreatmentDaoAsync.CreateTreatmentAsync(
                    new Callable<Void>() {
                        public Void call() {
                            ((Activity) context).finish();
                            return null;
                        }
                    }, treatment.type, treatment.lastDate, treatment.nextDate, treatment.repeatsUnit, treatment.repeats, observations).execute();
        }
    }

    private Treatment constructTreatment(Spinner treatmentField, EditText numberOfRepeatsField, Spinner unitOfRepeatsField, EditText lastDateField) {
        boolean canSave = true;
        Treatment treatment = new Treatment();

        // check if type was chosen
        treatment.type = treatmentField.getSelectedItem().toString();
        if (treatment.type.equals(context.getResources().getString(R.string.tipo_de_tratamento))) {
            TextView errorText = (TextView) treatmentField.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(R.string.error_treatment_empty);
            treatmentField.requestFocus();
            canSave = false;
        }

        // check if other type is selected; then treatment type will be whatever the user entered in other treatment field
        else if (treatment.type.equals(context.getResources().getString(R.string.other_treatment))) {
            EditText otherTreatmentField = ((Activity) context).findViewById(R.id.other_treatment);
            String otherTreatment = otherTreatmentField.getText().toString();
            if (otherTreatment.isEmpty()) {
                otherTreatmentField.setError(context.getString(R.string.error_other_treatment_empty));
                otherTreatmentField.requestFocus();
                canSave = false;
            }
            treatment.type = otherTreatment;
        }

        // check if repeats is correct
        treatment.repeats = Integer.parseInt(numberOfRepeatsField.getText().toString());
        if (treatment.repeats <= 0) {
            numberOfRepeatsField.setError(context.getString(R.string.error_number_of_repeats));
            numberOfRepeatsField.requestFocus();
            canSave = false;
        }

        // calculates next date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        treatment.repeatsUnit = unitOfRepeatsField.getSelectedItem().toString();

        try {
            treatment.lastDate = formatter.parse(lastDateField.getText().toString());
            treatment.nextDate = Treatment.calculateNextDate(treatment.lastDate, treatment.repeats, treatment.repeatsUnit);

        } catch (ParseException e) {
            e.printStackTrace();
            lastDateField.setHintTextColor(Color.RED);
            lastDateField.setHint(R.string.error_last_date);
            lastDateField.setError("");
            canSave = false;
        }

        if (canSave) {
            return treatment;
        }
        return null;
    }
}
