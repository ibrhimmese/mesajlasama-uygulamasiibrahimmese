package com.ibrahimmese.ibrahimmesefinal.databaseadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibrahimmese.ibrahimmesefinal.activity.ChatActivity;
import com.ibrahimmese.ibrahimmesefinal.R;
import com.ibrahimmese.ibrahimmesefinal.databasemodel.User;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.CardViewDesignThingsHolder> {
    private Context context;
    private ArrayList<User> userArrayList = new ArrayList<>();

    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;
    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;

    public ContactAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public CardViewDesignThingsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardviewcontact, parent, false);
        return new CardViewDesignThingsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewDesignThingsHolder holder, int position) {
        final User user = userArrayList.get(position);

        holder.cvContactNameLastname.setText(user.getAdsoyad());

        holder.clCvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMessagePage(user);
            }
        });

        holder.clCvContact.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                sendAddFriend(user);

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    private void toMessagePage(User user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("user", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static class CardViewDesignThingsHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout clCvContact = null;
        private TextView cvContactNameLastname = null;

        public CardViewDesignThingsHolder(@NonNull View itemView) {
            super(itemView);
            clCvContact = itemView.findViewById(R.id.clCvContact);
            cvContactNameLastname = itemView.findViewById(R.id.cvContactNameLastname);
        }
    }

    private void sendAddFriend(User user) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://final-8d1f6-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("FRIENDSHIP");

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("from", firebaseUser.getEmail());
        userMap.put("to", user.getEmail());
        userMap.put("controller", "false");

        databaseReference.push().setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Arkadaşlık isteği başarıyla gönderildi", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterUser(ArrayList<User> userFilterArrayList) {
        userArrayList = userFilterArrayList;
        notifyDataSetChanged();
    }
}
