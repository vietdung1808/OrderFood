package com.nasugar.orderfood.interfaces;

import com.nasugar.orderfood.model.Cart;

public interface OnCartListener {
    public void onCartButtonPlusClick(Cart cart);

    public void onCartButtonMinusClick(Cart cart);
}
