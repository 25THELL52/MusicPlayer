package com.example.musicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemAdapter.MusicItemViewHolder> {


    List<MusicFile> musicFiles;
    List<String> listOfSongTitles;
    MainActivity mainActivity;

    MusicItemAdapter(MainActivity mainActivity, List<MusicFile> musicFiles) {
        this.mainActivity = mainActivity;
        this.musicFiles = musicFiles;
    }

    public void setMusicFiles(List<MusicFile> musicFiles) {
        this.musicFiles = musicFiles;
    }

    public void setListOfSongTitles(List<String> listOfSongTitles) {
        this.listOfSongTitles = listOfSongTitles;
    }

    @NonNull
    @Override
    public MusicItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item, parent, false);

        return new MusicItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicItemViewHolder holder, int position) {

        holder.textView.setText(musicFiles.get(position).getTitle());

        if (!musicFiles.get(position).getOnPlay()) {
            holder.textView.setTextColor(mainActivity.getResources().getColor(R.color.purple_50));
            holder.itemView.setBackgroundColor(mainActivity.getResources().getColor(R.color.black));
        }
        else{
            holder.textView.setTextColor(mainActivity.getResources().getColor(R.color.black));
            holder.itemView.setBackgroundColor(mainActivity.getResources().getColor(R.color.purple_100));
        }
        holder.itemView.requestFocus();
        holder.itemView.setOnClickListener(v -> {
            mainActivity.onItemClicked(position);
        });

    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public class MusicItemViewHolder extends RecyclerView.ViewHolder {


        TextView textView;

        public MusicItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.tv);

        }
    }
}
