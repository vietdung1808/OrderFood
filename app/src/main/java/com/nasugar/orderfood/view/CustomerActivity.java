package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.StorageReference;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.interfaces.FragmentCommunicator;
import com.nasugar.orderfood.view.fragment.HomeFragment;
import com.nasugar.orderfood.view.fragment.ProfileFragment;
import com.nasugar.orderfood.view.fragment.UserOrdersFragment;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class CustomerActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_CAMERA = 111;
    public static final int REQUEST_CODE_FOLDER = 222;

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private FragmentCommunicator mFragmentCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        loadFragment(new HomeFragment(), "home");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mItemSelectedListener);
        bottomNavigationView.requestFocus();
    }

    public void passVal(FragmentCommunicator fragmentCommunicator) {
        this.mFragmentCommunicator = fragmentCommunicator;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(new HomeFragment(), "home");
                    return true;
                case R.id.navigation_profile:
                    loadFragment(new ProfileFragment(), "profile");
                    return true;
                case R.id.navigation_orders:
                    loadFragment(new UserOrdersFragment(), "orders");
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment, String tag) {
        if (mFragmentManager.findFragmentByTag(tag) != null) {
            mFragmentManager.popBackStack(tag, 0);
        } else {
            // load fragment
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.frame_container, fragment, tag);
            transaction.addToBackStack(tag);
            transaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }else if (requestCode == REQUEST_CODE_FOLDER && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_FOLDER);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            sendBitmapToFragmentProfile(bitmap);
        } else if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                sendBitmapToFragmentProfile(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendBitmapToFragmentProfile(Bitmap bitmap) {
        mFragmentCommunicator.passData(bitmap);
    }

}