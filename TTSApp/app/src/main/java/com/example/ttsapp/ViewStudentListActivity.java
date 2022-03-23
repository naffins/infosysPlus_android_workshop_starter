package com.example.ttsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class ViewStudentListActivity extends AppCompatActivity {

    // Class-specific constants for Spinner checking
    private final static String vacSpinnerDefault = "Fully vaccinated";
    private final static String undergradSpinnerDefault = "Undergraduate";

    // Toast messages
    private final static String FILTER_COMPILE_ERROR = "Error: could not compile JSON search filter";
    private final static String LIST_NOT_IMPLEMENTED_TEMP_BASE = "List function not implemented; attempted to list students with filter";
    private final static String MOCK_JSON_COMPILE_ERROR_TEMP = "Error: failed to compile mock JSON string";

    // Variables for interactive components
    private EditText nameEditText, idEditText, yearEditText;
    private CheckBox nameCheckBox, idCheckBox, yearCheckBox, vacCheckBox, undergradCheckBox;
    private Spinner vacSpinner, undergradSpinner;
    private Button loadButton, returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_list);

        // Set variables for components
        nameEditText = findViewById(R.id.activity_view_student_list_name_edittext);
        idEditText = findViewById(R.id.activity_view_student_list_id_edittext);
        yearEditText = findViewById(R.id.activity_view_student_list_year_edittext);

        nameCheckBox = findViewById(R.id.activity_view_student_list_name_checkbox);
        idCheckBox = findViewById(R.id.activity_view_student_list_id_checkbox);
        yearCheckBox = findViewById(R.id.activity_view_student_list_year_checkbox);
        vacCheckBox = findViewById(R.id.activity_view_student_list_vac_checkbox);
        undergradCheckBox = findViewById(R.id.activity_view_student_list_undergrad_checkbox);

        vacSpinner = findViewById(R.id.activity_view_student_list_vac_spinner);
        undergradSpinner = findViewById(R.id.activity_view_student_list_undergrad_spinner);

        loadButton = findViewById(R.id.activity_view_student_list_load_button);
        returnButton = findViewById(R.id.activity_view_student_list_return_button);

        // Load Spinner values and set spinner layouts
        ArrayAdapter<CharSequence> vacSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.activity_view_student_list_vac_spinner_values,android.R.layout.simple_spinner_item);
        vacSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vacSpinner.setAdapter(vacSpinnerAdapter);

        ArrayAdapter<CharSequence> undergradSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.activity_view_student_list_undergrad_spinner_values,android.R.layout.simple_spinner_item);
        undergradSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        undergradSpinner.setAdapter(undergradSpinnerAdapter);

        // Set listeners for buttons
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validate inputs
                if (!validateFields()) return;

                // Get filter JSON
                String filter = compileFilter();
                if (filter==null) {
                    Toast.makeText(getApplicationContext(),FILTER_COMPILE_ERROR,Toast.LENGTH_SHORT);
                    return;
                }

                // TODO: Add functionality to retrieve user list
                // (1) Make POST request, sending JSON filter as content
                // (2) Retrieve JSON input, which is a JSONArray of JSONObjects with fields "id", "year",
                //  "is_undergraduate", "is_vaccinated"
                // (3) Start ViewStudentListActivity, passing the received JSONArray

                // TODO: Remove this filler code
                // -----START OF CODE TO BE REMOVED-----

                // Show Toast indicating that function is not implemented
                String toastMessage = LIST_NOT_IMPLEMENTED_TEMP_BASE + "\n"
                        + filter + "\n"
                        + "Note that filter is not applied here";
                Toast.makeText(getApplicationContext(),toastMessage,Toast.LENGTH_LONG).show();

                String listData = "[]";

                // Create mock JSON list data
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject a, b;
                    a = new JSONObject();
                    a.put("name","Person A");
                    a.put("id",1001111);
                    a.put("year",2022);
                    a.put("is_vaccinated",true);
                    a.put("is_undergraduate",false);
                    b = new JSONObject();
                    b.put("name","Person B");
                    b.put("id",1002222);
                    b.put("year",2021);
                    b.put("is_vaccinated",false);
                    b.put("is_undergraduate",true);
                    jsonArray.put(a);
                    jsonArray.put(b);
                    listData = jsonArray.toString();
                }
                catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),MOCK_JSON_COMPILE_ERROR_TEMP,Toast.LENGTH_SHORT);
                }

                // Start results page activity
                Intent intent = new Intent(ViewStudentListActivity.this,LoadResultActivity.class);
                intent.putExtra("listData",listData);
                startActivity(intent);

                // -----END OF CODE TO BE REMOVED-----
            }
        });

        // Return to main menu
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Validate editText inputs if the corresponding checkboxes are checked
    // Return true only if all selected fields are valid
    private boolean validateFields() {

        if (nameCheckBox.isChecked()) {
            if (!Pattern.matches(CommonValues.NAME_REGEX,nameEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(),CommonValues.INVALID_NAME_ERROR,Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (idCheckBox.isChecked()) {
            if (!Pattern.matches(CommonValues.ID_REGEX,idEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(),CommonValues.INVALID_ID_ERROR,Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (yearCheckBox.isChecked()) {
            if (!Pattern.matches(CommonValues.YEAR_REGEX,yearEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(),CommonValues.INVALID_YEAR_ERROR,Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private String compileFilter() {
        JSONObject filterJson = new JSONObject();
        try {
            if (nameCheckBox.isChecked()) filterJson.put("name",nameEditText.getText().toString());
            if (idCheckBox.isChecked()) filterJson.put("id",Integer.valueOf(idEditText.getText().toString()));
            if (yearCheckBox.isChecked()) filterJson.put("year",Integer.valueOf(yearEditText.getText().toString()));
            if (vacCheckBox.isChecked()) filterJson.put("is_vaccinated",vacSpinner.getSelectedItem().toString().equals(vacSpinnerDefault));
            if (undergradCheckBox.isChecked()) filterJson.put("is_undergraduate",undergradSpinner.getSelectedItem().toString().equals(undergradSpinnerDefault));
        } catch (JSONException e) {
            return null;
        }
        return filterJson.toString();
    }

}