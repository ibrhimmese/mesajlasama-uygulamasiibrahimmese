package com.ibrahimmese.ibrahimmesefinal.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrahimmese.ibrahimmesefinal.R;
import com.ibrahimmese.ibrahimmesefinal.databasemodel.User;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imageViewProfileBack, imageViewProfileAddPhoto = null;
    private TextInputEditText textInputEditTextProfileNameLastname = null;
    private TextInputEditText textInputEditTextProfilePassword = null;
    private CardView cardViewProfileSave = null;
    private ConstraintLayout constraintLayoutProfileSave = null;

    private ProgressDialog progressDialog = null;

    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;
    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;
    private FirebaseStorage firebaseStorage = null;
    private StorageReference storageReference = null;

    private String userkey = "";
    private String password = "";
    private boolean controller = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeFirebase();
        findViewsById();
        setOnClickListeners();
    }

    private void findViewsById() {
        imageViewProfileBack = findViewById(R.id.imageViewProfileBack);
        imageViewProfileAddPhoto = findViewById(R.id.imageViewProfileAddPhoto);
        textInputEditTextProfileNameLastname = findViewById(R.id.textInputEditTextProfileNameLastname);
        textInputEditTextProfilePassword = findViewById(R.id.textInputEditTextProfilePassword);
        constraintLayoutProfileSave = findViewById(R.id.constraintLayoutProfileSave);
        cardViewProfileSave = findViewById(R.id.cardViewProfileSave);
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://final-8d1f6-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("UYELER");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("FOTOGRAFLAR").child(firebaseUser.getUid());

        findUser();
    }

    private void findUser() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    User user = d.getValue(User.class);

                    if (user != null && user.getUid().equals(firebaseUser.getUid())) {
                        userkey = d.getKey();
                        password = user.getParola();
                        controller = true;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickListeners() {
        imageViewProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageViewProfileAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhotoIntent();
            }
        });

        cardViewProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller) {
                    updateProfileInformation(textInputEditTextProfileNameLastname.getText().toString().trim(), textInputEditTextProfilePassword.getText().toString());
                }
            }
        });
    }

    private void getPhotoIntent() {
        if (controller) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Choose a picture"), 1);
        }else {
            Toast.makeText(ProfileActivity.this, "Fotoğraf eklenirken hata meydana geldi.", Toast.LENGTH_LONG).show();
        }

    }

    private void updateProfileInformation(String namelastname, String password) {
        if (this.password.equals(password)) {

            progressDialog = ProgressDialog.show(ProfileActivity.this, "", "Bilgileriniz güncellenirken lütfen bekleyiniz", false);
            progressDialog.setCancelable(false);

            databaseReference.child(userkey).child("adsoyad").setValue(namelastname).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    toSplashPage();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(ProfileActivity.this, "Girdiğiniz şifre hatalı olduğundan işlem gerçekleşemedi", Toast.LENGTH_LONG).show();
        }
    }

    private void updateProfilePhoto(Uri imageuri) {

        progressDialog = ProgressDialog.show(ProfileActivity.this, "", "Bilgileriniz güncellenirken lütfen bekleyiniz", false);
        progressDialog.setCancelable(false);

        storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child(userkey).child("fotograf").setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                toSplashPage();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toSplashPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageuri = data.getData();
            updateProfilePhoto(imageuri);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}