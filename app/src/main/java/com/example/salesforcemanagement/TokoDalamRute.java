package com.example.salesforcemanagement;

public class TokoDalamRute {

    private int id;
    private String kode;
    private String nama;
    private String salesid;
    private String partnerid;
    private String frekuensi;
    private String status;
    private boolean ba;

    public TokoDalamRute(int id, String kode, String nama, String salesid, String partnerid, String frekuensi, String status, boolean ba) {
        this.id = id;
        this.kode = kode;
        this.nama = nama;
        this.salesid = salesid;
        this.partnerid = partnerid;
        this.frekuensi = frekuensi;
        this.status = status;
        this.ba = ba;
    }

    public TokoDalamRute(String kode, String nama, String salesid, String partnerid, String frekuensi, String status, boolean ba) {
        this.kode = kode;
        this.nama = nama;
        this.salesid = salesid;
        this.partnerid = partnerid;
        this.frekuensi = frekuensi;
        this.status = status;
        this.ba = ba;
    }

    public TokoDalamRute() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getFrekuensi() {
        return frekuensi;
    }

    public void setFrekuensi(String frekuensi) {
        this.frekuensi = frekuensi;
    }

    public String getPartnerId() {
        return partnerid;
    }

    public void setPartnerId(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getBa() {
        return ba;
    }

    public void setBa(boolean ba) {
        this.ba = ba;
    }

    /*
    TOSTRING
    */
    @Override
    public String toString() {
        return (kode + " - " + nama);
    }
}

