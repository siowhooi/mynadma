package com.example.mynadma;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// CardAdapter.java
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<OfflineLIneCacheCard> cardItemList;

    // Constructor
    public CardAdapter(List<OfflineLIneCacheCard> cardItemList) {
        this.cardItemList = cardItemList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offline_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        OfflineLIneCacheCard item = cardItemList.get(position);
        holder.cardTitle.setText(item.getTitle());

        // Set Edit Button listener
        holder.editButton.setOnClickListener(v -> {
            // Perform your edit action
            Toast.makeText(v.getContext(), "Edit Button Clicked", Toast.LENGTH_SHORT).show();
            final EditText editText = new EditText(v.getContext());
            editText.setText(item.getTitle());  // Pre-fill with current title

            // Create an AlertDialog to prompt user to change the title
            new AlertDialog.Builder(v.getContext())
                .setTitle("Edit Title")
                .setMessage("Please enter the new title:")
                .setView(editText)  // Set the EditText in the dialog
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the new title from the EditText and set it to the item
                        String newTitle = editText.getText().toString().trim();

                        // Update the title (e.g., update the TextView or dataset)
                        holder.cardTitle.setText(newTitle);

                        // Optionally, you can also update the data in your adapter's dataset
                        // dataList.get(position).setTitle(newTitle);
                        // notifyItemChanged(position);

                        // Show confirmation message (Toast)
                        Toast.makeText(v.getContext(), "Title updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)  // No action on Cancel
                .show();
        });

        // Set Delete Button listener
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage(item.getTitle())
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Perform the delete action
                            // In this case, just show a Toast for now
                            Toast.makeText(v.getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();

                            // You would remove the item from your dataset here
                            // e.g., dataList.remove(position);
                            // adapter.notifyItemRemoved(position);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the card click
                Toast.makeText(v.getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

                // Perform any action you want, e.g., navigate to a detail page or show more info
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle, dateDownloaded;
        Button editButton, deleteButton;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            dateDownloaded = itemView.findViewById(R.id.dateDownloaded);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

