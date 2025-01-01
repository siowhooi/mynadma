package com.example.mynadma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle, dateDownloaded;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            dateDownloaded = itemView.findViewById(R.id.dateDownloaded);
        }
    }
}

