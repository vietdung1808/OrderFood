package com.nasugar.orderfood.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.adapter.FoodCatalogueAdapter;
import com.nasugar.orderfood.interfaces.OnFoodCatalogueListener;
import com.nasugar.orderfood.model.Banner;
import com.nasugar.orderfood.model.FoodCatalogue;
import com.nasugar.orderfood.view.FoodActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView rcvFoodCatalogue;
    List<FoodCatalogue> mFoodCatalogueList;
    FoodCatalogueAdapter mFoodCatalogueAdapter;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    //Slider
    HashMap<String, String> imageList;
    SliderLayout sliderBanner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rcvFoodCatalogue = view.findViewById(R.id.recycler_food_catalogue);
        sliderBanner = view.findViewById(R.id.slider);

        mFoodCatalogueList = new ArrayList<>();
        mFoodCatalogueAdapter = new FoodCatalogueAdapter(getActivity(), mFoodCatalogueList);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        rcvFoodCatalogue.setLayoutManager(layoutManager);
        rcvFoodCatalogue.setItemAnimator(new DefaultItemAnimator());
        rcvFoodCatalogue.setAdapter(mFoodCatalogueAdapter);
        rcvFoodCatalogue.setNestedScrollingEnabled(false);

        getItemData();
        setupSlider();
        setOnListener();

        return view;
    }

    private void setOnListener() {

        mFoodCatalogueAdapter.setOnItemClickListener(new OnFoodCatalogueListener() {
            @Override
            public void onFoodCatalogueItemClick(FoodCatalogue foodCatalogue) {
                Intent intent = new Intent(getContext(), FoodActivity.class);
                intent.putExtra("FoodCatalogue", foodCatalogue);
                startActivity(intent);
            }
        });
    }

    private void setupSlider() {
        imageList = new HashMap<>();
        final DatabaseReference database = myRef.child("Banner");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Banner banner = item.getValue(Banner.class);
                    imageList.put(banner.getId() + "_" + banner.getIdQuan(), banner.getImage());
                }
                for (String key : imageList.keySet()) {
                    String[] keySplit = key.split("_");
                    String nameOfFood = keySplit[0];
                    String idOfRestaurent = keySplit[1];

                    //Creative Slider
                    final TextSliderView textSliderView = new TextSliderView(getActivity());
                    textSliderView
                            .description(nameOfFood)
                            .image(imageList.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
//                                    Intent intent = new Intent(KhachHangActivity.this, FoodDetailActivity.class);
////                                    intent.putExtras(textSliderView.getBundle());
////                                    startActivity(intent);
                                }

                            });

                    //add extra bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId", nameOfFood);
                    textSliderView.getBundle().putString("RestaurentID", idOfRestaurent);

                    sliderBanner.addSlider(textSliderView);

                    //Remove event after finish
                    database.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getItemData() {
        DatabaseReference database = myRef.child("FoodCatalogue");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    FoodCatalogue foodCatalogue = item.getValue(FoodCatalogue.class);
                    foodCatalogue.setId(item.getKey());
                    mFoodCatalogueList.add(foodCatalogue);

                }
                mFoodCatalogueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
