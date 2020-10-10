package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nasugar.orderfood.R;

public class ForgotPassActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnForgotPass;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Quên mật khẩu");

        etEmail = findViewById(R.id.editText_forgot_pass_email);
        btnForgotPass = findViewById(R.id.button_forgot_pass);

        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                if(email.isEmpty()) {
                    Toast.makeText(ForgotPassActivity.this, "Vui lòng điền thông tin email", Toast.LENGTH_SHORT).show();
                }
                else {

                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPassActivity.this, "Yêu cầu thiết lập lại mật khẩu đã được gửi tới email của bạn.", Toast.LENGTH_LONG).show();
//                                        startActivity(new Intent(ForgotPassActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(ForgotPassActivity.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}