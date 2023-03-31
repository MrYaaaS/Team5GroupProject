package edu.northeastern.groupproject.GameSphere;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import edu.northeastern.groupproject.R;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<News> newsList;
    private DatabaseReference dataRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsList = new ArrayList<>();
        dataRef = FirebaseDatabase.getInstance().getReference("News");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Long newsId = snapshot.child("newsId").getValue(Long.class);
                    Long numberOfLikes = snapshot.child("numberOfLikes").getValue((Long.class));
                    String title = snapshot.child("title").getValue(String.class);
                    String newsDate = snapshot.child("newsDate").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);

                    List<News.Comment> commentList = new ArrayList<>();
                    List<Map<String, String>> comments = (List<Map<String, String>>) snapshot.child("comments").getValue();
                    for (Map<String, String> comment : comments) {
                        String commentDate = comment.get("commentDate");
                        String commentContent = comment.get("content");
                        String commentUsername = comment.get("username");
                        commentList.add(new News.Comment(commentUsername, commentContent, commentDate));
                    }

                    News news = new News(newsId, title, content, newsDate, numberOfLikes, commentList);
                    newsList.add(news);
                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsList, this);
        recyclerView.setAdapter(newsAdapter);
    }
}
