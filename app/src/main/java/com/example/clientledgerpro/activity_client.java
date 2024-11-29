package com.example.clientledgerpro;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.WindowInsetsCompat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class activity_client extends AppCompatActivity {

    private Button project;
    private Button client;
    private Button transaction;
    private Button addClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private LinearLayout parentLayout;
    private ImageView deleteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client);


        project=findViewById(R.id.button_projects);
        client=findViewById(R.id.button_clients);
        transaction=findViewById(R.id.button_transactions);
        addClient=findViewById(R.id.add_client_button);
        parentLayout = findViewById(R.id.client_container);

        db.collection("client_details").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String clientName = document.getString("client_name");
                    String contact_no = document.getString("phone_number");

                    // Inflate the item layout
                    View itemView = LayoutInflater.from(activity_client.this)
                            .inflate(R.layout.item_client, parentLayout, false);

                    // Populate data into the views
                    TextView clientNameTextView = itemView.findViewById(R.id.client_name);
                    TextView transactionNameTextView = itemView.findViewById(R.id.contact_no);
                    ImageView deleteIcon = itemView.findViewById(R.id.icon_transaction);

                    clientNameTextView.setText(clientName != null ? clientName : "Unknown Client");
                    transactionNameTextView.setText(contact_no != null ? contact_no : "No Details Available");
                    deleteIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDeleteConfirmationDialog(clientName);
                        }});

                    // Add the inflated view to the parent layout
                    parentLayout.addView(itemView);
                }
            } else {
                Log.e(TAG, "Error fetching Firestore data: ", task.getException());
            }
        });

        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent project=new Intent(activity_client.this, activity_project.class);
                startActivity(project);
                finish();
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent client=new Intent(activity_client.this, activity_client.class);
//                startActivity(client);
            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transaction=new Intent(activity_client.this, Transaction.class);
                startActivity(transaction);
                finish();
            }
        });

        addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addclient=new Intent(activity_client.this, AddClient.class);
                startActivity(addclient);
                finish();
            }
        });
    }

    private void showDeleteConfirmationDialog(String client_name){
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete "+ client_name +" Client?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform the deletion (e.g., delete from Firestore)
                    deleteRecord(client_name);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void deleteRecord(String client_name) {
        // Logic to delete the record from Firestore or wherever your data is stored
        Query query = db.collection("client_details")
                .whereEqualTo("client_name", client_name);

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
                                    DocumentReference documentReference = db.collection("client_details").document(document.getId());

                                    // Delete the document
                                    documentReference.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Successfully deleted the document
                                                        Log.d("Delete Document", "Document successfully deleted!");
                                                        Toast.makeText(activity_client.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Failed to delete the document
                                                        Log.e("Delete Document", "Error deleting document", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                // No document found with the project_name
                                Log.d("Delete Document", "No document found with project_name: " + client_name);
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