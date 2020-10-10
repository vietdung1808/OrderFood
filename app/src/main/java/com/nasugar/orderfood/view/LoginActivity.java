package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.Common;
import com.nasugar.orderfood.model.User;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    final public static String TAG = "BBB";

    DatabaseReference mData;
    FirebaseAuth mAuthencation;
    Button btnLog;
    EditText etEmail;
    EditText etPass;

    CheckBox chkRemember;
    TextView tvForgotPass, tvRegister;

    AlertDialog dlgWaiting;

    String mEmail, mPass;


    AlertDialog waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mapViews();
        dlgWaiting = new AlertDialog.Builder(this).setMessage("Vui lòng đợi...").setCancelable(false).create();
        mAuthencation = FirebaseAuth.getInstance();

        Paper.init(this);
        mEmail = Paper.book().read(Common.USER_KEY,"");
        mPass = Paper.book().read(Common.PWD_KEY,"");
        etEmail.setText(mEmail);
        etPass.setText(mPass);
        if (mEmail.length() > 0 && mPass.length() > 0) {
            DangNhap(mEmail, mPass, true);
        }

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = etEmail.getText().toString().trim();
                mPass = etPass.getText().toString().trim();
                DangNhap(mEmail, mPass, false);
            }
        });


        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPassActivity.class));
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void mapViews() {
        btnLog = findViewById(R.id.btnLog);
        etEmail = findViewById(R.id.edtEmailLog);
        etPass = findViewById((R.id.edtPassLog));
        chkRemember = findViewById(R.id.ckbRemember);
        tvForgotPass = findViewById(R.id.forgotPass);
        tvRegister = findViewById(R.id.textViewRegister);
    }

    private void DangNhap(String email, final String pass, final boolean auto_login) {
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin. ", Toast.LENGTH_SHORT).show();
        } else {
            if (isNetworkAvailable()) {
                dlgWaiting.show();
                mAuthencation.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
                            String userID = USER.getUid();

                            mData = FirebaseDatabase.getInstance().getReference().child("User").child(userID);

                            mData.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dlgWaiting.dismiss();
                                    if (chkRemember.isChecked()) {
                                        Paper.book().write(Common.USER_KEY, email);
                                        Paper.book().write(Common.PWD_KEY, pass);
                                    }
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user.getUserType().equals("admin")) {
                                        startActivity(new Intent(LoginActivity.this, RestaurantActivity.class));
                                    } else if (user.getUserType().equals("user")) {
                                        if (USER.isEmailVerified()) {
                                            startActivity(new Intent(LoginActivity.this, CustomerActivity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Vui lòng xác thực Email để đăng nhập", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    mData.removeEventListener(this);
//                                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("BBB", "onCancelled: " + databaseError.getMessage());
                                    Toast.makeText(LoginActivity.this, "Dang nhap that bai", Toast.LENGTH_SHORT).show();
                                }
                            });


                            // ghi lai mk trong database neu quen mat kau sau khi lay lai
                            if (!auto_login) {
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(userID);
                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        dataSnapshot.child("pass").getRef().setValue(pass);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                };
                                mDatabase.addListenerForSingleValueEvent(eventListener);
                            }

                        } else {
                            dlgWaiting.dismiss();
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Bạn chưa kết nối Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // kiểm tra kết nối internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}