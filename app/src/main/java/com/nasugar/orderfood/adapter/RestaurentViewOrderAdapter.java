package com.nasugar.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.Order;
import com.nasugar.orderfood.model.Orders;
import com.squareup.picasso.Picasso;


import java.util.List;

public class RestaurentViewOrderAdapter extends BaseAdapter {
//    FirebaseUser user = FirebaseAuth.getInstance();
    private Context context;
    private int layout;
    private List<Orders> listOrder;

    public RestaurentViewOrderAdapter(Context context, int layout, List<Orders> listOrder) {
        this.context = context;
        this.layout = layout;
        this.listOrder = listOrder;
    }


    @Override
    public int getCount() {
        return listOrder.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        //anh xa view
        TextView diachi = (TextView) view.findViewById( R.id.textview_Customer_orders_address);
        TextView tenKH = (TextView) view.findViewById(R.id.textview_Customer_Name);
        TextView tongtien = (TextView) view.findViewById(R.id.textview_Customer_orders_amount);
        TextView tinhtrang = (TextView) view.findViewById(R.id.textview_Customer_orders_status);
        TextView ngaydathang = (TextView) view.findViewById(R.id.textview_Customer_orders_date);


        //set value
        Orders order = listOrder.get(position);
        diachi.setText(order.getAddress());
        tenKH.setText(order.getUserName());
        tongtien.setText(order.getTotalAmount());
        ngaydathang.setText( order.getOrderDate() );
        switch (order.getStatus()) {
            case 0:
                tinhtrang.setText( "Chờ xử lý" );
                break;
            case 1:
                tinhtrang.setText( "Đang giao hàng" );
                break;
            case 2:
                tinhtrang.setText( "Đã giao hàng" );
                break;
            case 3:
                tinhtrang.setText( "Hủy đơn hàng" );
                break;

        }

//        Picasso.with(context).load(order.getLinkAnh()).into(hinh);

        return view;
    }
}
