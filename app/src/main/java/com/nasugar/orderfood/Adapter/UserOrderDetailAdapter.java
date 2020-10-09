package com.nasugar.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nasugar.orderfood.R;
import com.nasugar.orderfood.model.Cart;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class UserOrderDetailAdapter extends RecyclerView.Adapter<UserOrderDetailAdapter.UserOrderDetailHolder> {
    private Context mContext;
    private List<Cart> mFoodList;

    private DecimalFormat decimalFormat = new DecimalFormat("₫ ###,###");

    public UserOrderDetailAdapter(Context context, List<Cart> mFoodList) {
        this.mContext = context;
        this.mFoodList = mFoodList;
    }

    @NonNull
    @Override
    public UserOrderDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_user_order_detail,parent, false);
        return new UserOrderDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrderDetailHolder holder, int position) {
        Cart food = mFoodList.get(position);
        holder.tvFoodName.setText(food.getTenMon());
        holder.tvPrice.setText(decimalFormat.format(food.getGiaMon()));
        holder.tvQuantity.setText(String.format("x %d", food.getSoluong()));
        holder.tvAmount.setText(decimalFormat.format(food.getTongTien()));
        Picasso.with(mContext).load(food.getLinkAnh()).into(holder.imgFood);
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    class UserOrderDetailHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName, tvPrice, tvQuantity, tvAmount;
        public UserOrderDetailHolder(@NonNull View itemView) {
            super(itemView);

            imgFood = itemView.findViewById(R.id.imageView_user_order_detail_food);
            tvFoodName = itemView.findViewById(R.id.textView_user_order_detail_foodname);
            tvPrice = itemView.findViewById(R.id.textView_user_order_detail_price);
            tvQuantity = itemView.findViewById(R.id.textView_user_order_detail_quantity);
            tvAmount = itemView.findViewById(R.id.textView_user_order_detail_amount);
        }
    }

}
