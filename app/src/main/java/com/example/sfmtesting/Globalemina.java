package com.example.sfmtesting;

import java.util.ArrayList;

public class Globalemina {
    public static ArrayList<com.example.sfmtesting.Spacecraft> produk = new ArrayList<com.example.sfmtesting.Spacecraft>();
    public static ArrayList<String> kode = new ArrayList<String>();
    public static ArrayList<String> nama = new ArrayList<String>();
    public static ArrayList<String> harga = new ArrayList<String>();
    public static ArrayList<String> stock = new ArrayList<String>();
    public static ArrayList<String> qty = new ArrayList<String>();
    public static int produkCount = 0;
    public static String brand = "brand:Emina";
    public static ArrayList<Integer> id_produk = new ArrayList<Integer>();
    public static ArrayList<String> kategori = new ArrayList<String>();
    public static ArrayList<String> sgtorder = new ArrayList<String>();
    public static int totalsku = 0;
    public static int totalitem = 0;
    public static int totalorder = 0;
    public static String notes = "";

    public static void clearProduct() {
        produk.clear();
        notes = "";
        kode.clear();
        nama.clear();
        harga.clear();
        stock.clear();
        qty.clear();
        sgtorder.clear();
        id_produk.clear();
        kategori.clear();
        produkCount = 0;
        totalsku = 0;
        totalitem = 0;
        totalorder = 0;
    }
}
