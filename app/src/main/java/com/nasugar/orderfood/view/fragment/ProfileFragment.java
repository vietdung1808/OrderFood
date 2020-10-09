package com.nasugar.orderfood.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.User;
import com.nasugar.orderfood.view.RegisterActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private CircleImageView imgProfile;
    private ImageView imgCapture, imgFolder;
    private EditText etEmail, etName, etPhone, etAddress;
    private TextView tvName;
    private Button btnUpdate;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase;

    private User mUser;

    ProgressDialog mProcessDlg;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mapViews(view);
        init();
        loadData();
        setListener();

        return view;
    }

    private void init() {
        etEmail.setEnabled(false);
        mDatabase = myRef.child("User/" + firebaseUser.getUid());

        mProcessDlg = new ProgressDialog(getContext());
        mProcessDlg.setMessage("Vui lòng đợi");
    }

    private void setListener() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessDlg.show();
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    mProcessDlg.dismiss();
                    Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin. ", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile(name, phone, address);
                }
            }
        });
    }

    private void updateProfile(String name, String phone, String address) {
        mUser.setName(name);
        mUser.setPhone(phone);
        mUser.setAddress(address);

        mDatabase.setValue(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
//        mProcessDlg.dismiss();
    }


    private void loadData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser = snapshot.getValue(User.class);
                setValueDataControl(mUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setValueDataControl(User mUser) {
        if (firebaseUser.getPhotoUrl() != null) {
            Picasso.with(getContext()).load(firebaseUser.getPhotoUrl()).into(imgProfile);
        }
        etEmail.setText(firebaseUser.getEmail());

        etName.setText(mUser.getName());
        tvName.setText(mUser.getName());
        etPhone.setText(mUser.getPhone());
        etAddress.setText(mUser.getAddress());
    }

    private void mapViews(View view) {
        imgProfile = view.findViewById(R.id.circleImageView_profile_image_profile);
        imgCapture = view.findViewById(R.id.imageView_profile_capture);
        imgFolder = view.findViewById(R.id.imageView_profile_folder);
        etEmail = view.findViewById(R.id.editText_profile_email);
        etName = view.findViewById(R.id.editText_profile_name);
        etPhone = view.findViewById(R.id.editText_profile_phone);
        etAddress = view.findViewById(R.id.editText_profile_address);
        tvName = view.findViewById(R.id.textView_profile_name);
        btnUpdate = view.findViewById(R.id.button_profile_update);


    }
}