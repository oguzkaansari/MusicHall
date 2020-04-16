package com.example.musichall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView nameText,emailText,passwordText,passwordVerificationText,birthDateText;
    User user= new User();
    String deviceToken;
    List<String> defaultPlayList = new ArrayList<String>();
    //FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        nameText = (TextView)findViewById(R.id.name);
        emailText = (TextView)findViewById(R.id.mail);
        passwordText = (TextView)findViewById(R.id.password);
        passwordVerificationText = (TextView)findViewById(R.id.passwordVerification);
        birthDateText = (TextView)findViewById(R.id.birthDate);
        //storage = FirebaseStorage.getInstance();


    }
    public void signUp(View view) {


        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        if (password.length() < 8) {
            Toast.makeText(getApplicationContext(), "Your password should be contain least 8 characters", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "getInstanceId failed", Toast.LENGTH_SHORT).show();

                                return;
                            }
                            deviceToken = task.getResult().getToken();

                        }
                    });
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //İşlem başarısız olursa kullanıcıya bir Toast mesajıyla bildiriyoruz.
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "Yetkilendirme Hatası",
                                        Toast.LENGTH_SHORT).show();
                            }

                            //İşlem başarılı olduğu takdirde MainActivity e yönlendiriyoruz.
                            else {
                                final ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
                                progressDialog.setTitle("Saving...");
                                progressDialog.show();
                                DatabaseReference ddRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                user.setName(nameText.getText().toString());
                                user.setEmail(emailText.getText().toString());
                                user.setBirthDate(birthDateText.getText().toString());


                                ddRef.child(auth.getUid()).setValue(user);
                                ddRef.child(auth.getUid()).child("device_token").setValue(deviceToken);
                                ddRef.child(auth.getUid()).child("haveProfilePic").setValue("no");




                                finish();
                                startActivity(new Intent(SignUp.this, MainActivity.class));


                            }

                        }
                    });

        }
    }
}
