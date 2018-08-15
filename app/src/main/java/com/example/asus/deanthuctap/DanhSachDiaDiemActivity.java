package com.example.asus.deanthuctap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.adapter.DiaDiemAdapter;

import com.example.asus.model.DiaDiemModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class DanhSachDiaDiemActivity extends AppCompatActivity   {

    private final static String TAG = DanhSachDiaDiemActivity.class.getSimpleName();


    Spinner sptinhThanh;
    ArrayList<String> arrayTinhThanh;
    ArrayAdapter<String> adapterTinhThanh;

    GridView griviewItem;
    DiaDiemAdapter adapter;
    ArrayList<DiaDiemModel> diaDiemModelArrayList;
    DatabaseReference nodeRoot;


    String selectedChoose = "";
    String keySelected="";
    String keyXoa="";
    int vitri = -1;
    DiaDiemModel diaDiemModel;
    String madiadiemXoa="",hinhanhXoa="",tendiadiemXoa="",diachiXoa="",gioithieuXoa="",latitudeXoa ="",longitudeXoa ="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach);

        nodeRoot = FirebaseDatabase.getInstance().getReference();
        Setcontrol();

        addEvents();

    }

    private void addEvents() {

        nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot valueTinh : dataSnapshot.getChildren())
                {
                    String tinh = valueTinh.getValue(String.class);
                    arrayTinhThanh.add(tinh);
                }

                adapterTinhThanh.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        sptinhThanh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedChoose = (String) adapterView.getItemAtPosition(i);
                vitri = adapterView.getSelectedItemPosition();
                Log.e("Vtri sp ",vitri+"");
                LoadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // Click giữ item
        griviewItem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {

                madiadiemXoa    = diaDiemModelArrayList.get(i).getMadiadiem();
                hinhanhXoa      = diaDiemModelArrayList.get(i).getHinhanhdiadiem();
                tendiadiemXoa   = diaDiemModelArrayList.get(i).getTendiadiem();
                diachiXoa       = diaDiemModelArrayList.get(i).getDiachi();
                gioithieuXoa    = diaDiemModelArrayList.get(i).getGioithieu();
                latitudeXoa     = diaDiemModelArrayList.get(i).getLatitude();
                longitudeXoa    = diaDiemModelArrayList.get(i).getLongitude();
                diaDiemModel = new DiaDiemModel(madiadiemXoa,tendiadiemXoa,diachiXoa,gioithieuXoa,latitudeXoa, longitudeXoa, hinhanhXoa);

                CharSequence []item = {"Chỉnh sửa","Xóa"};
                AlertDialog.Builder builderOption = new AlertDialog.Builder(DanhSachDiaDiemActivity.this);
                builderOption.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(MainActivity.user.equals("") && MainActivity.pass.equals(""))
                        {
                            Toast.makeText(DanhSachDiaDiemActivity.this, "Bạn ko có quyền sửa hoặc xóa", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(i == 0)
                            {
                                Intent intent = new Intent(DanhSachDiaDiemActivity.this,SuaDiaDiemActivity.class);
                                intent.putExtra("object",diaDiemModel);
                                intent.putExtra("vitriSpinner",vitri);
                                intent.putExtra("keySpinner",keySelected);
                                Log.e(TAG,"đã truyền vị trí: "+vitri);
                                Log.e(TAG,"key Spinner: "+keySelected);
                                startActivity(intent);
                            }else
                            if(i == 1)
                            {
                                XoaDiaDiem(diaDiemModel);
                            }
                        }
                    }

                });
                builderOption.show();
                return true;
            }


        });

        // click item
        griviewItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DanhSachDiaDiemActivity.this,XemChiTietDiaDiemActivity.class);
                intent.putExtra("chitiet_diadiem",diaDiemModelArrayList.get(i));
                startActivity(intent);
            }
        });

}


    private void XoaDiaDiem(DiaDiemModel diaDiemModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DanhSachDiaDiemActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Bạn có muốn xóa " + diaDiemModel.getTendiadiem() + " không ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot valueTinhThanh : dataSnapshot.getChildren()) {
                            if (selectedChoose.equals(valueTinhThanh.getValue(String.class))) {
                                keyXoa = valueTinhThanh.getKey();

                            }
                        }
                        nodeRoot.child("diadiems").child(keyXoa).child(madiadiemXoa).removeValue();
                        nodeRoot.child("binhluans").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot keyDiaDiemBinhLuan: dataSnapshot.getChildren())
                                {
                                    if (madiadiemXoa.equals(keyDiaDiemBinhLuan.getKey()))
                                    {
                                        nodeRoot.child("binhluans").child(madiadiemXoa).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        // chui vào Storage xóa
                        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl((hinhanhXoa));
                        photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DanhSachDiaDiemActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DanhSachDiaDiemActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                                Log.e("LOI ", e.toString());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void LoadData(){
        nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG,"Moi vao tinhthanhs: " + dataSnapshot.toString());
                for (DataSnapshot valueTinhThanh : dataSnapshot.getChildren()) {
                    if (selectedChoose.equals(valueTinhThanh.getValue(String.class))) {
                        keySelected = valueTinhThanh.getKey();
                    }
                }


                nodeRoot.child("diadiems").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        diaDiemModelArrayList.clear();
                        Log.e(TAG,"Moi vao diadiems: " + dataSnapshot.toString());
                        for(DataSnapshot dataKey: dataSnapshot.getChildren()){
                            if(dataKey.getKey().equals(keySelected)){
                                for(DataSnapshot dataValue: dataKey.getChildren()){
                                    DiaDiemModel diaDiemModel = dataValue.getValue(DiaDiemModel.class);
                                    diaDiemModelArrayList.add(new DiaDiemModel(diaDiemModel.getMadiadiem(),
                                            diaDiemModel.getTendiadiem(),
                                            diaDiemModel.getDiachi(),
                                            diaDiemModel.getGioithieu(),
                                            diaDiemModel.getLatitude(),
                                            diaDiemModel.getLongitude(),
                                            diaDiemModel.getHinhanhdiadiem()));
                                    adapter.notifyDataSetChanged();
                                }
                                Log.e(TAG,"Mảng có " + diaDiemModelArrayList.size() + " địa điểm");
                            }
                            else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        adapter.notifyDataSetChanged();
    }


    private void Setcontrol() {

        sptinhThanh = findViewById(R.id.spList);
        arrayTinhThanh = new ArrayList<>();
        adapterTinhThanh = new ArrayAdapter<>(DanhSachDiaDiemActivity.this,android.R.layout.simple_spinner_item,arrayTinhThanh);
        adapterTinhThanh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sptinhThanh.setAdapter(adapterTinhThanh);

        diaDiemModelArrayList = new ArrayList<>();
        adapter = new DiaDiemAdapter(DanhSachDiaDiemActivity.this,R.layout.item_diadiem,diaDiemModelArrayList);
        griviewItem = findViewById(R.id.griviewItem);
        griviewItem.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
