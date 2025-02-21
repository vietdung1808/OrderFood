package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.Token;
import com.nasugar.orderfood.model.User;

import io.paperdb.Paper;


public class RestaurantActivity extends AppCompatActivity {
    Button  xemDSMon, xemDonDatHang, doiMK, update;
    ImageView LogOut;
    TextView tenQuan;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference( "User" ).child( user.getUid() );

    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
            return super.moveDatabaseFrom( sourceContext, name );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_restaurant );

//
        updateToken( FirebaseInstanceId.getInstance().getToken());
//
        // Paper init
        Paper.init(this);

        AnhXa();

        tenQuan.setText( "Quán " + user.getDisplayName() );

        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DangXuat();
            }
        });

        doiMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this,ChangePassActivity.class));
            }
        });


        xemDSMon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( RestaurantActivity.this, ViewListFoodActivity.class ) );
            }
        } );

        xemDonDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, RestaurantViewOrderActivity.class));
            }
        });

// Cập nhật thông tin của quán ăn
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogUpdate();
            }
        });

    }

    private void AnhXa() {
        LogOut = (ImageView) findViewById( R.id.btnLogOutQuan );
        doiMK = (Button) findViewById( R.id.btnChangePassQuan );
        tenQuan = (TextView) findViewById( R.id.twTenQuan );
        xemDSMon = (Button) findViewById( R.id.btnXemMon );
        xemDonDatHang = (Button) findViewById( R.id.btnXemDonHang );
        update = findViewById( R.id.btnUpdateInfoRestaurant );
    }

    private void DangXuat() {
        final Dialog dialogLogOut = new Dialog(RestaurantActivity.this,R.style.Theme_Dialog);
        dialogLogOut.setContentView(R.layout.dialog_dang_xuat);
        dialogLogOut.show();
        Button khong=(Button) dialogLogOut.findViewById(R.id.btnKhongDialogDangXuat);
        Button thoat=(Button) dialogLogOut.findViewById((R.id.btnDialogDangXuat));
        khong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogOut.cancel();
            }
        });

        thoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete remember user and password
                Paper.book().destroy();
                dialogLogOut.cancel();
                startActivity(new Intent(RestaurantActivity.this,LoginActivity.class));
            }
        });
    }

    private void showDialogUpdate() {
        final Dialog dialog   = new Dialog(RestaurantActivity.this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_update_info);
        dialog.show();
        final EditText name = (EditText) dialog.findViewById(R.id.updateName);
        final EditText address = (EditText) dialog.findViewById(R.id.updateAddress);
        final EditText phone = (EditText) dialog.findViewById(R.id.updatePhone);
        Button update = (Button) dialog.findViewById(R.id.btnUpdate);
        Button cancel = (Button) dialog.findViewById(R.id.btnCancel);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                address.setText(user.getAddress());
                phone.setText(user.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Name = name.getText().toString();
                final String Address = address.getText().toString();
                final String Phone = phone.getText().toString();
                if(Name.isEmpty() || Address.isEmpty() || Phone.isEmpty()){
                    Toast.makeText(RestaurantActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RestaurantActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    mDatabase.child("name").setValue(Name);
                    mDatabase.child("address").setValue(Address);
                    mDatabase.child("phone").setValue(Phone);

                    // Dung de cap nhat thong tin user profile cho quan an
                    if( user != null)
                    {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(Name.toString().trim()).build();

                        user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("Testing", "User Profile Updated");
                                }
                            }
                        });
                    }

                }
            }
        });
    }

    private void updateToken (String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token,2);
        reference.child(user.getUid()).setValue(token1);
    }


}