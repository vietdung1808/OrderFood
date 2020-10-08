package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
import com.nasugar.orderfood.adapter.FoodAdapter;
import com.nasugar.orderfood.interfaces.OnFoodListener;
import com.nasugar.orderfood.model.FoodCatalogue;
import com.nasugar.orderfood.model.MonAn;

import java.util.ArrayList;
import java.util.List;

public class FoodActivity extends AppCompatActivity {
    private static final String TAG = "BBB";
    private RecyclerView rcvFood;

    private List<MonAn> mMonAnList;
    private FoodAdapter mFoodAdapter;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        FoodCatalogue foodCatalogue = (FoodCatalogue) intent.getSerializableExtra("FoodCatalogue");
        this.setTitle(foodCatalogue.getName());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvFood = findViewById(R.id.recycler_view_food);
        rcvFood.setLayoutManager(layoutManager);
        rcvFood.setItemAnimator(new DefaultItemAnimator());

        mMonAnList = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(this, mMonAnList);
        rcvFood.setAdapter(mFoodAdapter);

        getItemData();
        setOnListener();
    }

    private void setOnListener() {
        mFoodAdapter.setOnItemClickListener(new OnFoodListener() {
            @Override
            public void onAddToCartClick(MonAn monAn) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference database = myRef.child("Carts/" + user.getUid());
                database.setValue(monAn).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(FoodActivity.this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(FoodActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }


    private void getItemData() {

        DatabaseReference database = myRef.child("QuanAn");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemList : snapshot.getChildren()) {
                    for (DataSnapshot item : itemList.getChildren()) {
                        MonAn monAn = item.getValue(MonAn.class);
                        mMonAnList.add(monAn);
                    }
                }
                Log.d(TAG, "onDataChange: " + mMonAnList.size());
                mFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return super.onCreateOptionsMenu(menu);
    }
}