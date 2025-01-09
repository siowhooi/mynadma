package com.example.mynadma;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

// CardAdapter.java
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<OfflineDisasterGuideCard> cardItemList;
    private String userId;

    // Constructor
    public CardAdapter(List<OfflineDisasterGuideCard> cardItemList, String userId) {
        this.cardItemList = cardItemList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offline_guide_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        OfflineDisasterGuideCard item = cardItemList.get(position);
        holder.cardTitle.setText(item.getTitle());

        // Set download Button listener
        holder.downloadButton.setOnClickListener(v -> {
            // Create an AlertDialog to confirm the download
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Download")
                    .setMessage("Are you sure you want to download this content?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // User confirmed, proceed with the download
                        startDownload(v.getContext(), item.getDatabaseKey());
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // User canceled, no download happens
                        dialog.dismiss();
                    })
                    .show();
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewOfflineCard = new Intent(v.getContext(), ViewOfflineDisasterGuideActivity.class);
                viewOfflineCard.putExtra("userId", userId);
                viewOfflineCard.putExtra("disasterName", item.getDatabaseKey());
                v.getContext().startActivity(viewOfflineCard);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle, dateDownloaded;
        Button downloadButton, deleteButton;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            downloadButton = itemView.findViewById(R.id.downloadButton);
        }
    }

    private void startDownload(Context context, String disasterName) {
        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("disasterGuide/" + disasterName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if data exists
                if (dataSnapshot.exists()) {
                    // Convert the snapshot into the Disaster object
                    OfflineGuide offlineGuide = dataSnapshot.getValue(OfflineGuide.class);
                    offlineGuide.setKey(disasterName);
                    Gson gson = new Gson();
                    String json = gson.toJson(offlineGuide); // Convert object to JSON

                    String filename = disasterName+".json";
                    // Get external storage directory
                    File file = new File(context.getFilesDir(), filename);

                    try {
                        // Open a file output stream to save in external storage
                        FileOutputStream fos = new FileOutputStream(file);
                        OutputStreamWriter writer = new OutputStreamWriter(fos);
                        writer.write(json);
                        writer.close();
                        Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(context, "Cannot download the guide.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "No data found for this disaster.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("Disaster", "Error fetching data", databaseError.toException());
            }
        });

    }
}

