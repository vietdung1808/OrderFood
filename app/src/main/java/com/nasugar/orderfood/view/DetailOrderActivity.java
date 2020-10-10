package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nasugar.orderfood.Notifications.APIService;
import com.nasugar.orderfood.Notifications.MyResponse;
import com.nasugar.orderfood.Notifications.Notification;
import com.nasugar.orderfood.Notifications.Sender;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.adapter.CartAdapter;
import com.nasugar.orderfood.adapter.CustomerOrderAdapter;
import com.nasugar.orderfood.adapter.ViewFoodAdapter;
import com.nasugar.orderfood.model.Cart;
import com.nasugar.orderfood.model.Common;
import com.nasugar.orderfood.model.MonAn;
import com.nasugar.orderfood.model.Order;
import com.nasugar.orderfood.model.Orders;
import com.nasugar.orderfood.model.Token;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderActivity extends AppCompatActivity {
    String TotalAmount = "";
    String CustomerName = "";
    String OrderID = "";
    Integer Status ;
    String UserID = "";

    RadioGroup radioGroup;
    TextView textView_Total_Amount, textView_Customer_Name;
    FButton xacnhan;
    FButton thoat;
    RadioButton dagiao, danggiao, hethang;

    RecyclerView recyclerViewFood;
    ArrayList<Cart> arrCart = new ArrayList<>();
    CustomerOrderAdapter viewFoodAdapter;
    Cart cart;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    String userid;

    DatabaseReference database;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_order );
        AnhXa();
        mService = Common.getFCMService();

        //Nhận THông tin Order từ Intent gửi đến
        Intent intent = getIntent();
        if (intent != null) {
            TotalAmount = intent.getStringExtra( "TotalAmount" );
            CustomerName = intent.getStringExtra( "CustomerName" );
            Status = intent.getIntExtra( "Status" ,0);
            OrderID = intent.getStringExtra( "OrderID" );
            UserID = intent.getStringExtra( "UserID" );

        }
        if (!TotalAmount.isEmpty() && TotalAmount != null && !CustomerName.isEmpty() && CustomerName != null) {
            initRecyclerView();
            getDataOrder( UserID, OrderID );
        }

        switch (Status ) {

            case 1:
                danggiao.setChecked(true);
                dagiao.setEnabled( true );
                hethang.setEnabled( false );
                break;
            case 2:
                dagiao.setChecked(true);
                break;
            case 3:
                hethang.setChecked(true);
                break;
        }
        xacnhan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dagiao.isChecked() || danggiao.isChecked() || hethang.isChecked()) {
                    String status = "";
                    if (dagiao.isChecked()) {
                        status = "đã giao";
                        mDatabase.child("Orders").child(UserID).child(OrderID).child("status").setValue(2);
                    } else if (danggiao.isChecked()) {
                        status = "đang giao";
                        mDatabase.child("Orders").child(UserID).child(OrderID).child("status").setValue(1);
                    } else if (hethang.isChecked()) {
                        status = "Hủy đơn hàng";
                        mDatabase.child("Orders").child(UserID).child(OrderID).child("status").setValue(3);
                    }

                    //send notification
                    sendNotification(OrderID,CustomerName,status,UserID);

                    Toast.makeText( DetailOrderActivity.this, "Đã xác nhận đơn hàng", Toast.LENGTH_SHORT ).show();
                    startActivity( new Intent( DetailOrderActivity.this, RestaurantViewOrderActivity.class ) );
                } else {
                    Toast.makeText( DetailOrderActivity.this, "Vui lòng chọn tình trạng giao hàng", Toast.LENGTH_SHORT ).show();
                }

            }
        } );

        thoat.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( DetailOrderActivity.this, RestaurantViewOrderActivity.class ) );
            }
        } );
    }

    private void initRecyclerView() {

        recyclerViewFood.setHasFixedSize( true );
        LinearLayoutManager layoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false );
        recyclerViewFood.setLayoutManager( layoutManager );
        recyclerViewFood.setItemAnimator( new DefaultItemAnimator() );
        viewFoodAdapter = new CustomerOrderAdapter( getApplicationContext(), arrCart );
        recyclerViewFood.setAdapter( viewFoodAdapter );
    }

    private void AnhXa() {

        textView_Customer_Name = findViewById( R.id.textview_Detail_Order_Customer_Name );
        textView_Total_Amount = findViewById( R.id.textview_Detail_Order_TotalAmount );
        radioGroup = (RadioGroup) findViewById( R.id.radioGroupShip );
        xacnhan = (FButton) findViewById( R.id.xacnhan );
        thoat = (FButton) findViewById( R.id.fbThoat );
        dagiao = (RadioButton) findViewById( R.id.dagiao );
        danggiao = (RadioButton) findViewById( R.id.danggiao );
        hethang = (RadioButton) findViewById( R.id.hethang );
        recyclerViewFood = findViewById( R.id.recycler_view_order_detail_food_list );

    }

    private void getDataOrder(String userID, String orderID) {

        mDatabase.child( "Orders" ).child( userID ).child( orderID ).child( "foodList" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                // đối tượng cart order lấy dữ liệu từ database
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue() != null) {
                        cart = ds.getValue( Cart.class );
                        arrCart.add( cart );
                        viewFoodAdapter.notifyDataSetChanged();
                        textView_Customer_Name.setText( CustomerName );
                        textView_Total_Amount.setText( TotalAmount );
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );
    }

    private void sendNotification(final String orderID, final String nameCustomer, final String status, final String ID) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference( "Tokens" );
        Query data = tokens.orderByChild( "checkToken" ).equalTo( 1 ); // get all node isServerToken is false
        data.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ID.equals( ds.getKey() )) {
                        Token customerToken = ds.getValue( Token.class );
                        Notification notification = new Notification( orderID + " được xác nhận " + status, "Chào! " + nameCustomer );
                        Sender content = new Sender( customerToken.getToken(), notification );

                        mService.sendNotification( content ).enqueue( new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body().success == 1) {
                                        //Toast.makeText(CartActivity.this, "thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Toast.makeText(CartActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.e( "Error", t.getMessage() );
                            }
                        } );
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}