package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.User;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPass, etName, etPhone, etAddress;
    Button btnDangKy;
    FirebaseAuth mAuthencation;
    DatabaseReference mData;
    FirebaseUser mFirebaseUser;
    ProgressDialog mProcessDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.dangky);

        mProcessDlg = new ProgressDialog(RegisterActivity.this);
        mProcessDlg.setMessage("Vui lòng đợi");
        mapViews();
        mAuthencation = FirebaseAuth.getInstance();
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessDlg.show();
                registerUser();
            }
        });
        mData = FirebaseDatabase.getInstance().getReference();
    }

    private void mapViews(){
        etEmail = findViewById(R.id.edtRegEmail);
        etPass = findViewById(R.id.edtRegPass);
        etName = findViewById(R.id.edtRegName);
        etPhone = findViewById(R.id.edtRegTelephone);
        etAddress = findViewById(R.id.edtRegAddress);
        btnDangKy= findViewById(R.id.btnRegister);
    }


    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        final String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String Address = etAddress.getText().toString().trim();
        final User user_register    = new User(email,pass,name,phone,Address,"user");

        if (email.isEmpty() || pass.isEmpty() || name.isEmpty() || phone.isEmpty() || Address.isEmpty()) {
            mProcessDlg.dismiss();
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin. ", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuthencation.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                mFirebaseUser = mAuthencation.getCurrentUser();

                                mFirebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProcessDlg.dismiss();
                                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công. Vui lòng xác thực email", Toast.LENGTH_SHORT).show();

                                                    //set name cho user
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(name)
                                                            .setPhotoUri(null)
                                                            .build();
                                                    mFirebaseUser.updateProfile(profileUpdates)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                }
                                                            });
                                                    //push data len realtime database
                                                    String userID= mFirebaseUser.getUid();
                                                    mData.child("User").child(userID).setValue(user_register);
                                                    //chuyen ve man hinh chinh
                                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });



                            }
                            else {
                                mProcessDlg.dismiss();
                                Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}