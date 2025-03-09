package com.example.readingapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.readingapp.R;
import com.example.readingapp.*;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class adminChapter extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ChapterItem> chapterList;
    private int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chapter);

        recyclerView = findViewById(R.id.chapter_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        chapterList = readChaptersFromAssets(bookId);

        recyclerView.setAdapter(new ChapterAdapter(chapterList));
    }

    private List<ChapterItem> readChaptersFromAssets(int bookId) {
        List<ChapterItem> chapters = new ArrayList<>();
        AssetManager assetManager = getAssets();

        try (InputStream inputStream = assetManager.open("preload_data.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONArray chaptersArray = new JSONObject(json.toString()).getJSONArray("chapters");
            for (int i = 0; i < chaptersArray.length(); i++) {
                JSONObject chapterObj = chaptersArray.getJSONObject(i);
                if (chapterObj.getInt("book_id") == bookId) {
                    chapters.add(new ChapterItem(
                            chapterObj.getString("chapter_name"),
                            chapterObj.getString("link")
                    ));
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return chapters;
    }

    private static class ChapterItem {
        String name, link;

        ChapterItem(String name, String link) {
            this.name = name;
            this.link = link;
        }
    }

    private class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
        private final List<ChapterItem> chapters;

        ChapterAdapter(List<ChapterItem> chapters) {
            this.chapters = chapters;
        }

        @NonNull
        @Override
        public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chapter_list_item, parent, false);
            return new ChapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
            ChapterItem chapter = chapters.get(position);
            holder.chapterName.setText(chapter.name);

            // Open chapter content on click
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(adminChapter.this, ChapterContent.class);
                intent.putExtra("CHAPTER_NAME", chapter.name);
                intent.putExtra("CHAPTER_LINK", chapter.link);
                startActivity(intent);
            });

            // Handle delete button click
            holder.deleteButton.setOnClickListener(v -> {
                chapters.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(adminChapter.this, "Chapter deleted", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return chapters.size();
        }

        public class ChapterViewHolder extends RecyclerView.ViewHolder {
            TextView chapterName;
            Button deleteButton;

            ChapterViewHolder(View itemView) {
                super(itemView);
                chapterName = itemView.findViewById(R.id.chapter_name);
                deleteButton = itemView.findViewById(R.id.btn_delete);
            }
        }
    }
}