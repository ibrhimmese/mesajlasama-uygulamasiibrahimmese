package com.ibrahimmese.ibrahimmesefinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrahimmese.ibrahimmesefinal.R;
import com.ibrahimmese.ibrahimmesefinal.databaseadapter.MessageAdapter;
import com.ibrahimmese.ibrahimmesefinal.databasemodel.Message;
import com.ibrahimmese.ibrahimmesefinal.databasemodel.User;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import at.markushi.ui.CircleButton;

public class ChatActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayoutMessageBackHolder = null;
    private EditText editTextClMessage = null;
    private CircleButton circleButtonSendMessage = null;
    private RecyclerView recyclerViewMessage = null;
    private ImageView imageViewConstraintLayoutProfile = null;
    private TextView textViewConstraintLayoutMessageTitle = null;

    private MessageAdapter messageAdapter = null;
    private ArrayList<Message> messageArrayList = null;
    public static User user = null;

    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;
    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViewsById();
        getUserInfo();
        initializeFirebase();
        setLayoutManager();
        setItems();

        constraintLayoutMessageBackHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        circleButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage(view);

            }
        });

    }

    private void findViewsById() {
        constraintLayoutMessageBackHolder = findViewById(R.id.constraintLayoutMessageBackHolder);
        editTextClMessage = findViewById(R.id.editTextClMessage);
        circleButtonSendMessage = findViewById(R.id.circleButtonSendMessage);
        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);
        imageViewConstraintLayoutProfile = findViewById(R.id.imageViewConstraintLayoutProfile);
        textViewConstraintLayoutMessageTitle = findViewById(R.id.textViewConstraintLayoutMessageTitle);
    }

    private void sendMessage(final View view) {
        String message = editTextClMessage.getText().toString().trim();

        if (message.length() != 0) {
            messageArrayList = new ArrayList<>();

            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
            Message messageobj = new Message(message, firebaseUser.getUid(), user.getUid(), timeStamp);

            databaseReference.push().setValue(messageobj).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    messageArrayList.clear();
                    setItems();
                    editTextClMessage.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(view, "Mesaj gönderilirken hata meydana geldi.", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://final-8d1f6-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("MESAJLAR");
    }

    private void setLayoutManager() {
        recyclerViewMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessage.setLayoutManager(linearLayoutManager);
    }

    private void setItems() {
        messageArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    Message message = d.getValue(Message.class);
                    if (message.getKimden().equals(user.getUid()) && message.getKime().equals(firebaseUser.getUid())) {
                        messageArrayList.add(message);
                    }else if (message.getKimden().equals(firebaseUser.getUid()) && message.getKime().equals(user.getUid())) {
                        messageArrayList.add(message);
                    }
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUserInfo() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        Picasso.get().load(Uri.parse(user.getFotograf())).into(imageViewConstraintLayoutProfile);

        textViewConstraintLayoutMessageTitle.setText(user.getAdsoyad());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        messageAdapter = new MessageAdapter(ChatActivity.this, messageArrayList);
        messageAdapter.notifyDataSetChanged();
        recyclerViewMessage.setAdapter(messageAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}