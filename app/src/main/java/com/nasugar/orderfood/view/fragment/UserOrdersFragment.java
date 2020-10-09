package com.nasugar.orderfood.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nasugar.orderfood.R;
import com.nasugar.orderfood.adapter.UserOrdersAdapter;
import com.nasugar.orderfood.model.Orders;

import java.util.ArrayList;
import java.util.List;


public class UserOrdersFragment extends Fragment {

    private RecyclerView rcvOrdersList;
    private UserOrdersAdapter mUserOrdersAdapter;
    private List<Orders> mOrdersList;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_orders, container, false);

        mapViews(view);
        init();
        loadData();
        return view;
    }

    private void loadData() {
        DatabaseReference database = myRef.child("Orders/" + user.getUid());
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mOrdersList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Orders orders = item.getValue(Orders.class);
                    mOrdersList.add(orders);
                }
                mUserOrdersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mOrdersList = new ArrayList<>();
        mUserOrdersAdapter = new UserOrdersAdapter(getActivity(), mOrdersList);

        rcvOrdersList.setLayoutManager(layoutManager);
        rcvOrdersList.setItemAnimator(new DefaultItemAnimator());
        rcvOrdersList.setAdapter(mUserOrdersAdapter);
    }

    private void mapViews(View view) {
        rcvOrdersList = view.findViewById(R.id.recycler_view_user_orders);
    }
}