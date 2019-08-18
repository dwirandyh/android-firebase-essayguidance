package com.project.thesisguidance.model;

import com.google.firebase.Timestamp;

public class Bimbingan {
    private String id_bimbingan;
    private String bab;
    private Timestamp tgl_kirim;
    private Timestamp tgl_selesai;
    private String keterangan_bab;
    private String url_dokumen;
    private String status_bab;

    // additional property
    private String nik;
    private String npm;

    public String getBab() {
        return bab;
    }

    public void setBab(String bab) {
        this.bab = bab;
    }

    public Timestamp getTgl_kirim() {
        return tgl_kirim;
    }

    public void setTgl_kirim(Timestamp tgl_kirim) {
        this.tgl_kirim = tgl_kirim;
    }

    public String getKeterangan_bab() {
        return keterangan_bab;
    }

    public void setKeterangan_bab(String keterangan_bab) {
        this.keterangan_bab = keterangan_bab;
    }

    public String getUrl_dokumen() {
        return url_dokumen;
    }

    public void setUrl_dokumen(String url_dokumen) {
        this.url_dokumen = url_dokumen;
    }

    public String getStatus_bab() {
        return status_bab;
    }

    public void setStatus_bab(String status_bab) {
        this.status_bab = status_bab;
    }


    public String getId_bimbingan() {
        return id_bimbingan;
    }

    public void setId_bimbingan(String id_bimbingan) {
        this.id_bimbingan = id_bimbingan;
    }

    public Timestamp getTgl_selesai() {
        return tgl_selesai;
    }

    public void setTgl_selesai(Timestamp tgl_selesai) {
        this.tgl_selesai = tgl_selesai;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNpm() {
        return npm;
    }

    public void setNpm(String npm) {
        this.npm = npm;
    }
}