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

public class Transaction extends AppCompatActivity {

    private Button addTransaction;
    private Button canceltransac;
    private Button project;
    private Button client;
    private Button transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);


        addTransaction=findViewById(R.id.add_transaction_button);
        addTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addtrans=new Intent(Transaction.this, AddTransaction.class);
                startActivity(addtrans);
            }
        });

        project=findViewById(R.id.button_projects);
        client=findViewById(R.id.button_clients);
        transaction=findViewById(R.id.button_transactions);

        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent project=new Intent(Transaction.this, activity_project.class);
                startActivity(project);
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent client=new Intent(Transaction.this, activity_client.class);
                startActivity(client);
            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent transaction=new Intent(Transaction.this, Transaction.class);
//                startActivity(transaction);
            }
        });
    }
}