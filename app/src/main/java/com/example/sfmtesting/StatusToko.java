package com.example.sfmtesting;

import java.util.ArrayList;

public class StatusToko {
    public static ArrayList<String> kodetoko = new ArrayList<String>();
    public static ArrayList<String> namatoko = new ArrayList<String>();
    public static ArrayList<String> tipetoko = new ArrayList<String>();
    public static ArrayList<String> statuskunjungan = new ArrayList<String>();
    public static ArrayList<String> waktumulai = new ArrayList<String>();
    public static ArrayList<String> waktuselesai = new ArrayList<String>();
    public static ArrayList<Integer> totalamount = new ArrayList<Integer>();
    public static ArrayList<Integer> totalqty = new ArrayList<Integer>();
    public static ArrayList<Integer> totalsku = new ArrayList<Integer>();
    public static ArrayList<String> producttype = new ArrayList<String>();
    public static int qtywardah = 0;
    public static int skuwardah = 0;
    public static int qtymake = 0;
    public static int skumake = 0;
    public static int qtyemina = 0;
    public static int skuemina = 0;
    public static int qtyputri = 0;
    public static int skuputri = 0;
    public static int itemqty = 0;
    public static int sku = 0;
    public static int do_id = 0;
    public static boolean rute = true;

    public static void clearToko() {
        kodetoko.clear();
        namatoko.clear();
        tipetoko.clear();
        statuskunjungan.clear();
        waktuselesai.clear();
        waktumulai.clear();
        totalamount.clear();
        totalqty.clear();
        totalsku.clear();
        rute = true;
    }

    public static void clearTotalToko() {
        itemqty = 0;
        sku = 0;
    }

    public static int getCount() {
        return StatusToko.kodetoko.size();
    }

}
