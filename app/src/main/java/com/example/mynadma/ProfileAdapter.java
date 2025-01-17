package com.example.mynadma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profile> profileList;

    public ProfileAdapter(List<Profile> profileList) {
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile currentProfile = profileList.get(position);
        holder.labelTextView.setText(currentProfile.getLabel());
        holder.valueTextView.setText(currentProfile.getValue());
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        TextView labelTextView;
        TextView valueTextView;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.labelTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
        }
    }
}
