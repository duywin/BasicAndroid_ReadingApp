package com.example.readingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.readingapp.model.Book;

import java.util.List;

public class BookCarouselAdapter extends PagerAdapter {
    private Context context;
    private List<Book> books;

    public BookCarouselAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item, container, false);

        ImageView bookImage = view.findViewById(R.id.bookImage);
        TextView bookTitle = view.findViewById(R.id.bookTitle);

        Book book = books.get(position);
        bookTitle.setText(book.getTitle());

        // Load image using Glide
        Glide.with(context).load(book.getImageLink()).into(bookImage);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
