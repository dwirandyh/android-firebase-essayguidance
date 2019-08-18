package com.project.thesisguidance.model;

public class Skripsi {
    private String id_skripsi;
    private String jenis_skripsi;
    private String judul;
    private String masalah;
    private String nama_pembimbing;
    private String nama_penguji;
    private String nik;
    private String npm;
    private String status;

    public String getId_skripsi() {
        return id_skripsi;
    }

    public void setId_skripsi(String id_skripsi) {
        this.id_skripsi = id_skripsi;
    }

    public String getJenis_skripsi() {
        return jenis_skripsi;
    }

    public void setJenis_skripsi(String jenis_skripsi) {
        this.jenis_skripsi = jenis_skripsi;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getMasalah() {
        return masalah;
    }

    public void setMasalah(String masalah) {
        this.masalah = masalah;
    }

    public String getNama_pembimbing() {
        return nama_pembimbing;
    }

    public void setNama_pembimbing(String nama_pembimbing) {
        this.nama_pembimbing = nama_pembimbing;
    }

    public String getNama_penguji() {
        return nama_penguji;
    }

    public void setNama_penguji(String nama_penguji) {
        this.nama_penguji = nama_penguji;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
