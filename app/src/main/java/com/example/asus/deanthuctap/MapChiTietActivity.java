package com.example.asus.deanthuctap;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    ProgressDialog progressDialog;

    Double latitudeDetail;
    Double longitudeDetail;
    String title="";

    //direction
    ArrayList<LatLng> listStep;
    PolylineOptions polyline;
    LatLng locationDevice,locationChiTiet;

    List<Marker> originMarkers;
    List<Marker> destinationMarkers;
    List<Polyline> polylinePaths;
    List markerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_chi_tiet);

        Setcontrols();
        markerPoints.clear();

        getLocationPermission();

        getDeviceLocation();


        Intent intent = getIntent();
        latitudeDetail = intent.getDoubleExtra("latitude",0);
        longitudeDetail = intent.getDoubleExtra("longitude",0);
        title = intent.getStringExtra("title");
        Log.e(TAG,latitudeDetail + " - " + longitudeDetail + " - " +title);
        locationChiTiet = new LatLng(latitudeDetail,longitudeDetail);
        markerPoints.add(locationChiTiet);

    }

    private void Setcontrols() {
        mGps = findViewById(R.id.img_gps_chitiet);
        mDirection = findViewById(R.id.img_direction_chitiet);
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        polylinePaths = new ArrayList<>();
        markerPoints = new ArrayList();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.e(TAG,"OnMapReady ---------------------");
        Toast.makeText(this, "Map on ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map on ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {

            getLocationSelected();
            markerPoints.add(locationDevice);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
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
                getDeviceLocationAndMoveCamere();
            }
        });
        mDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                veDuongDi();
                Log.e(TAG,"Vị trí device: "+locationDevice.latitude + "," + locationDevice.longitude);
                Log.e(TAG,"Vị trí dia diem: "+locationChiTiet.latitude + "," + locationChiTiet.longitude);
            }
        });


        Log.e(TAG,latitudeDetail + " = " + longitudeDetail + " / " + title );

    }
    private void veDuongDi() {
        Log.e(TAG,markerPoints.size()+"");
        if(markerPoints.size() >= 2)
        {
            String url = getDirectionsUrl(locationDevice,locationChiTiet);
            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }

    }

    private String getDirectionsUrl(LatLng locationDevice, LatLng locationChiTiet) {
        // Origin
        String str_origin = "origin=" + locationDevice.latitude + "," + locationDevice.longitude;

        // Destination
        String str_dest = "destination=" + locationChiTiet.latitude + "," + locationChiTiet.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode ;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e(TAG,"URL: "+url);


        return url;
    }

    class DownloadTask extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ParserTask parserTask = new ParserTask();

            Log.e(TAG,"DownloadTask(onPostExecute): "+s);
            parserTask.execute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            InputStreamReader isr = null;
            HttpURLConnection httpURLConnection = null;
            try {

                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                isr = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                StringBuffer builder = new StringBuffer();
                String line = null;
                while((line = br.readLine()) != null)
                {
                    builder.append(line);
                }
                data = builder.toString();
                br.close();

            } catch (Exception e) {
                Log.e("doInBackground: ", e.toString());
            }finally {
                httpURLConnection.disconnect();
            }
            return data;
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                Log.e(TAG,"jsonData: "+jsonData[0]);
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG,"doInBackground: "+routes);
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String,String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            Log.e(TAG,"onPostExecute: "+result);
            Log.e(TAG,"onPostExecute(size_result): "+result.size());
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = result.get(i);
                Log.e(TAG,"onPostExecute(size_path): "+path.size());
                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble(String.valueOf(point.get("lat")));
                    double lng = Double.parseDouble(String.valueOf(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
//                    Log.e(TAG,"Array Points: "+points.toString());
                }

                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
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

                            // loi chua dong bo ngay
                            locationDevice = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

                        }else{
                            Log.d(TAG,"onComplete: current location is null");
                            Toast.makeText(MapChiTietActivity.this, "Không thể lấy địa điểm thiết bị", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }

        }catch (Exception ex){
            Log.e(TAG,ex.toString());
        }
    }

    private void getDeviceLocationAndMoveCamere() {
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
                            Toast.makeText(MapChiTietActivity.this, "Không thể lấy địa điểm thiết bị", Toast.LENGTH_SHORT).show();
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

