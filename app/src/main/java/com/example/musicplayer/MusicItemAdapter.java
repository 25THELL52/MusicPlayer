 package com.example.musicplayer; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemAdapter.MusicItemViewHolder> {

    @NonNull
    @Override
    public MusicItemAdapter.MusicItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myTextView, parent, false);

        return new MusicItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicItemAdapter.MusicItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MusicItemViewHolder extends RecyclerView.ViewHolder {


        public MusicItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
