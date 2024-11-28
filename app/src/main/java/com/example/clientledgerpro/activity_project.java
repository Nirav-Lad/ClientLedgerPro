package com.example.clientledgerpro;

import static android.content.ContentValues.TAG;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class activity_project extends AppCompatActivity {


    private Button client;
    private Button transaction;
    private Button addProject;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();;
    private LinearLayout parentLayout;
    private ImageView deleteIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);


        client=findViewById(R.id.button_clients);
        transaction=findViewById(R.id.button_transactions);
        addProject=findViewById(R.id.addproject);
        parentLayout = findViewById(R.id.projects_container);

        db.collection("project_details").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String projectName = document.getString("project_name");
                    String clientName = document.getString("client_name");

                    // Inflate the item layout
                    View itemView = LayoutInflater.from(activity_project.this)
                            .inflate(R.layout.item_project, parentLayout, false);

                    // Populate data into the views
                    deleteIcon = itemView.findViewById(R.id.icon_transaction);
                    TextView projectNameTextView = itemView.findViewById(R.id.projectNameField);
                    TextView clientNameTextView = itemView.findViewById(R.id.client_name);

                    projectNameTextView.setText(projectName != null ? projectName : "Unknown Client");
                    clientNameTextView.setText(clientName != null ? clientName : "No Details Available");
                    deleteIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDeleteConfirmationDialog(projectName);
                        }
                    });

                    // Add the inflated view to the parent layout
                    parentLayout.addView(itemView);
                }
            } else {
                Log.e(TAG, "Error fetching Firestore data: ", task.getException());
            }
        });


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
                finish();
            }
        });
    }

    private void showDeleteConfirmationDialog(String project_name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete "+ project_name +" Project?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform the deletion (e.g., delete from Firestore)
                    deleteRecord(project_name);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void deleteRecord(String project_name) {
        // Logic to delete the record from Firestore or wherever your data is stored
        Query query = db.collection("project_details")
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
                                    DocumentReference documentReference = db.collection("project_details").document(document.getId());

                                    // Delete the document
                                    documentReference.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Successfully deleted the document
                                                        Log.d("Delete Document", "Document successfully deleted!");
                                                        Toast.makeText(activity_project.this, "Record Deleted", Toast.LENGTH_SHORT).show();
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