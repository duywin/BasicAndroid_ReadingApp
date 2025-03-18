package com.example.readingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.readingapp.model.Book;
import java.io.File;
import java.util.List;

public class BookCarouselAdapter extends RecyclerView.Adapter<BookCarouselAdapter.ViewHolder> {
    private List<Book> books;

    public BookCarouselAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bookTitle.setText(book.getTitle());

        // Load image correctly from internal storage or assets
        File internalImage = new File(holder.itemView.getContext().getFilesDir(), "books/" + book.getImageLink());
        if (internalImage.exists()) {
            Glide.with(holder.itemView.getContext()).load(internalImage).into(holder.bookImage);
        } else {
            String assetPath = "file:///android_asset/books/" + book.getImageLink();
            Glide.with(holder.itemView.getContext()).load(assetPath).into(holder.bookImage);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
        }
    }
}
