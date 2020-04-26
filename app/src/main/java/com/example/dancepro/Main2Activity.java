package com.example.dancepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText username,email,password,phone;
    Button register,log;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        phone = findViewById(R.id.phone);
        register = findViewById(R.id.register);
        log = findViewById(R.id.login);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressbar);

        if(fAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(),Main3Activity.class));
            finish();
        }

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Main2Activity.this,MainActivity.class);
                startActivity(i);
            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String e=email.getText().toString().trim();
                String ps=password.getText().toString().trim();
                final String n=username.getText().toString();
                final String ph=phone.getText().toString();


                if(TextUtils.isEmpty(e)){
                    email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(ps)){
                    password.setError("Password is required");
                    return;
                }

                if(ps.length()<6){
                    password.setError("Password must be more than 6 cahracters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //register the user in firebase

                fAuth.createUserWithEmailAndPassword(e,ps).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser fuser=fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(Main2Activity.this,"Verification email has been sent",Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure : Email not sent"+e.getMessage());
                                }
                            });


                            Toast.makeText(Main2Activity.this,"User created",Toast.LENGTH_SHORT).show();
                            userid=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fstore.collection("users").document(userid);
                            Map<String,Object>user=new HashMap<>();
                            user.put("uName",n);
                            user.put("email",e);
                            user.put("phone",ph);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSuccess : user profile is created for"+userid);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),Main3Activity.class));
                        }
                        else{
                            Toast.makeText(Main2Activity.this,"Error!!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}
