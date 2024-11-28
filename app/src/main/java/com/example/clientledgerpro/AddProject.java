package com.example.clientledgerpro;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProject extends AppCompatActivity {

    private Button addProject;
    private Spinner spinner;
    private FirebaseFirestore db;
    private EditText projectNameText,siteAddressText,estimatedQuotationText,advanceAmountText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_add_project);

        db = FirebaseFirestore.getInstance();
        projectNameText = findViewById(R.id.projectNameField);
        siteAddressText = findViewById(R.id.siteAddressField);
        estimatedQuotationText = findViewById(R.id.estimatedQuotationField);
        advanceAmountText = findViewById(R.id.advanceAmountField);


        db.collection("client_details").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> spinnerItems = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        spinnerItems.add(document.getString("client_name"));
                    }
                    //get the spinner from the xml.
                    spinner = findViewById(R.id.spinnerClientSelect);
                    //create a list of items for the spinner.
                    String[] items = new String[]{"1", "2", "three"};
                    //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                    //There are multiple variations of this, but this is the basic variant.
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProject.this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
                    //set the spinners adapter to the previously created one.
                    spinner.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });







        addProject=findViewById(R.id.submitButton);

        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String projectName=projectNameText.getText().toString();
                String siteAddress=siteAddressText.getText().toString();
                String estimatedQuotation=estimatedQuotationText.getText().toString();
                String advanceAmount=advanceAmountText.getText().toString();
                String clientName=spinner.getSelectedItem().toString();
                Log.d(TAG, "Project Name: " + projectName);
                Log.d(TAG, "Site Address: " + siteAddress);
                Log.d(TAG, "Estimated Quotation: " + estimatedQuotation);
                Log.d(TAG, "Advance Amount: " + advanceAmount);

                if(projectName.isEmpty() || siteAddress.isEmpty() || estimatedQuotation.isEmpty() || advanceAmount.isEmpty()){
                    Toast.makeText(AddProject.this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                } else if (Integer.parseInt(advanceAmount)>Integer.parseInt(estimatedQuotation)) {
                    Toast.makeText(AddProject.this, "Please check the details before submissions", Toast.LENGTH_LONG).show();
                }else{

                Map<String, Object> project_data = new HashMap<>();
                project_data.put("project_name", projectName);
                project_data.put("site_address", siteAddress);
                project_data.put("estimated_quotation", estimatedQuotation);
                project_data.put("advance_amount", advanceAmount);
                project_data.put("client_name", clientName);




                db.collection("project_details").add(project_data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                System.out.println("Data added successfully");
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(AddProject.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                try {
                                    Thread.sleep(1000);
                                    finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddProject.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                            }
                        });
                }

            }
        });

    }
}