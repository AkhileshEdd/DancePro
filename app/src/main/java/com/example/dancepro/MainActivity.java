package com.example.dancepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button signin,signup;
    EditText email,password;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    TextView forgotpassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email= (EditText) findViewById(R.id.loginEmail);
        password= (EditText) findViewById(R.id.password);
        signin= (Button)findViewById(R.id.signin);
        signup=(Button)findViewById(R.id.signup);
        progressBar=findViewById(R.id.progress);
        fAuth=FirebaseAuth.getInstance();
        forgotpassword= findViewById(R.id.forgot);



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e=email.getText().toString().trim();
                String ps=password.getText().toString().trim();


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

                //authenticate the user

                fAuth.signInWithEmailAndPassword(e,ps).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Signed in Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Main3Activity.class));
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Error!!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);


                        }
                    }
                });

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(i);
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText reset = new EditText(view.getContext());
                AlertDialog.Builder resetpassword=new AlertDialog.Builder(view.getContext());
                resetpassword.setTitle("Reset Password");
                resetpassword.setMessage("Enter your email to reset password");
                resetpassword.setView(reset);

                resetpassword.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //extract the email and sent reset link
                        String mail=reset.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Reset link sent to your email",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Error!! Reset link not sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
                resetpassword.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close the dialog
                    }
                });

                resetpassword.create().show();
            }
        });
    }
}
