package com.example.clientledgerpro;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTransaction extends AppCompatActivity {

    private Button addbutton;
    private Button cancelbutton;
    private FirebaseFirestore db;
    private Spinner projectNameSpinner;
    private EditText amountField;
    private Spinner paymentTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_transaction);

//        EdgeToEdge.enable(this);
        db = FirebaseFirestore.getInstance();

        db.collection("project_details").orderBy("project_name").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> projectNames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String projectName = document.getString("project_name");
                            projectNames.add(projectName);
                        }
                        projectNameSpinner = findViewById(R.id.ProjectNameSpinner);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTransaction.this, android.R.layout.simple_spinner_dropdown_item, projectNames);

                        projectNameSpinner.setAdapter(adapter);

                    } else {
                        Log.d("Query Execution Project Fetch", "Error getting documents: ", task.getException());
                    }
                });

        addbutton=findViewById(R.id.addButton);
        cancelbutton=findViewById(R.id.canButton);
        amountField = findViewById(R.id.amountField);
        paymentTypeSpinner = findViewById(R.id.paymentTypeSpinner);

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String projectName = projectNameSpinner.getSelectedItem().toString();
                String amount = amountField.getText().toString();
                String paymentType = paymentTypeSpinner.getSelectedItem().toString();

                Map<String, Object> transaction_data = new HashMap<>();
                transaction_data.put("project_name", projectName);
                transaction_data.put("amount", amount);
                transaction_data.put("payment_type", paymentType);
                transaction_data.put("created_at", FieldValue.serverTimestamp());

                db.collection("transaction_details")
                        .add(transaction_data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                System.out.println("Data added successfully");
                                Toast.makeText(AddTransaction.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddTransaction.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                            }
                        });


            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}