package com.example.readingapp.admin;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.example.readingapp.R;

public class adminStory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Story> storyList;
    private Button addStoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_story);

        recyclerView = findViewById(R.id.story_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addStoryButton = findViewById(R.id.add_story_button);
        addStoryButton.setOnClickListener(v -> addNewStory()); // Placeholder for add function

        storyList = readStoriesFromAssets();
        recyclerView.setAdapter(new StoryAdapter(storyList));
    }

    private List<Story> readStoriesFromAssets() {
        List<Story> stories = new ArrayList<>();
        AssetManager assetManager = getAssets();

        try (InputStream inputStream = assetManager.open("preload_data.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONArray booksArray = new JSONObject(json.toString()).getJSONArray("books");
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject book = booksArray.getJSONObject(i);
                String imagePath = book.getString("image_link");
                String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                stories.add(new Story(
                        i + 1,
                        book.getString("title"),
                        book.getString("author"),
                        book.getString("description"),
                        imageName
                ));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return stories;
    }

    private void addNewStory() {
        // Placeholder for adding a new story
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
            holder.storyId.setText("ID: " + story.id);
            holder.storyTitle.setText(story.title);
            holder.storyAuthor.setText("Tác giả: " + story.author);
            holder.storyDescription.setText(story.description);

            String assetPath = "file:///android_asset/books/" + story.imageUrl;
            Glide.with(holder.storyImage.getContext()).load(assetPath).into(holder.storyImage);

            holder.editButton.setOnClickListener(v -> editStory(story.id)); // Placeholder for edit function
            holder.deleteButton.setOnClickListener(v -> deleteStory(story.id)); // Placeholder for delete function

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
            TextView storyId, storyTitle, storyAuthor, storyDescription;
            ImageView storyImage;
            Button editButton, deleteButton;

            StoryViewHolder(View itemView) {
                super(itemView);
                storyId = itemView.findViewById(R.id.story_id);
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
        // Placeholder for editing a story
    }

    private void deleteStory(int storyId) {
        // Placeholder for deleting a story
    }
}