package com.ibrahimmese.ibrahimmesefinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibrahimmese.ibrahimmesefinal.R;
import java.util.HashMap;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private ImageView imageViewSignupCancel = null;
    private TextView textViewSigninSignup = null;
    private CardView cardViewSignup = null;
    private TextInputEditText textInputEditTextSignupNameLastname, textInputEditTextSignupEmail, textInputEditTextSignupPassword = null;

    private FirebaseAuth firebaseAuth = null;
    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewsById();
        initializeFirebase();
        setOnClickListeners();
    }

    private void findViewsById() {
        imageViewSignupCancel = findViewById(R.id.imageViewSignupCancel);
        textViewSigninSignup = findViewById(R.id.textViewSigninSignup);
        cardViewSignup = findViewById(R.id.cardViewSignup);
        textInputEditTextSignupNameLastname = findViewById(R.id.textInputEditTextSignupNameLastname);
        textInputEditTextSignupEmail = findViewById(R.id.textInputEditTextSignupEmail);
        textInputEditTextSignupPassword = findViewById(R.id.textInputEditTextSignupPassword);
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://final-8d1f6-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("UYELER");
    }

    private void setOnClickListeners() {
        imageViewSignupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        textViewSigninSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSigninPage();
            }
        });
        cardViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namelastname = textInputEditTextSignupNameLastname.getText().toString().trim();
                String email = textInputEditTextSignupEmail.getText().toString().trim();
                String password = textInputEditTextSignupPassword.getText().toString();

                if (namelastname.length() >= 5 && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 6) {

                    createUser(view, namelastname, email, password);

                }else {
                    Snackbar.make(view, "Lütfen bilgilerinizi kontrol ediniz", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUser(final View view, final String namelastname, final String email, final String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                HashMap<String, String> userMap = new HashMap<>();

                userMap.put("adsoyad", namelastname);
                userMap.put("fotograf", "https://www.pngarts.com/files/5/User-Avatar-PNG-Free-Download.png");
                userMap.put("email", email);
                userMap.put("parola", password);
                userMap.put("uid", Objects.requireNonNull(authResult.getUser()).getUid());

                databaseReference.push().setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        toMainPage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void toSigninPage() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void toMainPage() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}