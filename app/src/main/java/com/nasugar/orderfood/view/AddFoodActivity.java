package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.MonAn;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AddFoodActivity extends AppCompatActivity {
    private Button themMon,folder;
    private EditText tenMon,giaMon;
    private ImageView image;
    private int REQUEST_CODE_FOLDER = 123;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference refData = FirebaseDatabase.getInstance().getReference();
    final StorageReference storageRef = storage.getReferenceFromUrl("gs://orderfood-f6895.appspot.com//Food_Images");
    private String link ;
    AlertDialog waiting;
    Spinner spinner;
    final List<String> list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_food );
        AnhXa();

        refData.child("FoodCatalogue").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                    FoodCategory titlename = dataSnapshot1.getValue( FoodCategory.class);
                    list.add(titlename.getName());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddFoodActivity.this, android.R.layout.simple_spinner_item, list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddFoodActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        } );

        waiting =  new SpotsDialog.Builder().setContext(this).setMessage("Vui lòng đợi").setCancelable(false).build();

        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
            }
        });

        themMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.show();
                final String ten  =   tenMon.getText().toString();
                final String stringGia = giaMon.getText().toString();

                if(ten.isEmpty()|| stringGia.isEmpty()){
                    waiting.dismiss();
                    Toast.makeText(AddFoodActivity.this, "Vui lòng nhập đầy đủ thông tin ", Toast.LENGTH_SHORT).show();
                }
                else{
                    final long gia    =   Long.parseLong(stringGia);
                    Calendar calendar = Calendar.getInstance();
                    final String tenhinh="image"+calendar.getTimeInMillis();
                    final StorageReference mountainsRef = storageRef.child(tenhinh+".png");
                    image.setDrawingCacheEnabled(true);
                    image.buildDrawingCache();

                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    final UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get downloadUrl
                            Task<Uri> urlTask = uploadTask.continueWithTask( new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    link = mountainsRef.getDownloadUrl().toString();
                                    // Continue with the task to get the download URL
                                    return mountainsRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        waiting.dismiss();
                                        link = task.getResult().toString();
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String IDQuan = user.getUid();
                                        MonAn Mon= new MonAn(user.getDisplayName(),IDQuan,ten,link,gia,1);
                                        DatabaseReference refData = FirebaseDatabase.getInstance().getReference();
                                        refData.child("QuanAn").child(IDQuan).child(ten).setValue(Mon);
                                        Toast.makeText(AddFoodActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AddFoodActivity.this, ViewListFoodActivity.class));
                                    } else {
                                        Toast.makeText(AddFoodActivity.this, "Thêm không thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });


                }

            }
        });

    }
    // chọn ảnh từ file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void AnhXa(){

        themMon = findViewById(R.id.btnThemMonLayoutThemMon);
        folder  = findViewById(R.id.btnfolder);
        tenMon  = findViewById(R.id.edtTenMon);
        giaMon  = findViewById(R.id.edtGiaMon);
        image   = findViewById(R.id.ivImage);
        spinner = (Spinner)findViewById(R.id.spinnerFoodCategory);
    }
}