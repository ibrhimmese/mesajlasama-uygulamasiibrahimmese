package com.ibrahimmese.ibrahimmesefinal.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrahimmese.ibrahimmesefinal.R;
import com.ibrahimmese.ibrahimmesefinal.databaseadapter.FriendAdapter;
import com.ibrahimmese.ibrahimmesefinal.databasemodel.Friend;
import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    private ImageView ivFriendsBack = null;
    private RecyclerView rvFriends = null;
    private FriendAdapter friendAdapter = null;
    private ArrayList<Friend> friendArrayList = null;

    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;
    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        findViewsById();
        initializeFirebase();
        setLayoutManager();
        setItems();

        ivFriendsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void findViewsById() {
        ivFriendsBack = findViewById(R.id.ivFriendsBack);
        rvFriends = findViewById(R.id.rvFriends);
    }

    private void initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance("https://final-8d1f6-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("FRIENDSHIP");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setLayoutManager() {
        rvFriends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FriendsActivity.this, LinearLayoutManager.VERTICAL, false);
        rvFriends.setLayoutManager(linearLayoutManager);
    }

    private void setItems() {
        friendArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendArrayList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    Friend friend = new Friend(d.getKey(), String.valueOf(d.child("from").getValue()), String.valueOf(d.child("to").getValue()), String.valueOf(d.child("controller").getValue()));

                    if (friend.getTo().equals(firebaseUser.getEmail())) {
                        friendArrayList.add(friend);
                    }
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        friendAdapter = new FriendAdapter(FriendsActivity.this, friendArrayList);
        friendAdapter.notifyDataSetChanged();
        rvFriends.setAdapter(friendAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}