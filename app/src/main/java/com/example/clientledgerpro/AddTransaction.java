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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddTransaction extends AppCompatActivity {

    private Button addbutton;
    private Button cancelbutton;
    private FirebaseFirestore db;
    private Spinner projectNameSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_transaction);

//        EdgeToEdge.enable(this);
        db = FirebaseFirestore.getInstance();


        db.collection("project_details").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> spinnerItems = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        spinnerItems.add(document.getString("project_name"));
                    }

                    //get the spinner from the xml.
                    projectNameSpinner = findViewById(R.id.spinnerClientSelect);

                    String[] items = new String[]{"1", "2", "three"};

                    //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                    //There are multiple variations of this, but this is the basic variant.
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTransaction.this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
                    //set the spinners adapter to the previously created one.
                    projectNameSpinner.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        addbutton=findViewById(R.id.addButton);
        cancelbutton=findViewById(R.id.canButton);

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendToAddTransactions=new Intent(AddTransaction.this, Transaction.class);
                startActivity(sendToAddTransactions);
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