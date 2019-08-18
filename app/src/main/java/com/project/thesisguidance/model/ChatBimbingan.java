package com.project.thesisguidance.model;

import com.google.firebase.Timestamp;

public class ChatBimbingan {
    private String isi_chat = "";
    private String nama = "";
    private String nik = "";
    private String npm = "";
    private String id_bimbingan = "";
    private Timestamp tanggal;

    public String getIsi_chat() {
        return isi_chat;
    }

    public void setIsi_chat(String isi_chat) {
        this.isi_chat = isi_chat;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public String getId_bimbingan() {
        return id_bimbingan;
    }

    public void setId_bimbingan(String id_bimbingan) {
        this.id_bimbingan = id_bimbingan;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }
}
