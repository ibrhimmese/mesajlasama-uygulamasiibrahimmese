package com.ibrahimmese.ibrahimmesefinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrahimmese.ibrahimmesefinal.R;
import com.ibrahimmese.ibrahimmesefinal.databaseadapter.ContactAdapter;
import com.ibrahimmese.ibrahimmesefinal.databasemodel.User;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView imageViewMainProfile = null;
    private ImageView imageViewMainMenu = null;
    private ImageView imageViewMainAddFriend = null;
    private ImageView imageViewMainBack = null;
    private RecyclerView recyclerViewMain = null;
    private ConstraintLayout constraintLayoutMainMenu, constraintLayoutMainProfile, constraintLayoutMainLogout = null;
    private EditText editTextMainSearch = null;
    private TextView textView2, textView3 = null;

    private ContactAdapter contactAdapter = null;
    private ArrayList<User> userArrayList = null;

    private FirebaseDatabase firebaseDatabase = null;
    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;
    private DatabaseReference databaseReference = null;

    private String password = "";
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsById();
        startFirebase();
        userProfileInformation();
        recyclerViewLayoutManager();
        setDatabases();

        imageViewMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewMain.setVisibility(View.INVISIBLE);
                constraintLayoutMainMenu.setVisibility(View.VISIBLE);
            }
        });

        imageViewMainAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editTextMainSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterList(editable.toString());
            }
        });

        imageViewMainBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewMain.setVisibility(View.VISIBLE);
                constraintLayoutMainMenu.setVisibility(View.INVISIBLE);
            }
        });

        constraintLayoutMainProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewMain.setVisibility(View.VISIBLE);
                constraintLayoutMainMenu.setVisibility(View.INVISIBLE);

                toProfilePage();

            }
        });

        constraintLayoutMainLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (firebaseUser != null) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Snackbar.make(view, "Çıkış yapılacak bir oturum bulunamadı", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private void findViewsById() {
        imageViewMainProfile = findViewById(R.id.imageViewMainProfile);
        imageViewMainMenu = findViewById(R.id.imageViewMainMenu);
        imageViewMainAddFriend = findViewById(R.id.imageViewMainAddFriend);
        imageViewMainBack = findViewById(R.id.imageViewMainBack);
        recyclerViewMain = findViewById(R.id.recyclerViewMain);
        constraintLayoutMainMenu = findViewById(R.id.constraintLayoutMainMenu);
        constraintLayoutMainProfile = findViewById(R.id.constraintLayoutMainProfile);
        constraintLayoutMainLogout = findViewById(R.id.constraintLayoutMainLogout);
        editTextMainSearch = findViewById(R.id.editTextMainSearch);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
    }

    private void startFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://final-8d1f6-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("UYELER");
    }

    private void recyclerViewLayoutManager() {
        recyclerViewMain.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMain.setLayoutManager(linearLayoutManager);
    }

    private void userProfileInformation() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    User user = d.getValue(User.class);

                    if (user != null && user.getUid().equals(firebaseUser.getUid())) {
                        uid = d.getKey();
                        password = user.getParola();
                        setUserInfo(user.getFotograf(), user.getAdsoyad(), user.getEmail());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUserInfo(String photo, String namelastname, String email) {
        if (Patterns.WEB_URL.matcher(photo).matches()) {
            Picasso.get().load(photo).into(imageViewMainProfile);
        }
        textView2.setText(namelastname);
        textView3.setText(email);
    }

    private void setDatabases() {
        userArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    User user = d.getValue(User.class);
                    if (user != null  && !user.getEmail().equals(firebaseUser.getEmail())) {
                        userArrayList.add(user);
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
        contactAdapter = new ContactAdapter(MainActivity.this, userArrayList);
        contactAdapter.notifyDataSetChanged();
        recyclerViewMain.setAdapter(contactAdapter);
    }

    private void filterList(String text) {
        ArrayList<User> userFilterArrayList = new ArrayList<>();

        for (User user : userArrayList
             ) {
            if (user.getAdsoyad().toLowerCase().replaceAll(" ", "").contains(text.toLowerCase().replaceAll(" ", ""))) {
                userFilterArrayList.add(user);
            }
        }
        contactAdapter.filterUser(userFilterArrayList);
    }

    private void toProfilePage() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}