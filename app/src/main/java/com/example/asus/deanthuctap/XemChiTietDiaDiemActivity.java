package com.example.asus.deanthuctap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.model.DiaDiemModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class XemChiTietDiaDiemActivity extends FragmentActivity {

    private static final String TAG = "XemChiTietDiaDiemAc";


    TextView txtNameChiTiet,txtAddressChiTiet,txtIntroduceChiTiet;
    ImageView imgHinh;
    ImageButton btnGui;
    EditText edtCmt;

    DatabaseReference nodeRoot;

    ListView lvComment;
    ArrayList<String> arrayComment;
    ArrayAdapter adapter;

    String madd="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_chi_tiet_dia_diem);
        Setcontrols();
        addEvents();

    }

    private void addEvents() {

        Intent intent = getIntent();
        final DiaDiemModel diaDiemModel = (DiaDiemModel) intent.getSerializableExtra("chitiet_diadiem");
        txtNameChiTiet.setText(diaDiemModel.getTendiadiem());
        txtAddressChiTiet.setText(diaDiemModel.getDiachi());
        txtIntroduceChiTiet.setText(diaDiemModel.getGioithieu());
        Picasso.get().load(diaDiemModel.getHinhanhdiadiem()).resize(600,500).into(imgHinh);

        madd = diaDiemModel.getMadiadiem();

        loadComment();

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtCmt.getText().toString().trim().isEmpty()){
                    Toast.makeText(XemChiTietDiaDiemActivity.this, "Không bỏ trống", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG,"Đã thêm bình luận mới");
                    nodeRoot.child("binhluans").child(madd).push().setValue(edtCmt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(XemChiTietDiaDiemActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                            edtCmt.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(XemChiTietDiaDiemActivity.this, "Lỗi bình luận", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        txtAddressChiTiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(XemChiTietDiaDiemActivity.this,MapActivity.class);
                intent.putExtra("latitude",diaDiemModel.getLatitude());
                intent.putExtra("longitude",diaDiemModel.getLongitude());
                Log.d(TAG,diaDiemModel.getLatitude() + " - " + diaDiemModel.getLongitude());
                startActivity(intent);
            }
        });

    }

    private void loadComment(){
        Log.d(TAG,"load comment");
        nodeRoot.child("binhluans").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot valueKeyDiaDiem : dataSnapshot.getChildren())
                {
                    String value = valueKeyDiaDiem.getKey();
                    if(madd.equals(value)){

                        nodeRoot.child("binhluans").child(madd).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot != null){
                                    arrayComment.clear();
                                    for (DataSnapshot valueComt : dataSnapshot.getChildren()){
                                        String valueCmt = valueComt.getValue(String.class);
                                        arrayComment.add(valueCmt);
                                    }
                                    adapter.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(XemChiTietDiaDiemActivity.this, "Chưa có bình luận nào !", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Setcontrols() {
        txtNameChiTiet = findViewById(R.id.txtNameChiTiet);
        txtAddressChiTiet = findViewById(R.id.txtAddressChiTiet);
        txtIntroduceChiTiet = findViewById(R.id.txtIntroduceChiTiet);
        imgHinh = findViewById(R.id.imgHinhChiTiet);
        btnGui = findViewById(R.id.btnCmt);
        edtCmt = findViewById(R.id.edtCmt);

        lvComment = findViewById(R.id.lvComment);
        arrayComment = new ArrayList<>();
        adapter = new ArrayAdapter(XemChiTietDiaDiemActivity.this,android.R.layout.simple_list_item_1,arrayComment);
        lvComment.setAdapter(adapter);

        nodeRoot = FirebaseDatabase.getInstance().getReference();
    }
}
