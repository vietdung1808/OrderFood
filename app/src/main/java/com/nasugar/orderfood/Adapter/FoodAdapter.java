package com.nasugar.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nasugar.orderfood.R;
import com.nasugar.orderfood.interfaces.OnFoodListener;
import com.nasugar.orderfood.model.MonAn;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodHolder> {

    private Context mContext;
    private List<MonAn> monAnList;
    private OnFoodListener mListener;


    private DecimalFormat decimalFormat = new DecimalFormat("###,### VNĐ");

    public FoodAdapter(Context mContext, List<MonAn> monAnList) {
        this.mContext = mContext;
        this.monAnList = monAnList;
    }

    @NonNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.food_row, parent, false);
        return new FoodHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodHolder holder, int position) {
        MonAn monAn = monAnList.get(position);
        holder.tvFoodName.setText(monAn.getTenMon());
        holder.tvFoodPrice.setText(decimalFormat.format(monAn.getGiaMon()));
        Picasso.with(mContext).load(monAn.getLinkAnh()).into(holder.imgFood);
    }

    @Override
    public int getItemCount() {
        return monAnList.size();
    }

    class FoodHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName;
        TextView tvFoodPrice;
        ImageButton btnAddToCart;
        public FoodHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imageView_food);
            tvFoodName = itemView.findViewById(R.id.textView_food_foodname);
            tvFoodPrice = itemView.findViewById(R.id.textView_food_price);
            btnAddToCart = itemView.findViewById(R.id.imageButton_add_to_cart);

            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAddToCartClick(monAnList.get(getAdapterPosition()));
                }
            });
        }
    }

    public void setOnItemClickListener(OnFoodListener listener) {
        this.mListener = listener;
    }

}