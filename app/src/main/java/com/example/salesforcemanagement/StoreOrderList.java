package com.example.salesforcemanagement;

public class StoreOrderList {
    public String brand;
    public int do_id;
    public String visit_ref;
    public String nama_toko;
    public String partner_id;
    public String partner_ref;
    public String sales_id;
    public String user_id;
    public String reference;
    public int total_amount;
    public int total_sku;
    public int total_item;
    public String note;
    public boolean complete;

    public StoreOrderList(int do_id, String partner_id, String nama_toko, String reference,
                          String brand, String visit_ref){
        this.do_id = do_id;
        this.partner_id = partner_id;
        this.nama_toko = nama_toko;
        this.reference = reference;
        this.brand = brand;
        this.visit_ref = visit_ref;
    }

    public StoreOrderList(String partner_id, String partner_ref, String nama_toko, String reference,
                          String brand, String visit_ref, String sales_id, String user_id,
                          int total_amount, int total_item, int total_sku, String note){
        this.partner_id = partner_id;
        this.partner_ref = partner_ref;
        this.nama_toko = nama_toko;
        this.reference = reference;
        this.brand = brand;
        this.visit_ref = visit_ref;
        this.sales_id = sales_id;
        this.user_id = user_id;
        this.total_item = total_item;
        this.total_sku = total_sku;
        this.total_amount = total_amount;
        this.note = note;
    }

    public StoreOrderList() {}

    public int getDo_id(){return do_id;}
    public void setDo_id(int do_idk){this.do_id = do_idk;}

    public String getVisit_ref() {return visit_ref;}
    public void setVisit_ref(String visit_ref){this.visit_ref = visit_ref;}

    public String getBrand_produk(){return brand;}
    public void setBrand_produk(String brand){this.brand = brand;}

    public String getPartner_id(){return partner_id;}
    public void setPartner_id(String partner_id){this.partner_id = partner_id;}

    public String getPartner_ref(){return partner_ref;}
    public void setPartner_ref(String partner_id){this.partner_ref = partner_id;}

    public String getReference(){return reference;}
    public void setReference(String reference){this.reference = reference;}

    public String getNama_toko(){return nama_toko;}
    public void setNama_toko(String nama_toko){this.nama_toko = nama_toko;}

    public boolean getComplete(){return complete;}
    public void setComplete(boolean complete){this.complete = complete;}

    public String getSales_id(){return sales_id;}
    public void setSales_id(String partner_id){this.sales_id = partner_id;}

    public String getUser_id(){return user_id;}
    public void setUser_id(String partner_id){this.user_id = partner_id;}

    public int getTotal_amount(){return total_amount;}
    public void setTotal_amount(int total_amount){this.total_amount = total_amount;}

    public int getTotal_sku(){return total_sku;}
    public void setTotal_sku(int total_sku){this.total_sku = total_sku;}

    public int getTotal_item(){return total_item;}
    public void setTotal_item(int total_item){this.total_item = total_item;}

    public String getNote(){return note;}
    public void setNote(String brand){this.note = brand;}
}
