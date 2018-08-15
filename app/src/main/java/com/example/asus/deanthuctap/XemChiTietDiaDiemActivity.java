package com.example.asus.deanthuctap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.adapter.CommentAdapter;
import com.example.asus.model.Comment;
import com.example.asus.model.DiaDiemModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class XemChiTietDiaDiemActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = XemChiTietDiaDiemActivity.class.getSimpleName();
    int REQUEST_CODE_GMAIL = 3;

    TextView txtNameChiTiet,txtAddressChiTiet,txtIntroduceChiTiet,txtTenGmail;
    ImageView imgHinh;
    ImageButton btnLoginGmail,btnLogoutGmail;
    TextView txtCmtNew;

    GoogleApiClient signInApi;
    DatabaseReference nodeRoot;
    DiaDiemModel diaDiemModel;
    LocationManager locationManager;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


    ListView lvComment;
    ArrayList<Comment> arrayComment;
    CommentAdapter adapter;
    String cmtXoa = "";

    String madd="";
    Double latitude, longitude;
    String ten="",ngay = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_chi_tiet_dia_diem);
        Setcontrols();
        addEvents();

    }

    private void addEvents() {

        Intent intent = getIntent();
        diaDiemModel = (DiaDiemModel) intent.getSerializableExtra("chitiet_diadiem");
        txtNameChiTiet.setText(diaDiemModel.getTendiadiem());
        txtAddressChiTiet.setText(diaDiemModel.getDiachi());
        txtIntroduceChiTiet.setText(diaDiemModel.getGioithieu());
        latitude = Double.valueOf(diaDiemModel.getLatitude());
        longitude = Double.valueOf(diaDiemModel.getLongitude());
        Picasso.get().load(diaDiemModel.getHinhanhdiadiem()).into(imgHinh);
        madd = diaDiemModel.getMadiadiem();

        loadComment();
        Log.e(TAG,sdf.format(calendar.getTime()));

        txtCmtNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ten = txtTenGmail.getText().toString().trim();
                    if (ten.isEmpty())
                    {
                        ten = "Người Vô Danh";
                    }
                    ngay = sdf.format(calendar.getTime());

                    Intent intent = new Intent(XemChiTietDiaDiemActivity.this,BinhLuanActivity.class);
                    intent.putExtra("pdiadiem",madd);
                    intent.putExtra("ten",ten);
                    intent.putExtra("ngay",ngay);
                    startActivity(intent);

                }

        });

        txtAddressChiTiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    buildAlertMessageNoGps();
                }else{
                    Intent intent = new Intent(XemChiTietDiaDiemActivity.this,MapChiTietActivity.class);
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("title", diaDiemModel.getTendiadiem());
                    Log.e(TAG,latitude + " - " + longitude);
                    startActivity(intent);
                }
                
            }
        });

        // yêu cầu trả về gì
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
//                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .build();

        // khởi tạo Client kết nối để activity_login Google (mở màn hình activity_login google, truyền thông tin vào)
        signInApi = new GoogleApiClient.Builder(XemChiTietDiaDiemActivity.this)
                .enableAutoManage(XemChiTietDiaDiemActivity.this,XemChiTietDiaDiemActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        btnLoginGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDangNhapGoogle = Auth.GoogleSignInApi.getSignInIntent(signInApi);
                Log.e(TAG,"Sign IN API: "+signInApi);
                startActivityForResult(iDangNhapGoogle,REQUEST_CODE_GMAIL);
            }
        });
        btnLogoutGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(signInApi).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(false);
                        txtTenGmail.setText("");
                    }
                });
            }
        });
        lvComment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (MainActivity.user.equals("admin") && MainActivity.pass.equals("admin"))
                {
                    cmtXoa = arrayComment.get(position).getComment();
//                    position  = i;
                    AlertDialog.Builder builder = new AlertDialog.Builder(XemChiTietDiaDiemActivity.this);
                    builder.setMessage("Bạn có chắc chắn muốn xóa " + cmtXoa + " không ?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int i) {


                            nodeRoot.child("binhluans").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot keyDDBinhLuans: dataSnapshot.getChildren()){
                                        for (DataSnapshot keyBinhLuans: keyDDBinhLuans.getChildren())
                                        {
                                            Log.e(TAG,keyBinhLuans.toString());
                                            Comment valueBinhLuan = keyBinhLuans.getValue(Comment.class);
                                            Log.e(TAG,cmtXoa +", "+valueBinhLuan.getComment());
                                            if(cmtXoa.equals(valueBinhLuan.getComment())){
                                                Log.d(TAG,"Đã xóa " + cmtXoa );
                                                nodeRoot.child("binhluans").child(keyDDBinhLuans.getKey()).child(keyBinhLuans.getKey()).removeValue();
                                                Toast.makeText(XemChiTietDiaDiemActivity.this, "Đã xóa "+cmtXoa, Toast.LENGTH_SHORT).show();
                                        }

                                        }

                                    }
                                    cmtXoa = "";
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
                }else{
                    Toast.makeText(XemChiTietDiaDiemActivity.this, "Bạn không có quyền cập nhật", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void updateUI(boolean isLogin)
    {
        if (isLogin)
        {
            btnLogoutGmail.setVisibility(View.VISIBLE);
            btnLoginGmail.setVisibility(View.INVISIBLE);
        }
        else
        {
            btnLoginGmail.setVisibility(View.VISIBLE);
            btnLogoutGmail.setVisibility(View.INVISIBLE);
        }
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadComment(){
        Log.d(TAG,"load comment");
        nodeRoot.child("binhluans").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot valueKeyDiaDiem : dataSnapshot.getChildren())
                {
                    final String value = valueKeyDiaDiem.getKey();
                    if(madd.equals(value)){

                        nodeRoot.child("binhluans").child(madd).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot != null){
                                    arrayComment.clear();
                                    for (DataSnapshot valueComt : dataSnapshot.getChildren()){
                                        Comment comment = valueComt.getValue(Comment.class);
                                        Log.e(TAG,comment.getTen() + ", " + comment.getNgay() + ", " + comment.getComment());
                                        arrayComment.add(comment);
                                    }
                                    adapter.notifyDataSetChanged();
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
        btnLoginGmail = findViewById(R.id.btnLoginGmail);
        btnLogoutGmail = findViewById(R.id.btnLogoutGmail);
        btnLogoutGmail.setVisibility(View.INVISIBLE);
        txtTenGmail = findViewById(R.id.txtTenMail);

        txtNameChiTiet = findViewById(R.id.txtNameChiTiet);
        txtAddressChiTiet = findViewById(R.id.txtAddressChiTiet);
        txtIntroduceChiTiet = findViewById(R.id.txtIntroduceChiTiet);
        imgHinh = findViewById(R.id.imgHinhChiTiet);
        txtCmtNew = findViewById(R.id.txtCmtNew);

        lvComment = findViewById(R.id.lvComment);
        arrayComment = new ArrayList<>();
        adapter = new CommentAdapter(XemChiTietDiaDiemActivity.this,R.layout.item_binhluan,arrayComment);
        lvComment.setAdapter(adapter);

        nodeRoot = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GMAIL && resultCode == RESULT_OK )
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                String name = account.getDisplayName();
                txtTenGmail.setText(name);
                Toast.makeText(this, "Login Gmail thành công", Toast.LENGTH_SHORT).show();
                updateUI(true);
            }
            else{
                Toast.makeText(this, "Login Gmail thất bại", Toast.LENGTH_SHORT).show();
                updateUI(false);
            }
        }




//        if(requestCode == REQUEST_CODE_GMAIL)
//        {
//            Log.d(TAG,"Nhận RESULT: "+resultCode +"======="+RESULT_OK);
//            if(resultCode == RESULT_OK)
//            {
//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//                String tokenID = result.getSignInAccount().getIdToken();
//                Log.d(TAG,"Lấy ID token: "+tokenID);
//                AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenID,null);
//                mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful())
//                        {
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            txtTenGmail.setText(user.getDisplayName());
//                            Toast.makeText(XemChiTietDiaDiemActivity.this,
//                                    "Đăng nhập Gmail thành công", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//            }
//            else {
//                Toast.makeText(XemChiTietDiaDiemActivity.this,
//                        "Đăng nhập Gmail thất bại", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
