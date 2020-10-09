package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.nasugar.orderfood.adapter.UserOrderDetailAdapter;
import com.nasugar.orderfood.model.Cart;
import com.nasugar.orderfood.model.Orders;

import java.util.ArrayList;
import java.util.List;

public class UserOrderDetailActivity extends AppCompatActivity {

    TextView tvOrderId, tvTotalAmount, tvStatus;
    RecyclerView rcvFood;
    Button btnCancel;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private List<Cart> mFoodList;
    private String mOrderId;
    private UserOrderDetailAdapter mUserOrderDetailAdapter;
    private Orders mOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapViews();
        init();
        loadData();
        setListener();
        
    }

    private void setListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder = new AlertDialog.Builder(UserOrderDetailActivity.this)
                        .setMessage("Bạn chắc chắn muốn hủy đơn hàng này?")
                        .setCancelable(false)
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOrders.setStatus(3);
                                DatabaseReference database = myRef.child("Orders/" + user.getUid() + "/" + mOrderId);
                                
                                database.setValue(mOrders).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(UserOrderDetailActivity.this, "Đơn hàng đã được hủy", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(UserOrderDetailActivity.this, "Đơn hàng chưa được hủy", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .show();
            }
        });
    }

    private void loadData() {
        DatabaseReference database = myRef.child("Orders/" + user.getUid() + "/" + mOrderId);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mOrders = snapshot.getValue(Orders.class);
                setValueDataControls(mOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setValueDataControls(Orders orders) {
        mFoodList.clear();
        tvOrderId.setText(mOrderId);
        switch (orders.getStatus()) {
            case 0:
                tvStatus.setText("Chờ xác nhận");
                btnCancel.setVisibility(View.VISIBLE);
                break;
            case 1:
                tvStatus.setText("Đang giao");
                break;
            case 2:
                tvStatus.setText("Đã giao");
                break;
            case 3:
                tvStatus.setText("Đã hủy");
                break;
        }
        tvTotalAmount.setText(orders.getTotalAmount());
        mFoodList.addAll(orders.getFoodList());
        mUserOrderDetailAdapter.notifyDataSetChanged();
    }

    private void init() {
        mOrderId = getIntent().getStringExtra("OrderId");
        this.setTitle("Chi tiết đơn hàng");

        mFoodList = new ArrayList<>();
        mUserOrderDetailAdapter = new UserOrderDetailAdapter(this, mFoodList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvFood.setLayoutManager(layoutManager);
        rcvFood.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rcvFood.setItemAnimator(new DefaultItemAnimator());
        rcvFood.setAdapter(mUserOrderDetailAdapter);
    }

    private void mapViews() {
        tvOrderId = findViewById(R.id.textview_user_orders_detail_orderid);
        tvStatus = findViewById(R.id.textview_user_orders_detail_status);
        tvTotalAmount = findViewById(R.id.textView_user_order_detail_total_amount);
        rcvFood = findViewById(R.id.recycler_view_order_detail);
        btnCancel = findViewById(R.id.button_user_order_detail_cancel);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}