package com.example.asus.deanthuctap;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.model.DiaDiemModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnDanhSachDiaDiem,btnThemDiaDiem,btnThemTinhThanh,btnTimKiem,btnThoat;
    DatabaseReference mTinhThanh;
    public static String user = "";
    public static String pass = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isConnected() == false) {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Thông báo")
                    .setMessage("Bạn chưa kết nối mạng !!!")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "Đã kết nối mạng", Toast.LENGTH_SHORT).show();
        }

        Setcontrols();
        addEvents();


    }



    private boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting())  return true;
        return false;
    }

    private void addEvents() {
        btnDanhSachDiaDiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,DanhSachDiaDiemActivity.class));
            }
        });
        btnThemDiaDiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ThemDiaDiemActivity.class));
            }
        });
        btnThemTinhThanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ThemTinhThanh.class));
            }
        });
        btnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TimKiemActivity.class));
            }
        });
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Bạn có chắc chắn thoát ?");
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
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_login)
        {
            if (user.length() > 0 && pass.length() > 0)
            {
                Toast.makeText(this, "Bạn đang đăng nhập với quyền quản trị", Toast.LENGTH_SHORT).show();
            }
            else{
                LoginQuanTri();
            }

        }
        if (item.getItemId() == R.id.menu_logout)
        {
            if (user.equals("") && pass.equals(""))
            {
                Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            }
            else {
                user = "";
                pass = "";
                Toast.makeText(this, "Bạn đã đăng xuất hệ thống", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
    private void LoginQuanTri(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_login);

        // khi click bên ngoài ko tắt
        dialog.setCanceledOnTouchOutside(false);

        final EditText edtUsername = dialog.findViewById(R.id.editTextUsername);
        final EditText edtPassword = dialog.findViewById(R.id.editTextPassword);
        Button btnDongy            = dialog.findViewById(R.id.buttonDongy);
        Button btnHuy              = dialog.findViewById(R.id.buttonHuy);

        btnDongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(username.equals("admin") && password.equals("admin")){
                    user = username;
                    pass = password;
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Đăng nhập admin thành công", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Bạn có chắc chắn thoát ?");
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

    private void Setcontrols() {
        btnDanhSachDiaDiem = findViewById(R.id.btnDanhSachDiaDiem);
        btnThemDiaDiem = findViewById(R.id.btnThemDiaDiem);
        btnThemTinhThanh = findViewById(R.id.btnThemTinh);
        btnTimKiem = findViewById(R.id.btnTimKiem);
        btnThoat = findViewById(R.id.btnThoat);

        mTinhThanh = FirebaseDatabase.getInstance().getReference();

    }
}
