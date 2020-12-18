package com.example.clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText user_name , full_name , et_email , et_password;
    Button register;
    TextView txtlogin;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firebaseAuth = FirebaseAuth.getInstance();
        user_name = findViewById(R.id.et_Username);
        full_name = findViewById(R.id.et_fullname);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        register = findViewById(R.id.btn_register);
        txtlogin = findViewById(R.id.text_login);

        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Plaese wait...");
                pd.show();

                final String str_username = user_name.getText().toString();
                final String str_fullname = full_name.getText().toString();
                String str_email = et_email.getText().toString();
                String str_password = et_password.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) ||
                        TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegisterActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6 ){
                    Toast.makeText(RegisterActivity.this, "Password is To short", Toast.LENGTH_SHORT).show();
                }else {

                    firebaseAuth.createUserWithEmailAndPassword(str_email,str_password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        String userId = user.getUid();
                                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                                        HashMap<String,Object> hashMap = new HashMap<>();
                                        hashMap.put("id",userId);
                                        hashMap.put("username",str_username.toLowerCase());
                                        hashMap.put("fullname",str_fullname);
                                        hashMap.put("bio","");
                                        hashMap.put("imgurl","gs://clone-19e44.appspot.com/IMG_0016.JPG");

                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    pd.dismiss();
                                                    Intent ii = new Intent(RegisterActivity.this,LoginActivity.class);
                                                    ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(ii);

                                                }
                                            }
                                        });
                                    }else {
                                        pd.dismiss();
                                        Toast.makeText(RegisterActivity.this, "you cant register with this email and password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


            }
        });
    }
}