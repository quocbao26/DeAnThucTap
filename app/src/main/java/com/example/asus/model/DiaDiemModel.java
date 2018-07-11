package com.example.asus.model;

import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiaDiemModel implements Serializable {
    private String madiadiem,tendiadiem,diachi,gioithieu,latitude,longitude;

    private String hinhanhdiadiem;

    DatabaseReference nodeRoot;


    public DiaDiemModel() {
        nodeRoot = FirebaseDatabase.getInstance().getReference();
    }

    public DiaDiemModel(String madiadiem, String tendiadiem, String diachi, String gioithieu, String latitude, String longitude, String hinhanhdiadiem) {
        this.madiadiem = madiadiem;
        this.tendiadiem = tendiadiem;
        this.diachi = diachi;
        this.gioithieu = gioithieu;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hinhanhdiadiem = hinhanhdiadiem;
    }

    public String getMadiadiem() {
        return madiadiem;
    }

    public void setMadiadiem(String madiadiem) {
        this.madiadiem = madiadiem;
    }

    public String getTendiadiem() {
        return tendiadiem;
    }

    public void setTendiadiem(String tendiadiem) {
        this.tendiadiem = tendiadiem;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getGioithieu() {
        return gioithieu;
    }

    public void setGioithieu(String gioithieu) {
        this.gioithieu = gioithieu;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getHinhanhdiadiem() {
        return hinhanhdiadiem;
    }

    public void setHinhanhdiadiem(String hinhanhdiadiem) {
        this.hinhanhdiadiem = hinhanhdiadiem;
    }
}
