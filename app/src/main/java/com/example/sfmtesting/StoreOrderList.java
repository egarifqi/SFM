package com.example.sfmtesting;

public class StoreOrderList {
    public String brand;
    public int do_id;
    public String nama_toko;
    public String partner_id;
    public String date;
    public String reference;

    public StoreOrderList(int do_id, String partner_id, String nama_toko, String reference, String brand){
        this.do_id = do_id;
        this.partner_id = partner_id;
        this.nama_toko = nama_toko;
        this.reference = reference;
        this.date = date;
        this.brand = brand;
    }

    public StoreOrderList() {}

    public int getDo_id(){return do_id;}
    public void setDo_id(int do_idk){this.do_id = do_idk;}

    public String getBrand_produk(){return brand;}
    public void setBrand_produk(String brand){this.brand = brand;}

    public String getPartner_id(){return partner_id;}
    public void setPartner_id(String partner_id){this.partner_id = partner_id;}

    public String getReference(){return reference;}
    public void setReference(String reference){this.reference = reference;}

    public String getNama_toko(){return nama_toko;}
    public void setNama_toko(String nama_toko){this.nama_toko = nama_toko;}

    public String getDate(){return date;}
    public void setDate(String date){this.date = date;}
}
