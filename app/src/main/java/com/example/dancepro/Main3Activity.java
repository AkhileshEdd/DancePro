package com.example.dancepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class Main3Activity extends AppCompatActivity {

    TextView Username,email,phone,verifymsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userid;
    Button resetbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Username=findViewById(R.id.pname);
        email=findViewById(R.id.pemail);
        phone=findViewById(R.id.pphone);
        resetbtn=findViewById(R.id.verifybtn);
        verifymsg=findViewById(R.id.verifymsg);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userid=fAuth.getCurrentUser().getUid();
        final FirebaseUser user=fAuth.getCurrentUser();




        if (!user.isEmailVerified()){
            resetbtn.setVisibility(View.VISIBLE);
            verifymsg.setVisibility(View.VISIBLE);

            resetbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(Main3Activity.this,"Verification email has been sent",Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag","onFailure : Email not sent"+e.getMessage());
                        }
                    });
                }
            });

        }

        DocumentReference documentReference= fstore.collection("users").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Username.setText(documentSnapshot.getString("uName"));
                email.setText(documentSnapshot.getString("email"));
                phone.setText(documentSnapshot.getString("phone"));

            }
        });

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void uploadpic(View view) {
        startActivity(new Intent(getApplicationContext(),upload_img.class));
        finish();
    }
}

