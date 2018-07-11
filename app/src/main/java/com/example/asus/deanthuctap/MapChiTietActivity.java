package com.example.asus.deanthuctap;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.adapter.PlaceAutocompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapChiTietActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = "MapChiTietActivity";

     ImageView mGps,mDirection;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final Float DEFAULT_ZOOM = 15f;

    Boolean mLocationPermissionGranted = false;
    MapFragment mapFragment;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;

    Double latitude;
    Double longitude;
    Double latitudeDetail;
    Double longitudeDetail;
    String title="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_chi_tiet);
        mGps = findViewById(R.id.img_gps_chitiet);
        mDirection = findViewById(R.id.img_direction_chitiet);

        getLocationPermission();


        Intent intent = getIntent();
        latitudeDetail = Double.valueOf(intent.getStringExtra("latitude"));
        longitudeDetail = Double.valueOf(intent.getStringExtra("longitude"));
        title = intent.getStringExtra("title");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.e(TAG,"OnMapReady ---------------------");
        Toast.makeText(this, "Map on ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map on ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {

            getLocationSelected();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();

        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        mDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCamera(new LatLng(latitudeDetail,longitudeDetail),10,title);
            }
        });


        Log.e(TAG,latitudeDetail + " = " + longitudeDetail + " / " + title );

    }

    private void moveCamera(LatLng latLng,float zoom,String title){
        Log.d(TAG,"moveCamera");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));


            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);


    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if (mLocationPermissionGranted) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                final Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"onComplete: found location");
                            currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,
                                    "Bạn đang ở đây");

                        }else{
                            Log.d(TAG,"onComplete: current location is null");
                            Toast.makeText(MapChiTietActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }

        }catch (Exception ex){
            Log.e(TAG,ex.toString());
        }
    }

    private void getLocationSelected() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if (mLocationPermissionGranted) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                moveCamera(new LatLng(latitudeDetail,longitudeDetail),DEFAULT_ZOOM,title);

            }


        }catch (Exception ex){
            Log.e(TAG,ex.toString());
        }
    }


    private void initMap(){
        Log.d(TAG,"initMap: initializing Map ");
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.myMapChiTietFragment);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission(){
        Log.d(TAG,"getLocationPermission: getting location Permission");
        String [] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

        }

