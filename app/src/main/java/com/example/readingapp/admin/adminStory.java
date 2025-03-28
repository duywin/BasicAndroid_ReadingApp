package com.example.readingapp.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.dao.BookDAO;
import com.example.readingapp.model.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class adminStory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Story> storyList;
    private Button addStoryButton;
    private StoryAdapter storyAdapter;
    private int accountId;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_story);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        accountId = sharedPreferences.getInt("account_id", -1);

        if (accountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }


        recyclerView = findViewById(R.id.story_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addStoryButton = findViewById(R.id.add_story_button);
        addStoryButton.setOnClickListener(v -> addNewStory());

        loadStories();
        setupBottomNavigation();
    }

    // Load books from database
    private void loadStories() {
        storyList = readStoriesFromDatabase();

        if (storyList == null) {
            storyList = new ArrayList<>(); // Prevent NullPointerException
        }

        storyAdapter = new StoryAdapter(storyList);
        recyclerView.setAdapter(storyAdapter);
    }

    private List<Story> readStoriesFromDatabase() {
        List<Story> stories = new ArrayList<>();
        BookDAO bookDAO = new BookDAO(this);
        List<Book> books = bookDAO.getAllBooks();

        if (books != null) {
            for (Book book : books) {
                stories.add(new Story(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getDescription(),
                        book.getImageLink()
                ));
            }
        } else {
            Toast.makeText(this, "Error loading books!", Toast.LENGTH_SHORT).show();
        }

        return stories;
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_story);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_chart) {
                startActivity(new Intent(adminStory.this, adminChart.class));
                return true;
            } else if (id == R.id.nav_admin_story) {
                return true;
            } else if (id == R.id.nav_admin_genre) {
                startActivity(new Intent(adminStory.this, adminGenre.class));
                return true;
            } else if (id == R.id.nav_admin_account) {
                startActivity(new Intent(adminStory.this, adminAccount.class));
                return true;
            }
            return false;
        });
    }

    private void addNewStory() {
        Intent intent = new Intent(adminStory.this, addStoryActivity.class);
        startActivityForResult(intent, 1);
    }

    private static class Story {
        int id;
        String title, author, description, imageUrl;

        Story(int id, String title, String author, String description, String imageUrl) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.description = description;
            this.imageUrl = imageUrl;
        }
    }

    private class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
        private final List<Story> stories;

        StoryAdapter(List<Story> stories) {
            this.stories = stories;
        }

        @NonNull
        @Override
        public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_story_item, parent, false);
            return new StoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
            Story story = stories.get(position);
            holder.storyTitle.setText("Tên truyện: " + story.title);
            holder.storyAuthor.setText("Tác giả: " + story.author);
            holder.storyDescription.setText("Mô tả: "+ story.description);

            // Load image correctly from internal storage or assets
            File internalImage = new File(getFilesDir(), "books/" + story.imageUrl);
            if (internalImage.exists()) {
                Glide.with(holder.storyImage.getContext()).load(internalImage).into(holder.storyImage);
            } else {
                String assetPath = "file:///android_asset/books/" + story.imageUrl;
                Glide.with(holder.storyImage.getContext()).load(assetPath).into(holder.storyImage);
            }

            holder.editButton.setOnClickListener(v -> editStory(story.id));
            holder.deleteButton.setOnClickListener(v -> deleteStory(story.id));

            // Navigate to adminChapter to show chapters for the selected book
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(adminStory.this, adminChapter.class);
                intent.putExtra("BOOK_ID", story.id);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return stories.size();
        }

        public class StoryViewHolder extends RecyclerView.ViewHolder {
            TextView storyTitle, storyAuthor, storyDescription;
            ImageView storyImage;
            Button editButton, deleteButton;

            StoryViewHolder(View itemView) {
                super(itemView);
                storyTitle = itemView.findViewById(R.id.story_title);
                storyAuthor = itemView.findViewById(R.id.story_author);
                storyDescription = itemView.findViewById(R.id.story_description);
                storyImage = itemView.findViewById(R.id.story_image);
                editButton = itemView.findViewById(R.id.edit_story_button);
                deleteButton = itemView.findViewById(R.id.delete_story_button);
            }
        }
    }

    private void editStory(int storyId) {
        Intent intent = new Intent(adminStory.this, addStoryActivity.class);
        intent.putExtra("BOOK_ID", storyId);
        startActivityForResult(intent, 1);
    }

    private void deleteStory(int storyId) {
        BookDAO bookDAO = new BookDAO(this);
        Book book = bookDAO.getBookById(storyId);

        if (book != null) {
            boolean deleted = bookDAO.deleteBook(storyId);
            if (deleted) {
                File imageFile = new File(getFilesDir(), "books/" + book.getImageLink());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                loadStories(); // Refresh list after delete
                Toast.makeText(this, "Xóa truyện thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi xóa truyện!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
