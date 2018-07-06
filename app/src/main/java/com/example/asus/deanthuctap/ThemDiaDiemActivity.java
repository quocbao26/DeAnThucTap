package com.example.asus.deanthuctap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.model.DiaDiemModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ThemDiaDiemActivity extends AppCompatActivity {

    private final static String TAG = ThemDiaDiemActivity.class.getSimpleName();
    public static final int ERROR_DIALOG_REQUEST = 9001;

    Spinner spTinhThanh;
    ArrayList<String> arrTinhThanh;
    ArrayAdapter<String> adapter;

    EditText edtTenDiaDiemThem,edtDiaChiThem,edtGioiThieuThem,edtKinhDo,edtViDo;
    Button btnLayAnhTuDT,btnThem;
    ImageView imgHinh;
    ImageButton imgbtnMap;

    DatabaseReference nodeRoot;
    StorageReference mountainsRef,storageRef;

    String seletectedSpinner = "";
    String keyValueTinhThanh ="";
    String ten,diachi,gioithieu,latitude,longitude;

    ProgressDialog progressDialog;

    String thongbao ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_dia_diem);

        nodeRoot = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://doanthuctap-44bd2.appspot.com");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Thông báo");
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCanceledOnTouchOutside(false);


        Setcontrols();
        addEvents();

        Intent intent = getIntent();
        edtKinhDo.setText(intent.getStringExtra("kinhdo"));
        edtViDo.setText(intent.getStringExtra("vido"));
        edtDiaChiThem.setText(intent.getStringExtra("diachi"));
        Log.d(TAG,"onCreate: đã nhận tọa độ: " +intent.getStringExtra("kinhdo")+", "+intent.getStringExtra("vido"));
    }

    private void addEvents() {
        nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null)
                {
                    for (DataSnapshot valueTinh : dataSnapshot.getChildren())
                    {
                        String tinh = valueTinh.getValue(String.class);
                        arrTinhThanh.add(tinh);
                    }
                    Log.e(TAG,arrTinhThanh+"");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spTinhThanh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                seletectedSpinner = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLyThemDiaDiem();
            }
        });
        btnLayAnhTuDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });


        if (isServicesOK()){
            imgbtnMap.setEnabled(true);
            imgbtnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ThemDiaDiemActivity.this,MapActivity.class));
                }
            });
        }else{
            imgbtnMap.setEnabled(false);
        }

    }



    public boolean isServicesOK() {
        Log.d(TAG, "is Services OK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ThemDiaDiemActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // everything  is fine and the user can make map request
            Log.d(TAG, "is ServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "is ServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ThemDiaDiemActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't  make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void xuLyThemDiaDiem() {
        ten = edtTenDiaDiemThem.getText().toString().trim();
        diachi = edtDiaChiThem.getText().toString().trim();
        gioithieu = edtGioiThieuThem.getText().toString().trim();
        latitude = edtKinhDo.getText().toString().trim();
        longitude = edtViDo.getText().toString().trim();
        if(ten.isEmpty() || diachi.isEmpty() || latitude.isEmpty() || longitude.isEmpty()){
            Toast.makeText(this, "Vui lòng không bỏ trống", Toast.LENGTH_SHORT).show();
        }else{

            try {

                progressDialog.show();
                Calendar calendar = Calendar.getInstance();
                mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");

                imgHinh.setDrawingCacheEnabled(true);
                imgHinh.buildDrawingCache();

                //chuyển hình về mảnh byte
                Bitmap bitmap = ((BitmapDrawable) imgHinh.getDrawable()).getBitmap();

                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                scaled.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                final UploadTask uploadTask = mountainsRef.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return mountainsRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    final Uri downloadUri = task.getResult();




                                    //lấy key auto của tỉnh thành từ Spinner
                                    nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot valueTinhThanh : dataSnapshot.getChildren()) {
                                                if (seletectedSpinner.equals(valueTinhThanh.getValue(String.class))) {
                                                    keyValueTinhThanh = valueTinhThanh.getKey();
                                                    Log.e(TAG, keyValueTinhThanh);
                                                }
                                            }


                                            String id = nodeRoot.child("diadiems").push().getKey();
                                            DiaDiemModel diadiem = new DiaDiemModel(id, ten, diachi, gioithieu, latitude, longitude,String.valueOf(downloadUri));

//                                            nodeRoot.child("hinhanhs").child(id).push().setValue(String.valueOf(downloadUri));


                                            nodeRoot.child("diadiems").child(keyValueTinhThanh).child(id).setValue(diadiem, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        Toast.makeText(ThemDiaDiemActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                                                        edtTenDiaDiemThem.setText("");
                                                        edtDiaChiThem.setText("");
                                                        edtGioiThieuThem.setText("");
                                                        edtKinhDo.setText("");
                                                        edtViDo.setText("");
                                                        imgHinh.setImageResource(R.drawable.no_images);
                                                        progressDialog.dismiss();
                                                    } else {
                                                        Toast.makeText(ThemDiaDiemActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                        });
                    }
                });
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }

        }

   }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgHinh.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("Loi Gallery: ",e.toString());
            }
        }
    }

    private void Setcontrols() {
        edtKinhDo = findViewById(R.id.edtKinhDo);
        edtViDo = findViewById(R.id.edtViDo);
        edtTenDiaDiemThem = findViewById(R.id.edtTenDiaDiemThem);
        edtDiaChiThem = findViewById(R.id.edtDiaChiThem);
        edtGioiThieuThem = findViewById(R.id.edtGioiThieuThem);
        btnLayAnhTuDT = findViewById(R.id.btnLayAnhTuDT);
        btnThem = findViewById(R.id.btnThem);
        imgbtnMap = findViewById(R.id.imgbtnMap);
        imgHinh = findViewById(R.id.imgHinhThem);

        spTinhThanh = findViewById(R.id.spTinhThanhThem);
        arrTinhThanh = new ArrayList<>();
        adapter = new ArrayAdapter<>(ThemDiaDiemActivity.this,android.R.layout.simple_spinner_item,arrTinhThanh);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTinhThanh.setAdapter(adapter);
    }
}
