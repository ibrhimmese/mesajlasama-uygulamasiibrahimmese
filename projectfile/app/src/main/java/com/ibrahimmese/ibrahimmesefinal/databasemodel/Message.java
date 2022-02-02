package com.ibrahimmese.ibrahimmesefinal.databasemodel;

public class Message {
    private String mesaj;
    private String kimden;
    private String kime;
    private String tarihsaat;

    public Message() {
    }

    public Message(String mesaj, String kimden, String kime, String tarihsaat) {
        this.mesaj = mesaj;
        this.kimden = kimden;
        this.kime = kime;
        this.tarihsaat = tarihsaat;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getKimden() {
        return kimden;
    }

    public void setKimden(String kimden) {
        this.kimden = kimden;
    }

    public String getKime() {
        return kime;
    }

    public void setKime(String kime) {
        this.kime = kime;
    }

    public String getTarihsaat() {
        return tarihsaat;
    }

    public void setTarihsaat(String tarihsaat) {
        this.tarihsaat = tarihsaat;
    }
}