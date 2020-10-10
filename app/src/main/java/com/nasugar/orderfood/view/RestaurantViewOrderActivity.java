package com.nasugar.orderfood.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nasugar.orderfood.adapter.RestaurentViewOrderAdapter;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.Order;
import com.nasugar.orderfood.model.Orders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RestaurantViewOrderActivity extends AppCompatActivity {
    ListView listOrder;
    ArrayList<Orders> arrOrder;
    RestaurentViewOrderAdapter adapter = null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    DatabaseReference mDatabase;
    Button btnThoat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_restaurant_view_order );

        btnThoat =(Button) findViewById(R.id.btnThoatXemdonhangQuanan);
        listOrder  =   (ListView) findViewById(R.id.listRestaurent_viewOrder);
        arrOrder = new ArrayList<>();
        adapter = new RestaurentViewOrderAdapter(this, R.layout.item_restaurent_view_order, arrOrder);
        listOrder.setAdapter(adapter);
        getInfoOrder();

        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantViewOrderActivity.this, RestaurantActivity.class));
            }
        });


        listOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Orders order = arrOrder.get(position);

                Intent OrderDetail = new Intent(RestaurantViewOrderActivity.this, DetailOrderActivity.class);
                //gửi FoodId (ten của Food) và id quán đến activity FoodDetail
                OrderDetail.putExtra("TotalAmount",order.getTotalAmount());
                OrderDetail.putExtra("CustomerName",order.getUserName());
                OrderDetail.putExtra("OrderID",order.getOrderId());
                OrderDetail.putExtra("Status",order.getStatus());
                OrderDetail.putExtra("UserID",order.getUserId());
                // mở activity  foodDetail
                startActivity(OrderDetail);
            }
        });

    }

    private void getInfoOrder(){
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Orders");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get date-time
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy   hh:mm:ss aa");
                final String dateCurrent = dateFormat.format(c.getTime());

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        if (ds1.getValue() != null) {
                            Orders orders = ds1.getValue( Orders.class );
                            /*0: Chờ xử lý; 1: đang giao; 2: đã giao; 3: Hủy*/
                            if (orders.getStatus() == 0 || orders.getStatus() == 1) {
                                arrOrder.add( orders );
                                adapter.notifyDataSetChanged();

                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Integer getDayTime(String date){
        return Integer.parseInt(date.substring(0,2));
    }
}