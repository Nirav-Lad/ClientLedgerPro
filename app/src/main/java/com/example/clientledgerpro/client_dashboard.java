package com.example.clientledgerpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class client_dashboard extends AppCompatActivity {

    private Button project;
    private Button client;
    private Button transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_dashboard);
        project=findViewById(R.id.button_projects);
        client=findViewById(R.id.button_clients);
        transaction=findViewById(R.id.button_transactions);

        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent project=new Intent(client_dashboard.this, activity_project.class);
                startActivity(project);
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent client=new Intent(client_dashboard.this, activity_client.class);
                startActivity(client);
            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transaction=new Intent(client_dashboard.this, Transaction.class);
                startActivity(transaction);
            }
        });
    }


    public void onBackPressed() {
        super.onBackPressed(); // Call the superclass's onBackPressed method
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.onBackPressed(); // You can add custom logic before calling this
        finish(); // Close the activity or app
    }
}