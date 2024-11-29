package com.example.clientledgerpro;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction extends AppCompatActivity {

    private Button addTransaction;
    private Button project;
    private Button client;
    private Button transaction;
    private FirebaseFirestore db;
    private LinearLayout parentLayout;
    private TextView totalDues;
    private ImageView deleteIcon;
    private float quotationSum;
    private float transactionSum;
    private float TotalDues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_transaction);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        addTransaction=findViewById(R.id.add_transaction_button);



        addTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addtrans=new Intent(Transaction.this, AddTransaction.class);
                startActivity(addtrans);
                finish();
            }
        });

        totalDues = findViewById(R.id.total_dues_amount);
        project=findViewById(R.id.button_projects);
        client=findViewById(R.id.button_clients);
        transaction=findViewById(R.id.button_transactions);
        parentLayout = findViewById(R.id.transactions_container);

        db.collection("transaction_details").orderBy("created_at", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String amount = document.getString("amount");
                    transactionSum += Integer.parseInt(amount);
                    String projectName = document.getString("project_name");
                    Timestamp date_time = document.getTimestamp("created_at");

                    Date date =  date_time.toDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy ", Locale.getDefault());
                    SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String formattedDate = sdf.format(date);
                    String formattedTime = sdf.format(date);

                    Log.d("Date", "Date: " + formattedDate);;


                    db.collection("project_details").whereEqualTo("project_name", projectName).get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for(QueryDocumentSnapshot document1 : task1.getResult()){
                                String clientName = document1.getString("client_name");
                                Log.d(TAG, "Client Name: " + clientName);

                                // Inflate the item layout
                                View itemView = LayoutInflater.from(Transaction.this)
                                        .inflate(R.layout.item_transaction, parentLayout, false);

                                // Populate data into the views
                                deleteIcon = itemView.findViewById(R.id.icon_transaction);
                                TextView transactionDateTextView = itemView.findViewById(R.id.transaction_date);
                                TextView clientNameTextView = itemView.findViewById(R.id.transaction_name);
                                TextView transactionAmountTextView = itemView.findViewById(R.id.transaction_amount);

                                transactionDateTextView.setText(formattedDate != null ? formattedDate : "Unknown detail");
                                clientNameTextView.setText(clientName != null ? clientName : "No Details Available");
                                transactionAmountTextView.setText("₹"+amount);

                                deleteIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDeleteConfirmationDialog(amount , projectName);
                                    }
                                });

                                // Add the inflated view to the parent layout
                                parentLayout.addView(itemView);

                            }
                        }
                    });


                }
                db.collection("project_details").get().addOnCompleteListener(task2 -> {
                    if(task2.isSuccessful()){
                        for(QueryDocumentSnapshot document1 : task2.getResult()){
                            String estimatedQuotation = document1.getString("estimated_quotation");
                            quotationSum += Integer.parseInt(estimatedQuotation);
                            String aAmount = document1.getString("advance_amount");
                            transactionSum += Integer.parseInt(aAmount);
                        }
                        TotalDues = quotationSum - transactionSum;
                        totalDues.setText("₹"+TotalDues);
                        if(TotalDues<0){
                            totalDues.setTextColor(Color.parseColor("#FF0000"));
                        }else{
                            totalDues.setTextColor(Color.parseColor("#00FF00"));
                        }
                    }else{
                        Log.e(TAG, "Error fetching Firestore data: ", task2.getException());
                    }
                });
//                String TotalDues = String.valueOf(quotationSum - transactionSum);

            } else {
                Log.e(TAG, "Error fetching Firestore data: ", task.getException());
            }
        });

        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent project=new Intent(Transaction.this, activity_project.class);
                startActivity(project);
                finish();
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent client=new Intent(Transaction.this, activity_client.class);
                startActivity(client);
                finish();
            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent transaction=new Intent(Transaction.this, Transaction.class);
//                startActivity(transaction);
//                finish();
            }
        });
    }

    private void showDeleteConfirmationDialog(String amount , String project_name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete transaction with amount: "+ amount +" for Project "+project_name)
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform the deletion (e.g., delete from Firestore)
                    deleteRecord(amount, project_name);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void deleteRecord(String amount,String project_name) {
        // Logic to delete the record from Firestore or wherever your data is stored
        Query query = db.collection("transaction_details")
                .whereEqualTo("amount", amount)
                .whereEqualTo("project_name", project_name);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Check if documents matching the query exist
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Loop through all documents (there should be only one if project_name is unique)
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    // Get the document reference to delete
                                    DocumentReference documentReference = db.collection("transaction_details").document(document.getId());

                                    // Delete the document
                                    documentReference.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Successfully deleted the document
                                                        Log.d("Delete Document", "Document successfully deleted!");
                                                        Toast.makeText(Transaction.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Failed to delete the document
                                                        Log.e("Delete Document", "Error deleting document", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                // No document found with the project_name
                                Log.d("Delete Document", "No document found with project_name: " + project_name);
                            }
                        } else {
                            // Query failed
                            Log.e("Delete Document", "Error getting documents: ", task.getException());
                        }
                    }
                });


        try {
            Thread.sleep(1000);
        }catch(Exception e){}
        recreate();
    }
}