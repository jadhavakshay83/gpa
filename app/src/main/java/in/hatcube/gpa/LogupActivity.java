package in.hatcube.gpa;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LogupActivity extends AppCompatActivity {

    Spinner technique;
    Button addGPA;
    TextInputEditText name, email, phone, pass, confPass;
    String selectedTechnique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logup);

        // Initialize elements
        name        = findViewById(R.id.name);
        email       = findViewById(R.id.email);
        phone       = findViewById(R.id.phone);
        pass        = findViewById(R.id.pass);
        confPass    = findViewById(R.id.confPass);
        technique   = findViewById(R.id.technique);
        addGPA      = findViewById(R.id.addGPA);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add(Constants.PVS);
        categories.add(Constants.CPS);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        technique.setAdapter(dataAdapter);

        // Spinner click listener
        technique.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // On selecting a spinner item
                selectedTechnique = adapterView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addGPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check pass and confPass are same
                if(pass.getText().toString().equals(confPass.getText().toString())) {
                    if(selectedTechnique.equals(Constants.PVS)) {
                        Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                        startActivity(intent);
                    } else if(selectedTechnique.equals(Constants.CPS)) {
                        Intent intent = new Intent(getApplicationContext(), ClickimgActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Password and Confirm password do not match!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
