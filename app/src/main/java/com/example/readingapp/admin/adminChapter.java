package com.example.readingapp.admin;

import android.content.Intent;
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

import com.example.readingapp.ChapterContent;
import com.example.readingapp.R;
import com.example.readingapp.dao.ChapterDAO;
import com.example.readingapp.model.Chapter;

import java.util.List;

public class adminChapter extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnWriteChapter, btnUploadChapter, btnBack;
    private List<Chapter> chapterList;
    private ChapterDAO chapterDAO;
    private int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chapter);

        recyclerView = findViewById(R.id.chapter_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnWriteChapter = findViewById(R.id.btn_write_chapter);
        btnUploadChapter = findViewById(R.id.btn_upload_chapter);
        btnBack = findViewById(R.id.btn_back_admin_story);

        chapterDAO = new ChapterDAO(this);
        bookId = getIntent().getIntExtra("BOOK_ID", -1);

        chapterList = chapterDAO.getChaptersByBookId(bookId);
        recyclerView.setAdapter(new ChapterAdapter(chapterList));

        btnWriteChapter.setOnClickListener(v -> {
            Intent intent = new Intent(adminChapter.this, WriteChapterActivity.class);
            intent.putExtra("BOOK_ID", bookId);  // ✅ Pass book ID
            startActivity(intent);
        });

        btnUploadChapter.setOnClickListener(v -> {
            Intent intent = new Intent(adminChapter.this, UploadChapterActivity.class);
            intent.putExtra("BOOK_ID", bookId);  // ✅ Pass book ID
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
        private final List<Chapter> chapters;

        ChapterAdapter(List<Chapter> chapters) {
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
            Chapter chapter = chapters.get(position);
            holder.chapterName.setText(chapter.getChapterName());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(adminChapter.this, ChapterContent.class);
                intent.putExtra("CHAPTER_NAME", chapter.getChapterName());
                intent.putExtra("CHAPTER_LINK", chapter.getLink());
                startActivity(intent);
            });

            holder.deleteButton.setOnClickListener(v -> {
                boolean deleted = chapterDAO.deleteChapter(chapter.getId());

                if (deleted) {
                    chapters.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(adminChapter.this, "Chapter deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(adminChapter.this, "Error deleting chapter!", Toast.LENGTH_SHORT).show();
                }
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
