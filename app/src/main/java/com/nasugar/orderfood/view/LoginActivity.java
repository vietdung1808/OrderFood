package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.User;

public class LoginActivity extends AppCompatActivity {

    final public static String TAG = "BBB";

    DatabaseReference mData;
    FirebaseAuth mAuthencation;
    Button btnLog;
    EditText email;
    EditText pass;

    CheckBox Remember;
    TextView forgotPass;

    AlertDialog waiting;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        AnhXa();
//        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Vui lòng đợi...").setCancelable(false).build();
        mAuthencation = FirebaseAuth.getInstance();

//        Paper.init(this);

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DangNhap();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this,ForgotPassActivity.class));
            }
        });
    }

    private void AnhXa() {
        btnLog = (Button) findViewById(R.id.btnLog);
        email = (EditText) findViewById(R.id.edtEmailLog);
        pass = (EditText) findViewById((R.id.edtPassLog));
        Remember = (CheckBox) findViewById(R.id.ckbRemember);
        forgotPass = (TextView) findViewById(R.id.forgotPass);
    }

    private void DangNhap() {
        final String Email = email.getText().toString().trim();
        final String Pass = pass.getText().toString().trim();
        if (Email.isEmpty() || Pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin. ", Toast.LENGTH_SHORT).show();
        } else {
            if (isNetworkAvailable()) {
//                waiting.show();
//                if (Remember.isChecked()) {
//                    Paper.book().write(Common.USER_KEY, Email);
//                    Paper.book().write(Common.PWD_KEY, Pass);
//                }
                mAuthencation.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
                            String userID = USER.getUid();


//                            mData = FirebaseDatabase.getInstance().getReference().child("User").child(userID);


                            myRef.child("SinhVien").setValue("Nguyen Van A")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Them thanh cong", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Them that bai", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });


//                            mData.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                    waiting.dismiss();
////                                    User user = dataSnapshot.getValue(User.class);
//                                    Toast.makeText(LoginActivity.this, "Dang nhap thanh cong /n" , Toast.LENGTH_SHORT).show();
//
////                                    if (user.getUserType().equals("admin")) {
////                                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
////                                    } else if (user.getUserType().equals("restaurent")) {
////                                        startActivity(new Intent(LoginActivity.this, RestaurantActivity.class));
////                                    } else if (user.getUserType().equals("customer")) {
////
////                                        if (USER.isEmailVerified()) {
////                                            startActivity(new Intent(LoginActivity.this, KhachHangActivity.class));
////                                        } else {
////                                            Toast.makeText(LoginActivity.this, "Vui lòng xác thực Email để đăng nhập", Toast.LENGTH_SHORT).show();
////                                        }
////
////                                    }
////                                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    Log.d("BBB", "onCancelled: " + databaseError.getMessage());
//                                }
//                            });


                            // ghi lai mk trong database neu quen mat kau sau khi lay lai

//                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
//                            ValueEventListener eventListener = new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    dataSnapshot.child("pass").getRef().setValue(Pass);
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            };
//                            mDatabase.addListenerForSingleValueEvent(eventListener);

                        } else {
//                            waiting.dismiss();
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