package com.example.clientledgerpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_project extends AppCompatActivity {


    private Button client;
    private Button transaction;
    private Button addProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);

        client=findViewById(R.id.button_clients);
        transaction=findViewById(R.id.button_transactions);
        addProject=findViewById(R.id.addproject);


        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent client=new Intent(activity_project.this, activity_client.class);
                startActivity(client);
            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transaction=new Intent(activity_project.this, Transaction.class);
                startActivity(transaction);
            }
        });

        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addproject=new Intent(activity_project.this, AddProject.class);
                startActivity(addproject);
            }
        });
    }
}