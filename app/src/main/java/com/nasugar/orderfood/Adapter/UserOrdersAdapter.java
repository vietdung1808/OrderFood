package com.nasugar.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nasugar.orderfood.R;
import com.nasugar.orderfood.interfaces.OnUserOrderListener;
import com.nasugar.orderfood.model.Orders;

import java.util.List;

public class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.UserOrdersHolder> {
    private Context mContext;
    private List<Orders> mOrdersList;
    private OnUserOrderListener mListener;

    public UserOrdersAdapter(Context mContext, List<Orders> mOrdersList) {
        this.mContext = mContext;
        this.mOrdersList = mOrdersList;
    }

    @NonNull
    @Override
    public UserOrdersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_user_orders, parent, false);
        return new UserOrdersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrdersHolder holder, int position) {
        Orders orders = mOrdersList.get(position);
        holder.tvOrderId.setText(orders.getOrderId());
        holder.tvAddress.setText(orders.getAddress());
        holder.tvOrderDate.setText(orders.getOrderDate());
        holder.tvAmount.setText(orders.getTotalAmount());
        switch (orders.getStatus()) {
            case 0:
                holder.tvStatus.setText("Chờ xác nhận");
                break;
            case 1:
                holder.tvStatus.setText("Đang giao");
                break;
            case 2:
                holder.tvStatus.setText("Đã giao");
                break;
            case 3:
                holder.tvStatus.setText("Đã hủy");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mOrdersList.size();
    }

    class UserOrdersHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvAddress, tvOrderDate, tvAmount;
        public UserOrdersHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.textview_user_orders_orderid);
            tvOrderDate = itemView.findViewById(R.id.textview_user_orders_orderdate);
            tvStatus = itemView.findViewById(R.id.textview_user_orders_status);
            tvAddress = itemView.findViewById(R.id.textview_user_orders_address);
            tvAmount = itemView.findViewById(R.id.textview_user_orders_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(tvOrderId.getText().toString());
                }
            });
        }
    }

    public void setOnItemClick(OnUserOrderListener listener) {
        this.mListener = listener;
    }
}
