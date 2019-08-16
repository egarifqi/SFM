package com.example.sfmtesting;

public class OrderedProduct {
    public int id_produk;
    public String kode_odoo;
    public String nama_produk;
    public String brand_produk;
    public String harga_produk;
    public int stock_produk;
    public int sgtorder_produk;
    public int finalorder_produk;
    public int do_id;
    public String nama_toko;
    public String kategori_produk;
    public String partner_id;
    public String reference;

    public OrderedProduct(int id_produk, String kode_odoo, String nama_produk, String harga_produk,
                          int stock_produk, int sgtorder_produk, int finalorder_produk, String kategori_produk,
                          String brand_produk, String partner_id, String reference, int do_id, String nama_toko){
        this.id_produk = id_produk;
        this.kode_odoo = kode_odoo;
        this.nama_produk = nama_produk;
        this.harga_produk = harga_produk;
        this.stock_produk = stock_produk;
        this.sgtorder_produk = sgtorder_produk;
        this.finalorder_produk = finalorder_produk;
        this.kategori_produk = kategori_produk;
        this.brand_produk = brand_produk;
        this.partner_id = partner_id;
        this.reference = reference;
        this.do_id = do_id;
        this.nama_toko = nama_toko;
    }

    public OrderedProduct() {}

    public int getId_produk(){return id_produk;}
    public void setId_produk(int id_produk){this.id_produk = id_produk;}

    public int getDo_id(){return do_id;}
    public void setDo_id(int do_idk){this.do_id = do_idk;}

    public int getStock_produk(){return stock_produk;}
    public void setStock_produk(int stock_produk){this.stock_produk = stock_produk;}

    public int getSgtorder_produk(){return sgtorder_produk;}
    public void setSgtorder_produk(int sgtorder_produk){this.sgtorder_produk = sgtorder_produk;}

    public int getFinalorder_produk(){return finalorder_produk;}
    public void setFinalorder_produk(int finalorder_produk){this.finalorder_produk = finalorder_produk;}

    public String getKode_odoo(){return kode_odoo;}
    public void setKode_odoo(String kode_odoo){this.kode_odoo = kode_odoo;}

    public String getNama_produk(){return nama_produk;}
    public void setNama_produk(String nama_produk){this.nama_produk = nama_produk;}

    public String getHarga_produk(){return harga_produk;}
    public void setHarga_produk(String harga_produk){this.harga_produk = harga_produk;}

    public String getBrand_produk(){return brand_produk;}
    public void setBrand_produk(String brand_produk){this.brand_produk = brand_produk;}

    public String getKategori_produk(){return kategori_produk;}
    public void setKategori_produk(String kategori_produk){this.kategori_produk = kategori_produk;}

    public String getPartner_id(){return partner_id;}
    public void setPartner_id(String partner_id){this.partner_id = partner_id;}

    public String getReference(){return reference;}
    public void setReference(String reference){this.reference = reference;}

    public String getNama_toko(){return nama_toko;}
    public void setNama_toko(String nama_toko){this.nama_toko = nama_toko;}
}
