package com.project.thesisguidance.model;

import com.google.firebase.Timestamp;

public class Dosen {
    private String nik;
    private String nama_dosen;
    private String password;
    private Timestamp tgl_lahir;

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama_dosen() {
        return nama_dosen;
    }

    public void setNama_dosen(String nama_dosen) {
        this.nama_dosen = nama_dosen;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getTgl_lahir() {
        return tgl_lahir;
    }

    public void setTgl_lahir(Timestamp tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }

    @Override
    public String toString() {
        return nama_dosen;
    }
}
