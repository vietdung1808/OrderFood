package com.nasugar.orderfood.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.nasugar.orderfood.R;
import com.nasugar.orderfood.interfaces.OnCartListener;
import com.nasugar.orderfood.model.Cart;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CardHolder> {

    private Context mContext;
    private List<Cart> mCartList;
    private OnCartListener mListener;

    private DecimalFormat decimalFormat = new DecimalFormat("###,### VNĐ");

    public CartAdapter(Context mContext, List<Cart> mCartList) {
        this.mContext = mContext;
        this.mCartList = mCartList;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cart_row, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        Cart cart = mCartList.get(position);
        holder.tvFoodName.setText(cart.getTenMon());
        holder.tvFoodPrice.setText(decimalFormat.format(cart.getGiaMon()));
        holder.tvQuantity.setText(String.valueOf(cart.getSoluong()));
        Picasso.with(mContext).load(cart.getLinkAnh()).into(holder.imgFood);
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    class CardHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName, tvFoodPrice, tvQuantity;
        ImageButton btnPlus, btnMinus;

        public CardHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imageView_cart_food);
            tvFoodName = itemView.findViewById(R.id.textView_cart_food_foodname);
            tvFoodPrice = itemView.findViewById(R.id.textView_cart_food_price);
            tvQuantity = itemView.findViewById(R.id.textView_cart_quantity);
            btnMinus = itemView.findViewById(R.id.imageButton_cart_tru);
            btnPlus = itemView.findViewById(R.id.imageButton_cart_cong);

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCartButtonPlusClick(mCartList.get(getAdapterPosition()));
                }
            });

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cart cart = mCartList.get(getAdapterPosition());
                    if (cart.getSoluong() > 1) {
                        mListener.onCartButtonMinusClick(cart);
                    } else {
                        AlertDialog builder = new AlertDialog.Builder(mContext)
                                .setMessage("Bạn chắc chắn muốn bỏ sản phẩm này?")
                                .setCancelable(false)
                                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mListener.onCartButtonMinusClick(cart);
                                    }
                                })
                                .show();
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnCartListener listener) {
        this.mListener = listener;
    }
}
