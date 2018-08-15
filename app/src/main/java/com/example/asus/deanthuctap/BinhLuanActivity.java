package com.example.asus.deanthuctap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BinhLuanActivity extends AppCompatActivity {

    private static final String TAG = BinhLuanActivity.class.getSimpleName();

    RatingBar mRatingBar;
    TextView txtStatus;
    EditText edtCmt;
    Button btnCmt;

    DatabaseReference nodeRoot;
    Comment comment;

    String ten = "", ngay = "", pDiaDiem = "";
    String binhluan = "";
    int sao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binh_luan);
        Setcontrols();
        addEvents();
    }

    private void addEvents() {

        Intent intent = getIntent();
        pDiaDiem = intent.getStringExtra("pdiadiem");
        ten = intent.getStringExtra("ten");
        ngay = intent.getStringExtra("ngay");

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                txtStatus.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                    {
                        txtStatus.setText("Rất tệ");
                        sao = 1;
                        break;
                    }
                    case 2:
                    {
                        txtStatus.setText("Cần cải thiện hơn");
                        sao = 2;
                        break;
                    }
                    case 3:
                    {
                        txtStatus.setText("Tốt");
                        sao = 3;
                        break;
                    }
                    case 4:
                    {
                        txtStatus.setText("Tuyệt");
                        sao = 4;
                        break;
                    }
                    case 5:
                    {
                        txtStatus.setText("Quá tốt");
                        sao = 5;
                        break;
                    }
                    default:
                        txtStatus.setText("");
                }
//                sao = (int) ratingBar.getRating();
            }
        });



        btnCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Đã thêm bình luận mới");
                Log.e(TAG,"Số sao: "+sao);
                binhluan = edtCmt.getText().toString().trim();
                if (binhluan.isEmpty())
                {
                    Toast.makeText(BinhLuanActivity.this, "Vui lòng nhập bình luận!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    comment = new Comment(ten,ngay,binhluan,String.valueOf(sao));
                    nodeRoot.child("binhluans").child(pDiaDiem).push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(BinhLuanActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BinhLuanActivity.this, "Bình luận thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void Setcontrols() {
        nodeRoot = FirebaseDatabase.getInstance().getReference();
        mRatingBar =  findViewById(R.id.ratingBar);
        txtStatus =  findViewById(R.id.txtStatus);
        edtCmt =  findViewById(R.id.edtCmt);
        btnCmt =  findViewById(R.id.btnCmt);
    }
}
