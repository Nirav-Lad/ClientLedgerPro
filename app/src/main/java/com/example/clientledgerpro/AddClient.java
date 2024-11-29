package com.example.clientledgerpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddClient extends AppCompatActivity {

    private Button addClient;
    private EditText clientName,address,phoneNumber;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_add_client);

        addClient=findViewById(R.id.submitButton);
        clientName=findViewById(R.id.clientNameField);
        address=findViewById(R.id.projectNameField);
        phoneNumber=findViewById(R.id.phoneNumberField);

        addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientNameText=clientName.getText().toString();
                String addressText=address.getText().toString();
                String phoneNumberText=phoneNumber.getText().toString();
                phoneNumberText = phoneNumberText.trim();
                if (phoneNumberText.length()!=10){
                    Toast.makeText(AddClient.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                }
                else {

                    Map<String, Object> client_data = new HashMap<>();
                    client_data.put("client_name", clientNameText);
                    client_data.put("address", addressText);
                    client_data.put("phone_number", phoneNumberText);
                    client_data.put("created_at", FieldValue.serverTimestamp());

                    db.collection("client_details")
                            .add(client_data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    System.out.println("Data added successfully");
                                    Toast.makeText(AddClient.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    Intent intent = new Intent(AddClient.this, client_dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddClient.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                }
                            });

                }


            }
        });
    }
}