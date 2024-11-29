package com.example.clientledgerpro;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        project = findViewById(R.id.button_projects);
        client = findViewById(R.id.button_clients);
        transaction = findViewById(R.id.button_transactions);
        addClient = findViewById(R.id.add_client_button);
        parentLayout = findViewById(R.id.client_container);

        db.collection("client_details").orderBy("client_name").get().addOnCompleteListener(task -> {
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
                    deleteIcon.setOnClickListener(v -> showDeleteConfirmationDialog(clientName));

                    // Add the inflated view to the parent layout
                    parentLayout.addView(itemView);
                }
            } else {
                Log.e("Error", "Error fetching Firestore data: ", task.getException());
            }
        });

        project.setOnClickListener(v -> {
            Intent projectIntent = new Intent(activity_client.this, activity_project.class);
            startActivity(projectIntent);
            finish();
        });

        transaction.setOnClickListener(v -> {
            Intent transactionIntent = new Intent(activity_client.this, Transaction.class);
            startActivity(transactionIntent);
            finish();
        });

        addClient.setOnClickListener(v -> {
            Intent addClientIntent = new Intent(activity_client.this, AddClient.class);
            startActivity(addClientIntent);
            finish();
        });
    }

    private void showDeleteConfirmationDialog(String client_name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete the client " + client_name + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteRecord(client_name))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteRecord(String client_name) {
        // Start deleting client record
        Query query = db.collection("client_details").whereEqualTo("client_name", client_name);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        DocumentReference clientDocRef = db.collection("client_details").document(document.getId());

                        // Start deleting projects related to the client
                        deleteClientAndProjects(client_name, clientDocRef);
                    }
                } else {
                    Log.d("Delete Document", "No document found with client_name: " + client_name);
                }
            } else {
                Log.e("Delete Document", "Error getting client documents: ", task.getException());
            }
        });
    }

    private void deleteClientAndProjects(String client_name, DocumentReference clientDocRef) {
        clientDocRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Delete Client", "Client successfully deleted!");

                // Now delete all projects related to the client
                Query query = db.collection("project_details").whereEqualTo("client_name", client_name);
                query.get().addOnCompleteListener(projectTask -> {
                    if (projectTask.isSuccessful()) {
                        QuerySnapshot projectSnapshot = projectTask.getResult();
                        if (projectSnapshot != null && !projectSnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot projectDoc : projectSnapshot) {
                                String project_name = projectDoc.getString("project_name");
                                DocumentReference projectDocRef = db.collection("project_details").document(projectDoc.getId());

                                // Start deleting transactions related to the project
                                deleteProjectAndTransactions(project_name, projectDocRef);
                            }
                        }
                    } else {
                        Log.e("Delete Projects", "Error getting project documents: ", projectTask.getException());
                    }
                });
            } else {
                Log.e("Delete Client", "Error deleting client document", task.getException());
            }
        });
    }

    private void deleteProjectAndTransactions(String project_name, DocumentReference projectDocRef) {
        projectDocRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Delete Project", "Project successfully deleted!");

                // Now delete all transactions related to the project
                Query query = db.collection("transaction_details").whereEqualTo("project_name", project_name);
                query.get().addOnCompleteListener(transactionTask -> {
                    if (transactionTask.isSuccessful()) {
                        QuerySnapshot transactionSnapshot = transactionTask.getResult();
                        if (transactionSnapshot != null && !transactionSnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot transactionDoc : transactionSnapshot) {
                                DocumentReference transactionDocRef = db.collection("transaction_details").document(transactionDoc.getId());
                                transactionDocRef.delete().addOnCompleteListener(deleteTransactionTask -> {
                                    if (deleteTransactionTask.isSuccessful()) {
                                        Toast.makeText(activity_client.this, "Document Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        Log.d("Delete Transaction", "Transaction successfully deleted.");
                                        recreate();
                                    } else {
                                        Log.e("Delete Transaction", "Error deleting transaction document", deleteTransactionTask.getException());
                                    }
                                });
                            }
                        }
                    } else {
                        Log.e("Delete Transactions", "Error getting transaction documents: ", transactionTask.getException());
                    }
                });
            } else {
                Log.e("Delete Project", "Error deleting project document", task.getException());
            }
        });
    }
}
