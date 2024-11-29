package com.example.clientledgerpro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class client_dashboard extends AppCompatActivity {

    private Button project;
    private Button client;
    private Button transaction;
    private FirebaseFirestore db;
    private TextView clientCount,projectCount;
    private float quotationSum;
    private float transactionSum;
    private TextView totalDues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_dashboard);
        totalDues = findViewById(R.id.total_dues_amount);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        project=findViewById(R.id.button_projects);
        client=findViewById(R.id.button_clients);
        transaction=findViewById(R.id.button_transactions);
        db=FirebaseFirestore.getInstance();
        clientCount=findViewById(R.id.clientCountTextView);
        projectCount = findViewById(R.id.projectCount);

        db.collection("client_details").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot querySnapshot = task.getResult();
                String totalClients = "" + querySnapshot.size();
                if(totalClients.length()==1){
                    totalClients="0"+totalClients;
                }
                clientCount.setText(totalClients);
            }
                });

        db.collection("project_details").get().addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                QuerySnapshot querySnapshot = task.getResult();
                String totalProjects = "" + querySnapshot.size();
                if(totalProjects.length()==1){
                    totalProjects="0"+totalProjects;
                }
                projectCount.setText(totalProjects);
                for(QueryDocumentSnapshot document_iterate :  task.getResult()){
                    String quote_amount =  document_iterate.getString("estimated_quotation");
                    String advance_amount = document_iterate.getString("advance_amount");
                    quotationSum += Float.parseFloat(quote_amount);
                    transactionSum += Float.parseFloat(advance_amount);
                }
                db.collection("transaction_details").get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        for(QueryDocumentSnapshot document3 : task2.getResult()){
                            String amount = document3.getString("amount");
                            transactionSum += Float.parseFloat(amount);
                        }
                        float TotalDues = quotationSum - transactionSum;
                        totalDues.setText("₹"+TotalDues);
                        if(TotalDues<0){
                            totalDues.setTextColor(Color.parseColor("#FF0000"));
                        }else{
                            totalDues.setTextColor(Color.parseColor("#00FF00"));
                        }
                    }
                });
            }
        });



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