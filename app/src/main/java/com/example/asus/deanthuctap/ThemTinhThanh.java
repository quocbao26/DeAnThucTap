package com.example.asus.deanthuctap;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ThemTinhThanh extends AppCompatActivity {

    private static final String TAG = "ThemTinhThanh";

    DatabaseReference nodeRoot;
    EditText edtTinhThanh;
    Button btnThemTinhThanh;

    ListView lvTinhThanh;
    ArrayList<String> arrTinhThanh;
    ArrayAdapter adapter;

    String themmoi = " ",ten = "";
//    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_tinh_thanh);
        Log.d(TAG,"onCreate");
        Setcontrols();
        loadTinhThanh();
        addEvents();
    }

    private void addEvents() {

        btnThemTinhThanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                themmoi = edtTinhThanh.getText().toString().trim();
                if(themmoi.isEmpty()){
                    Toast.makeText(ThemTinhThanh.this, "Chưa có nội dung", Toast.LENGTH_SHORT).show();
                }else{
                    nodeRoot.child("tinhthanhs").push().setValue(themmoi);
                    Toast.makeText(ThemTinhThanh.this, "Đã thêm "+themmoi, Toast.LENGTH_SHORT).show();
                    edtTinhThanh.setText("");
                }

            }
        });

        lvTinhThanh.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                if (MainActivity.user.equals("admin") && MainActivity.pass.equals("admin"))
                {
                    ten = (String) adapterView.getItemAtPosition(i);
//                    position  = i;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThemTinhThanh.this);
                    builder.setMessage("Bạn có chắc chắn muốn xóa " + ten + " không ?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int i) {
                            Log.e(TAG,"Mảng trước khi xóa "+arrTinhThanh.toString());

                            nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot valueXoas: dataSnapshot.getChildren()){
                                        String valueXoa = valueXoas.getValue(String.class);
                                        if(ten.equals(valueXoa)){
                                            Log.d(TAG,"Đã xóa " + ten );
                                            nodeRoot.child("tinhthanhs").child(valueXoas.getKey()).removeValue();
                                            Toast.makeText(ThemTinhThanh.this, "Đã xóa "+ten, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    ten = "";
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
                else{
                    Toast.makeText(ThemTinhThanh.this, "Bạn không có quyền cập nhật", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void loadTinhThanh(){
        Log.d(TAG,"Load DATA");
        nodeRoot.child("tinhthanhs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                arrTinhThanh.clear();

                for(DataSnapshot valueTinhThanhs : dataSnapshot.getChildren())
                {
                    String tinhthanh = valueTinhThanhs.getValue(String.class);
                    arrTinhThanh.add(tinhthanh);
                }
                Log.d(TAG,"Mảng đã load: "+arrTinhThanh.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Setcontrols() {
        Log.d(TAG,"Setcontrols");
        nodeRoot = FirebaseDatabase.getInstance().getReference();
        edtTinhThanh = findViewById(R.id.edtTinhThanhAdd);
        btnThemTinhThanh = findViewById(R.id.btnThemTinhThanh);

        lvTinhThanh = findViewById(R.id.lvTinhThanh);
        arrTinhThanh = new ArrayList<>();
        adapter = new ArrayAdapter(ThemTinhThanh.this,android.R.layout.simple_list_item_1,arrTinhThanh);
        lvTinhThanh.setAdapter(adapter);
    }
}
