package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.nasugar.orderfood.adapter.CartAdapter;
import com.nasugar.orderfood.interfaces.OnCartListener;
import com.nasugar.orderfood.model.Cart;
import com.nasugar.orderfood.model.Orders;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    RecyclerView rcvCart;
    Button btnDatHang;
    TextView tvTongTien;

    private List<Cart> mCartList;
    private CartAdapter mCartAdapter;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user;

    private DecimalFormat decimalFormat = new DecimalFormat("###,### VNĐ");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mapViews();
        init();
        loadCart();
        setListener();
    }

    private void setListener() {
        mCartAdapter.setOnItemClickListener(new OnCartListener() {
            @Override
            public void onCartButtonPlusClick(Cart cart) {
                cart.setSoluong(cart.getSoluong() + 1);
                cart.setTongTien(cart.getSoluong() * cart.getGiaMon());
                DatabaseReference database = myRef.child("Carts/" + user.getUid() + "/" + cart.getTenMon());
                database.setValue(cart);
            }

            @Override
            public void onCartButtonMinusClick(Cart cart) {
                DatabaseReference database = myRef.child("Carts/" + user.getUid() + "/" + cart.getTenMon());
                if (cart.getSoluong() > 1) {
                    cart.setSoluong(cart.getSoluong() - 1);
                    cart.setTongTien(cart.getSoluong() * cart.getGiaMon());
                } else {
                    cart = null;
                }
                database.setValue(cart);
            }
        });

        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogConfirm = new Dialog(CartActivity.this,R.style.Theme_Dialog);
                dialogConfirm.setContentView(R.layout.dialog_confirmcart);
                dialogConfirm.setCanceledOnTouchOutside(false);

                EditText etAddress = dialogConfirm.findViewById(R.id.textview_diachigiaohang);
                TextView btnCancel = dialogConfirm.findViewById(R.id.textview_cancelCart);
                TextView btnConfirm = dialogConfirm.findViewById(R.id.textview_confirmCart);
                dialogConfirm.show();
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirm.dismiss();
                    }
                });

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String address = etAddress.getText().toString().trim();
                        if (address.length() == 0) {
                            Toast.makeText(CartActivity.this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String orderDate = dateFormat.format(new Date());
                        Orders orders = new Orders(
                                user.getUid(),
                                orderDate,
                                address,
                                tvTongTien.getText().toString(),
                                0,
                                mCartList
                        );

                        DatabaseReference database = myRef.child("Orders/" + user.getUid() + "/" + orderDate);
                        database.setValue(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CartActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                    dialogConfirm.dismiss();
                                } else {
                                    Toast.makeText(CartActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    private void init() {
        this.setTitle("Giỏ hàng");
        mCartList = new ArrayList<>();
        mCartAdapter = new CartAdapter(this, mCartList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvCart.setLayoutManager(layoutManager);
        rcvCart.setItemAnimator(new DefaultItemAnimator());
        rcvCart.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rcvCart.setAdapter(mCartAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void mapViews() {
        rcvCart = findViewById(R.id.recycler_view_card_food);
        btnDatHang = findViewById(R.id.button_cart_confirm);
        tvTongTien = findViewById(R.id.textView_cart_amount);
    }

    private void loadCart() {
        DatabaseReference database = myRef.child("Carts/" + user.getUid());

        database.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCartList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Cart cart = item.getValue(Cart.class);
                    mCartList.add(cart);
                }
                mCartAdapter.notifyDataSetChanged();

                //Sum total amount
                Long amount = mCartList.stream().mapToLong(a -> a.getTongTien()).sum();
                tvTongTien.setText(decimalFormat.format(amount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}