package com.example.asus.deanthuctap;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.adapter.PlaceAutocompleteAdapter;
import com.example.asus.model.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final Float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168),new LatLng(71,136)
    );

    Button btnXacNhanToaDo;
    AutoCompleteTextView mSearchText;
    ImageView mGps;

    Boolean mLocationPermissionGranted = false;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;
    GoogleMap mMap;
    MapFragment mapFragment;
    PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    GoogleApiClient mGoogleApiClient;
    PlaceInfo  mPlace;

    String kinhDo = "";
    String viDo = "";
    String diaChi = "";
    String ten = "";
    String gioithieu = "";
    int vitriSpinner;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        btnXacNhanToaDo = findViewById(R.id.btnXacNhanToaDo);
        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.img_gps);


        Intent intent = getIntent();
        ten = intent.getStringExtra("ten");
        gioithieu = intent.getStringExtra("gioithieu");
        vitriSpinner = intent.getIntExtra("vitriSpinner",0);
        Log.e(TAG,"Nhận vị trí: "+vitriSpinner+"");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải..");

        Log.e(TAG,"OnCreate ---------------------");
        getLocationPermission();


        btnXacNhanToaDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Location selected: "+kinhDo+", "+viDo);
                Intent intent = new Intent(MapActivity.this,ThemDiaDiemActivity.class);
                intent.putExtra("kinhdo",kinhDo);
                intent.putExtra("vido",viDo);
                intent.putExtra("diachi",diaChi);
                intent.putExtra("ten",ten);
                intent.putExtra("gioithieu",gioithieu);
                intent.putExtra("vitriSpinner",vitriSpinner);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressDialog.show();
        Log.e(TAG,"OnMapReady ---------------------");
        Toast.makeText(this, "Map on ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map on ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
        progressDialog.dismiss();
    }

    private void init(){
        Log.d(TAG,"init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();

        mSearchText.setOnItemClickListener(mAutoCompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    // excute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyBoard();
    }

    private void geoLocate() {
        Log.d(TAG,"geoLocate: geoLocating");

        String searching =  mSearchText.getText().toString();

        Geocoder  geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searching,1);
        }catch (Exception ex){
            Log.e(TAG,"geoLocate: "+ex.toString());
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG,"geoLocate: found a location "+address.toString());

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
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
                                Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



            }

        }catch (Exception ex){
            Log.e(TAG,ex.toString());
        }
    }

    private void moveCamera(LatLng latLng,float zoom,String title){
        Log.d(TAG,"moveCamera");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        if (!title.equals("Bạn đang ở đây")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyBoard();
    }


    private void initMap(){
        Log.d(TAG,"initMap: initializing Map ");
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.myMapFragment);
        mapFragment.getMapAsync(this);
    }


    private void getLocationPermission(){
        Log.d(TAG,"getLocationPermission: lấy quyền địa điểm");
        String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult: đang gọi.");
        mLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0 ){
                    for(int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG,"onRequestPermissionsResult: bị lỗi.");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionsResult: được cấp quyền.");
                    mLocationPermissionGranted = true;

                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyBoard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*
        ---------------- google places API autocomplete suggestions ---------------------
        ----------------
    */

    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyBoard();

            AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            String placeID = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeID);
            placeResult.setResultCallback(mUpdatePlaceDetailCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG,"onResult: Place query did not  complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
//                mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setId(place.getId().toString());
                mPlace.setLatLng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhonenumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());

                Log.d(TAG,"onResult: places details: " + mPlace.toString());
            }catch (Exception ex){
                Log.e(TAG,"onResult: NullPointerException "+ex.toString());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude),DEFAULT_ZOOM,mPlace.getName());

            kinhDo = String.valueOf(place.getViewport().getCenter().longitude);
            viDo = String.valueOf(place.getViewport().getCenter().latitude);
            diaChi = String.valueOf(place.getAddress());
            places.release();
        }
    };

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setMessage("Bạn có muốn quay lại ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MapActivity.this,ThemDiaDiemActivity.class));
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
