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

    public String getHinhanhdiadiem() {
        return hinhanhdiadiem;
    }

    public void setHinhanhdiadiem(String hinhanhdiadiem) {
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

//    public void getDanhSachDiaDiem(final IDiaDiem iDiaDiem){
//        final List<DiaDiemModel> diaDiemModelList = new ArrayList<>();
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                DataSnapshot dataSnapshotDiaDiem = dataSnapshot.child("diadiems");
//
//                for (DataSnapshot valueMaTinhThanh : dataSnapshotDiaDiem.getChildren()){
//                    for(DataSnapshot valueMaQuanAn : valueMaTinhThanh.getChildren())
//                    {
//                        DiaDiemModel diaDiemModel = valueMaQuanAn.getValue(DiaDiemModel.class);
//                        diaDiemModelList.add(diaDiemModel);
//                        iDiaDiem.getDanhSachDiaDiemMoDel(diaDiemModelList);
//
//                        DataSnapshot dataSnapshotHinhDiaDiem = dataSnapshot.child("hinhanhs").child(valueMaQuanAn.getKey());
//                        List<String> hinhanhlist = new ArrayList<>();
//                        for(DataSnapshot valueHinh : dataSnapshotDiaDiem.getChildren()){
//
//                        }
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        nodeRoot.addListenerForSingleValueEvent(valueEventListener);
//    }
//    public void getDanhSachTinhThanh(final IDiaDiem iDiaDiem){
//        final List<String> arrTinhThanh = new ArrayList<>();
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot valueTinh : dataSnapshot.getChildren())
//                        {
//                            String tinh = valueTinh.getValue(String.class);
//                            arrTinhThanh.add(tinh);
//                            iDiaDiem.getDanhSachTinhThanh(arrTinhThanh);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        nodeRoot.addValueEventListener(valueEventListener);
//    }

}
