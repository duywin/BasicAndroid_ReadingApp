package com.example.readingapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingapp.model.Genre;

import java.util.List;
import java.util.Random;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private final List<Genre> genres;
    private final Context context;
    private final OnGenreClickListener listener;
    private final int[] colors = {
            Color.parseColor("#FF5733"), // Red
            Color.parseColor("#33FF57"), // Green
            Color.parseColor("#3357FF"), // Blue
            Color.parseColor("#F1C40F"), // Yellow
            Color.parseColor("#9B59B6"), // Purple
            Color.parseColor("#E74C3C"), // Dark Red
            Color.parseColor("#2ECC71"), // Bright Green
            Color.parseColor("#3498DB")  // Sky Blue
    };

    public interface OnGenreClickListener {
        void onGenreClick(Genre genre);
    }

    public GenreAdapter(Context context, List<Genre> genres, OnGenreClickListener listener) {
        this.context = context;
        this.genres = genres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.genre_item, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.btnGenre.setText(genre.getName());
        holder.btnGenre.setBackgroundColor(colors[new Random().nextInt(colors.length)]);
        holder.btnGenre.setOnClickListener(v -> listener.onGenreClick(genre));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        Button btnGenre;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            btnGenre = itemView.findViewById(R.id.btnGenre);
        }
    }
}
