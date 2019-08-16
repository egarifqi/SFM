package com.example.sfmtesting;

import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.io.Serializable;

public class ViewHolder implements Serializable {
    CardView cardView;
    TextView product_odoo;
    TextView product_name;
    TextView product_price;
    TextView product_ws;
    TextView product_stock;
    TextView product_qty;
}
