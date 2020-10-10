package com.nasugar.orderfood.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.interfaces.FragmentCommunicator;
import com.nasugar.orderfood.model.Common;
import com.nasugar.orderfood.model.User;
import com.nasugar.orderfood.view.ChangePassActivity;
import com.nasugar.orderfood.view.CustomerActivity;
import com.nasugar.orderfood.view.LoginActivity;
import com.nasugar.orderfood.view.RegisterActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;


public class ProfileFragment extends Fragment {
    private CircleImageView imgProfile;
    private ImageView imgCapture, imgFolder;
    private EditText etEmail, etName, etPhone, etAddress;
    private TextView tvName, tvChangePass, tvLogout;
    private Button btnUpdate;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    DatabaseReference mDatabase;

    private User mUser;
    private String link_image;

    ProgressDialog mProcessDlg;
    AlertDialog waiting;


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
        waiting =  new SpotsDialog.Builder().setContext(getContext()).setMessage("Đang upload...").setCancelable(false).build();
    }

    private void setListener() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessDlg.show();
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                Uri photoUrl = null;

                if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    mProcessDlg.dismiss();
                    Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin. ", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile(name, phone, address, photoUrl);
                }
            }
        });

        imgFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CustomerActivity.REQUEST_CODE_FOLDER);
            }
        });

        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        CustomerActivity.REQUEST_CODE_CAMERA);
            }
        });

        ((CustomerActivity) getActivity()).passVal(new FragmentCommunicator() {
            @Override
            public void passData(Bitmap bitmap) {
                waiting.show();

                imgProfile.setImageBitmap(bitmap);

                Calendar calendar = Calendar.getInstance();
                String tenhinh = "image" + calendar.getTimeInMillis();
                final StorageReference mountainsRef = storageRef.child("img_profile/" + tenhinh + ".jpeg");
                imgProfile.setDrawingCacheEnabled(true);
                imgProfile.buildDrawingCache();

                Bitmap bitmap1 = ((BitmapDrawable) imgProfile.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data1 = baos.toByteArray();

                final UploadTask uploadTask = mountainsRef.putBytes(data1);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // get downloadUrl
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                link_image = mountainsRef.getDownloadUrl().toString();
                                // Continue with the task to get the download URL
                                return mountainsRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    link_image = task.getResult().toString();
                                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(Uri.parse(link_image))
                                            .build();
                                    firebaseUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                firebaseUser.reload();
                                                Picasso.with(getContext()).load(firebaseUser.getPhotoUrl()).into(imgProfile);
                                            }
                                        }
                                    });
                                    Toast.makeText(getActivity(), "Thêm ảnh thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Thêm ảnh không thành công", Toast.LENGTH_SHORT).show();
                                }
                                waiting.dismiss();
                            }
                        });
                    }
                });
            }
        });

        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePassActivity.class));
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.init(getContext());
                Paper.book().delete(Common.PWD_KEY);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void updateProfile(String name, String phone, String address, Uri photoUrl) {
        mUser.setName(name);
        mUser.setPhone(phone);
        mUser.setAddress(address);

        mDatabase.setValue(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(photoUrl)
                            .build();
                    firebaseUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                firebaseUser.reload();
                                tvName.setText(firebaseUser.getDisplayName());
                            }
                        }
                    });

                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }

                mProcessDlg.dismiss();
            }
        });
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
        tvName.setText(firebaseUser.getDisplayName());

        etName.setText(mUser.getName());
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
        tvChangePass = view.findViewById(R.id.textView_profile_change_pass);
        tvLogout = view.findViewById(R.id.textView_profile_logout);

    }

}