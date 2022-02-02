package com.ibrahimmese.ibrahimmesefinal.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ibrahimmese.ibrahimmesefinal.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startFirebase();
        timer();

    }

    private void startFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void timer() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseUser != null) {
                    toMainPage();
                }else {
                    toSigninPage();
                }
            }
        }, 3000);

    }

    private void toMainPage() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void toSigninPage() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}