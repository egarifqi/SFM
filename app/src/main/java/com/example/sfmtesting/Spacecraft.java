package com.example.sfmtesting;

import android.os.Parcel;
import android.os.Parcelable;


public class Spacecraft implements Parcelable {
    public static final Creator<Spacecraft> CREATOR = new Creator<Spacecraft>() {
        @Override
        public Spacecraft createFromParcel(Parcel source) {
            return new Spacecraft(source);
        }

        @Override
        public Spacecraft[] newArray(int size) {
            return new Spacecraft[size];
        }
    };
    String kodeodoo;
    String namaproduk;
    String price;
    String stock;
    String qty;
    String koli;
    String category;
    String producttype;
    String barcode;
    int weekly_sales;
    String partner_id;
    String brand;
    String fuzzyMatchStatus;
    /*
    INSTANCE FIELDS
    */
    private int id;

    public Spacecraft() { }

    public Spacecraft(int id, String kodeodoo, String namaproduk, String price, String category,
                      String producttype, String barcode, int weekly_sales, String partner_id,
                      String brand, String stock, String qty, String koli) {

        this.id = id;
        this.kodeodoo = kodeodoo;
        this.namaproduk = namaproduk;
        this.price = price;
        this.category = category;
        this.producttype = producttype;
        this.barcode = barcode;
        this.stock = stock;
        this.qty = qty;
        this.weekly_sales = weekly_sales;
        this.partner_id = partner_id;
        this.brand = brand;
        this.koli = koli;
    }

    protected Spacecraft(Parcel in) {
        id = in.readInt();
        kodeodoo = in.readString();
        namaproduk = in.readString();
        price = in.readString();
        stock = in.readString();
        qty = in.readString();
        category = in.readString();
        producttype = in.readString();
        barcode = in.readString();
        weekly_sales = in.readInt();
        partner_id = in.readString();
        brand = in.readString();
        koli = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(kodeodoo);
        dest.writeString(namaproduk);
        dest.writeString(price);
        dest.writeString(stock);
        dest.writeString(qty);
        dest.writeString(category);
        dest.writeString(producttype);
        dest.writeString(barcode);
        dest.writeInt(weekly_sales);
        dest.writeString(partner_id);
        dest.writeString(brand);
        dest.writeString(koli);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    /*
            GETTERS AND SETTERS
            */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKodeodoo() {
        return kodeodoo;
    }

    public void setKodeodoo(String kodeodoo) {
        this.kodeodoo = kodeodoo;
    }

    public String getNamaproduk() {
        return namaproduk;
    }

    public void setNamaproduk(String namaproduk) {
        this.namaproduk = namaproduk;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public int getWeeklySales() { return weekly_sales; }

    public void setWeeklySales(int weekly_sales) {
        this.weekly_sales = weekly_sales;
    }

    public String getPartner_id() { return partner_id; }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
    }

    public String getBrand() { return brand; }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getKoli() {
        return koli;
    }

    public void setKoli(String koli) { this.koli = koli; }

    public String getFuzzyMatchStatus() {return fuzzyMatchStatus;}

    public void setFuzzyMatchStatus(String fuzzyStatus) {this.fuzzyMatchStatus = fuzzyStatus;}

    public void clearAll() {
        this.id = 0;
        this.kodeodoo = "";
        this.namaproduk = "";
        this.price = "0";
        this.category = "0";
        this.producttype = "";
        this.brand = "";
        this.weekly_sales = 0;
        this.partner_id = "";
        this.barcode = "";
        this.stock = "0";
        this.qty = "0";
        this.koli = "0";
        this.fuzzyMatchStatus = "fuzzynotmatched";
    }

        /*
        TOSTRING
        */
//        @Override
//        public String toString() {
//            return kodeodoo;
//        }
}


