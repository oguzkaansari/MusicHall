package com.example.musichall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginScreen extends AppCompatActivity {

    EditText mailText,passwordText;
    DatabaseReference dbRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        auth = FirebaseAuth.getInstance();
        mailText = (EditText) findViewById(R.id.mail);
        passwordText = (EditText) findViewById(R.id.password);

    }
    @Override
    public void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, MainActivity.class));

        }
    }
    public void login(View view){


        String email = mailText.getText().toString();
        String password = passwordText.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please enter your mail",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please enter your password",Toast.LENGTH_SHORT).show();
        }
        if (password.length()<8){
            Toast.makeText(getApplicationContext(),"Your password should be contain least 8 characters",Toast.LENGTH_SHORT).show();
        }
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginScreen.this, "getInstanceId failed", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String deviceToken = task.getResult().getToken();
                                    dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid());
                                    dbRef.child("device_token").setValue(deviceToken);

                                }
                            });
                    finish();
                    startActivity(new Intent(LoginScreen.this,MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public void signUp(View view){
        finish();
        startActivity(new Intent(LoginScreen.this, SignUp.class));


    }
}
