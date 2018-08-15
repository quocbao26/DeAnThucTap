package com.example.asus.deanthuctap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import com.example.asus.adapter.DiaDiemAdapter;
import com.example.asus.model.DiaDiemModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class TimKiemActivity extends AppCompatActivity {

    SearchView sv;
    DiaDiemModel diaDiemModel;

    GridView gvSearchDiaDiem;
    ArrayList <DiaDiemModel> arrayDiaDiem;
    DiaDiemAdapter adapter;

    DatabaseReference nodeRoot;

    private static final String TAG = "TimKiemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_kiem);
        Setcontrols();
        load_All_Diadiem();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        gvSearchDiaDiem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TimKiemActivity.this,XemChiTietDiaDiemActivity.class);
                intent.putExtra("chitiet_diadiem",arrayDiaDiem.get(i));
                startActivity(intent);
            }
        });
    }


    private void load_All_Diadiem(){
        nodeRoot.child("diadiems").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG,"Moi vao"+dataSnapshot.toString());
                for (DataSnapshot keyDiaDiem : dataSnapshot.getChildren())
                {
                    Log.e(TAG,"KeyDiaDiem"+keyDiaDiem.toString());
                    diaDiemModel = keyDiaDiem.getValue(DiaDiemModel.class);
                    arrayDiaDiem.add(diaDiemModel);
                    Log.d(TAG,diaDiemModel.getTendiadiem());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void Setcontrols() {
        sv = findViewById(R.id.svDiaDiem);
        gvSearchDiaDiem = findViewById(R.id.gvSearchDiaDiem);
        arrayDiaDiem = new ArrayList<>();
        adapter = new DiaDiemAdapter(TimKiemActivity.this,R.layout.item_diadiem,arrayDiaDiem);
        gvSearchDiaDiem.setAdapter(adapter);

        nodeRoot = FirebaseDatabase.getInstance().getReference();
    }
}
