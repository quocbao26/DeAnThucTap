package com.example.asus.deanthuctap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.model.DiaDiemModel;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class SuaDiaDiemActivity extends AppCompatActivity {

    private final static String TAG = SuaDiaDiemActivity.class.getSimpleName();
    public static final int ERROR_DIALOG_REQUEST = 9001;

    Spinner spTinhThanhEdit;
    ArrayList<String> arrTinhThanhEdit;
    ArrayAdapter<String> adapterEdit;

    EditText edtTenEdit,edtDiaChiEdit,edtGioiThieuEdit,edtKinhDoEdit,edtViDoEdit;
    Button btnLayAnhTuDTEdit,btnEdit;
    ImageView imgHinhEdit;

    DatabaseReference nodeRoot;
    StorageReference mountainsRef,storageRef;
    DiaDiemModel diaDiemModel;
    ProgressDialog progressDialog;

    String ten,diachi,gioithieu,latitude,longitude;
    String keyDiaDiem = "";
    String hinhanhSua = "";
    String keyTinhThanh = "";

    int vitri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_dia_diem);
        Setcontrols();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Thông báo");
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCanceledOnTouchOutside(false);

        nodeRoot = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://doanthuctap-44bd2.appspot.com");

        Intent intent = getIntent();
        diaDiemModel = (DiaDiemModel) intent.getSerializableExtra("object");
        vitri = intent.getIntExtra("vitriSpinner",0);
        keyTinhThanh = intent.getStringExtra("keySpinner");
        keyDiaDiem = diaDiemModel.getMadiadiem();
        hinhanhSua = diaDiemModel.getHinhanhdiadiem();
        Log.e(TAG,"đã nhận vị trí: "+vitri+"");
        Log.e(TAG,diaDiemModel.getLatitude() + ", " +diaDiemModel.getLongitude());

        addEvents();
    }

    private void addEvents() {
        nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot valueTinh : dataSnapshot.getChildren())
                {
                    String tinh = valueTinh.getValue(String.class);
                    arrTinhThanhEdit.add(tinh);
                }

                adapterEdit.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        spTinhThanhEdit.setSelection(vitri);
        spTinhThanhEdit.setEnabled(false);
        spTinhThanhEdit.setClickable(false);

        edtTenEdit.setText(diaDiemModel.getTendiadiem());
        edtDiaChiEdit.setText(diaDiemModel.getDiachi());
        edtGioiThieuEdit.setText(diaDiemModel.getGioithieu());
        edtViDoEdit.setText(diaDiemModel.getLatitude());
        edtKinhDoEdit.setText(diaDiemModel.getLongitude());
        Picasso.get().load(diaDiemModel.getHinhanhdiadiem()).resize(300,300).into(imgHinhEdit);


        btnLayAnhTuDTEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLySuaDiaDiem();
            }
        });
    }



    private void xuLySuaDiaDiem() {
        ten = edtTenEdit.getText().toString().trim();
        diachi = edtDiaChiEdit.getText().toString().trim();
        gioithieu = edtGioiThieuEdit.getText().toString().trim();
        latitude = edtViDoEdit.getText().toString().trim();
        longitude = edtKinhDoEdit.getText().toString().trim();
        if(ten.isEmpty()){
            Toast.makeText(this, "Vui lòng không bỏ trống", Toast.LENGTH_SHORT).show();
        }else{
            try {
                progressDialog.show();

                FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl((hinhanhSua));
                photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LOI ", e.toString());
                    }
                });

                Calendar calendar = Calendar.getInstance();
                mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");

                imgHinhEdit.setDrawingCacheEnabled(true);
                imgHinhEdit.buildDrawingCache();

                //chuyển hình về mảnh byte
                Bitmap bitmap = ((BitmapDrawable) imgHinhEdit.getDrawable()).getBitmap();

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


                                    DiaDiemModel diadiem = new DiaDiemModel(keyDiaDiem, ten, diachi, gioithieu, latitude, longitude, String.valueOf(downloadUri));

                                    nodeRoot.child("diadiems").child(keyTinhThanh).child(keyDiaDiem).setValue(diadiem, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                Toast.makeText(SuaDiaDiemActivity.this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SuaDiaDiemActivity.this,DanhSachDiaDiemActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SuaDiaDiemActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }
                });
                progressDialog.dismiss();
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgHinhEdit.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("Loi Gallery: ", e.toString());
            }
        }
    }

    private void Setcontrols() {
        edtKinhDoEdit = findViewById(R.id.edtKinhDoEdit);
        edtViDoEdit = findViewById(R.id.edtViDoEdit);
        edtTenEdit = findViewById(R.id.edtTenDiaDiemEdit);
        edtDiaChiEdit = findViewById(R.id.edtDiaChiEdit);
        edtGioiThieuEdit = findViewById(R.id.edtGioiThieuEdit);
        btnLayAnhTuDTEdit = findViewById(R.id.btnLayAnhTuDTEdit);
        btnEdit = findViewById(R.id.btnEdit);
        imgHinhEdit = findViewById(R.id.imgHinhEdit);

        spTinhThanhEdit = findViewById(R.id.spTinhThanhEdit);
        arrTinhThanhEdit = new ArrayList<>();
        adapterEdit = new ArrayAdapter<>(SuaDiaDiemActivity.this,android.R.layout.simple_spinner_item,arrTinhThanhEdit);
        adapterEdit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTinhThanhEdit.setAdapter(adapterEdit);
    }
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SuaDiaDiemActivity.this);
        builder.setMessage("Bạn có muốn quay lại trang danh sách ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
