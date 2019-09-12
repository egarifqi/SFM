package com.example.salesforcemanagement;

public class StoreVisitList {
    public int id;
    public String sales_id;
    public String user_id;
    public String partner_ref;
    public String partner_id;
    public boolean inroute;
    public String latitude;
    public String longitude;
    public String nama_toko;
    public String reference;
    public String start_time;
    public String finish_time;
    public String reason;
    public String state;

    public StoreVisitList(String sales_id, String user_id, String partner_ref, String partner_id,
                          boolean inroute, String latitude, String longitude, String nama_toko,
                          String reference, String start_time, String finish_time, String reason,
                          String state){
        this.sales_id = sales_id;
        this.user_id = user_id;
        this.partner_ref = partner_ref;
        this.partner_id = partner_id;
        this.inroute = inroute;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nama_toko = nama_toko;
        this.start_time = start_time;
        this.finish_time = finish_time;
        this.reference = reference;
        this.reason = reason;
        this.state = state;
    }

    public StoreVisitList() {}

    public int getId(){return id;}
    public void setId(int do_idk){this.id = do_idk;}

    public String getSales_id(){return sales_id;}
    public void setSales_id(String do_idk){this.sales_id = do_idk;}

    public String getUser_id(){return user_id;}
    public void setUser_id(String brand){this.user_id = brand;}

    public String getPartner_id(){return partner_id;}
    public void setPartner_id(String partner_id){this.partner_id = partner_id;}

    public String getReference(){return reference;}
    public void setReference(String reference){this.reference = reference;}

    public String getNama_toko(){return nama_toko;}
    public void setNama_toko(String nama_toko){this.nama_toko = nama_toko;}

    public String getPartner_ref(){return partner_ref;}
    public void setPartner_ref(String date){this.partner_ref = date;}

    public boolean getInroute(){return inroute;}
    public void setInroute(boolean complete){this.inroute = complete;}

    public String getLatitude(){return latitude;}
    public void setLatitude(String date){this.latitude = date;}

    public String getLongitude(){return longitude;}
    public void setLongitude(String date){this.longitude = date;}

    public String getStart_time(){return start_time;}
    public void setStart_time(String date){this.start_time = date;}

    public String getFinish_time(){return finish_time;}
    public void setFinish_time(String date){this.finish_time = date;}

    public String getReason(){return reason;}
    public void setReason(String date){this.reason = date;}

    public String getState(){return state;}
    public void setState(String state){this.state = state;}
}
