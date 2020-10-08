package com.nasugar.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nasugar.orderfood.R;
import com.nasugar.orderfood.interfaces.OnFoodCatalogueListener;
import com.nasugar.orderfood.model.FoodCatalogue;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodCatalogueAdapter extends RecyclerView.Adapter<FoodCatalogueAdapter.FoodCatalogueHolder> {

    private Context mContext;
    private List<FoodCatalogue> mFoodCatalogues;

    private OnFoodCatalogueListener listener;

    public FoodCatalogueAdapter(Context context, List<FoodCatalogue> foodCatalogues) {
        this.mContext = context;
        this.mFoodCatalogues = foodCatalogues;
    }

    @NonNull
    @Override
    public FoodCatalogueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.food_catalogue_row, parent, false);
        return new FoodCatalogueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodCatalogueHolder holder, int position) {
        FoodCatalogue foodCatalogue = mFoodCatalogues.get(position);
        holder.tvFoodCatalogue.setText(foodCatalogue.getName());
        Picasso.with(mContext).load(foodCatalogue.getLinkimage()).into(holder.imgFoodCatalogue);
    }

    @Override
    public int getItemCount() {
        return mFoodCatalogues.size();
    }

    class FoodCatalogueHolder extends RecyclerView.ViewHolder {
        ImageView imgFoodCatalogue;
        TextView tvFoodCatalogue;

        public FoodCatalogueHolder(@NonNull View itemView) {
            super(itemView);

            imgFoodCatalogue = itemView.findViewById(R.id.imageview_foodcatalogue);
            tvFoodCatalogue = itemView.findViewById(R.id.texview_foodcatalogue);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFoodCatalogueItemClick(mFoodCatalogues.get(getAdapterPosition()));
                }
            });
        }
    }

    public void setOnItemClickListener(OnFoodCatalogueListener listener) {
        this.listener = listener;
    }
}