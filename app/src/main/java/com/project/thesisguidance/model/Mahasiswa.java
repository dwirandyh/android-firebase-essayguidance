package com.project.thesisguidance.model;

import com.google.firebase.Timestamp;

public class Mahasiswa {
    private String name;
    private String npm;
    private String password;
    private String alamat;
    private int semester;
    private String tempat_lahir;
    private Timestamp tgl_lahir;

    public String getNama() {
        return name;
    }

    public void setNama(String name) {
        this.name = name;
    }

    public String getNpm() {
        return npm;
    }

    public void setNpm(String npm) {
        this.npm = npm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getTempat_lahir() {
        return tempat_lahir;
    }

    public void setTempat_lahir(String tempat_lahir) {
        this.tempat_lahir = tempat_lahir;
    }

    public Timestamp getTgl_lahir() {
        return tgl_lahir;
    }

    public void setTgl_lahir(Timestamp tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }
}
