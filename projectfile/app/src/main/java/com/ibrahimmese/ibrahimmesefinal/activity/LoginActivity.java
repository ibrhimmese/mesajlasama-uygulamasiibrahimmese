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
import com.ibrahimmese.ibrahimmesefinal.R;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageViewSigninCancel = null;
    private TextView textViewSigninSignup = null;
    private CardView cardViewSignin = null;
    private TextInputEditText textInputEditTextSigninEmail, textInputEditTextSigninPassword = null;

    private FirebaseAuth firebaseAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        findViewsById();
        startFirebase();
        onClickListenersMethods();
    }

    private void findViewsById() {
        imageViewSigninCancel = findViewById(R.id.imageViewSigninCancel);
        textViewSigninSignup = findViewById(R.id.textViewSigninSignup);
        cardViewSignin = findViewById(R.id.cardViewSignin);
        textInputEditTextSigninEmail = findViewById(R.id.textInputEditTextSigninEmail);
        textInputEditTextSigninPassword = findViewById(R.id.textInputEditTextSigninPassword);
    }

    private void startFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void onClickListenersMethods() {
        imageViewSigninCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        textViewSigninSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSignupPage();
            }
        });
        cardViewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textInputEditTextSigninEmail.getText().toString().trim();
                String password = textInputEditTextSigninPassword.getText().toString();

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 6) {
                    userController(view, email, password);
                }else {
                    Snackbar.make(view, "Lütfen bilgilerinizi kontrol ediniz", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void userController(final View view, String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                toMainPage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void toSignupPage() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void toMainPage() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}