package com.example.gpstracking;


import static com.example.gpstracking.R.layout.activity_routeand_bus;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.animation.AccelerateDecelerateInterpolator;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpstracking.directionhelpers.FetchURL;
import com.example.gpstracking.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gpstracking.databinding.ActivityRouteandBusBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import java.security.acl.Group;
import java.text.DecimalFormat;

public class RouteandBus extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback,  LocationListener  {

    private GoogleMap mMap;
    private ActivityRouteandBusBinding binding;
    private ImageView getlocation, backbutton;
    LocationManager locationManager;
    private DatabaseReference mUser, mroutes, mstops;
    LocationListener locationListener;
    private TextView textView;
    LatLng userLatLong, lats;
    private TextView directionwalk;
    DatabaseReference reference;
    Marker currLocationMarker, getCurrLocationMarker;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;
    Bitmap BitMapMarker, BitMapMarker1, BitMarker3;
    private Marker carMarker, carMarker1;


    private BottomNavigationView bottomr;
    private Fragment selectorFragment, myfrag;
    private Group group;
    private SearchView searchView;
    private BottomSheetBehavior bottomsheet;
    private String Names, Emails;
    private NavigationView navigationView;
    private TextView tv_result2;
    private FrameLayout myframe;
    Location myLocation = null;
    private ImageView getPoly;

    Location myUpdatedLocation = null;
    float Bearing = 0;
    boolean AnimationStatus = false;


    private final static int LOCATION_REQUEST_CODE = 23;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(activity_routeand_bus);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        backbutton = findViewById(R.id.backbutton);
        getlocation = findViewById(R.id.getmylocation);
        textView = findViewById(R.id.myduration);
        BitmapDrawable bitmapdraw3 = (BitmapDrawable) getResources().getDrawable(R.drawable.pickups);
        Bitmap c = bitmapdraw3.getBitmap();

        BitMarker3 = Bitmap.createScaledBitmap(c, 150, 100, false);

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bustruck);
        Bitmap b = bitmapdraw.getBitmap();
        BitMapMarker = Bitmap.createScaledBitmap(b, 160, 100, false);
getPoly = findViewById(R.id.directionvehicle);
        BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.posimarker);
        Bitmap a = bitmapdraw1.getBitmap();

        BitMapMarker1 = Bitmap.createScaledBitmap(a, 100, 100, false);
        mstops = FirebaseDatabase.getInstance().getReference("Stops");
        mstops.push().setValue(carMarker1);
        myframe = findViewById(R.id.myframe);
        mUser = FirebaseDatabase.getInstance().getReference("Drivers");
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RouteandBus.this, MapsActivity.class));
            }
        });
        getPoly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double lat = getIntent().getExtras().getDouble("latitude");
                Double lng = getIntent().getExtras().getDouble("longitude");
                String name = getIntent().getExtras().getString("routename");
setCameraWithCoordinationBounds(new LatLng(lat,lng));



            }
        });

        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLocation();
                setCameraWithCoordinationBounds(userLatLong);


            }
        });

        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    void changePositionSmoothly(final Marker myMarker, final LatLng newLatLng, final Float bearing) {

        final LatLng startPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        final LatLng finalPosition = newLatLng;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                myMarker.setRotation(bearing);
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                myMarker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 600);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }

                myLocation.setLatitude(newLatLng.latitude);
                myLocation.setLongitude(newLatLng.longitude);
            }
        });
    }
    void addstops(GoogleMap googleMap){
        mMap = googleMap;
        mstops.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                if (snapshots.exists()) {
                    if (carMarker1!=null){
                        carMarker1.remove();
                    }

                    for (DataSnapshot b : snapshots.getChildren()) {

                        GetRoute getRoute = b.getValue(GetRoute.class);
                        LatLng loc = new LatLng(getRoute.lat, getRoute.lng);
                       carMarker1 = mMap.addMarker(new MarkerOptions().position(loc).title(getRoute.routeName).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMarker3)));


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void myLocation(){

        locationManager = (LocationManager) this.getSystemService(android.content.Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (AnimationStatus) {
                    myUpdatedLocation = location;
                }

                myLocation = location;

                userLatLong = new LatLng(location.getLatitude(), location.getLongitude());

                if (carMarker != null){
                    carMarker.remove();
                }
                carMarker= mMap.addMarker(new MarkerOptions().position(userLatLong).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker1)));







                Bearing = location.getBearing();
                LatLng updatedLatLng = new LatLng(userLatLong.latitude, userLatLong.longitude);
                changePositionSmoothly(carMarker, updatedLatLng, Bearing);
            }
        };
        askLocation();

    }

    public void getLoc(GoogleMap googleMap) {
        locationManager = (LocationManager) this.getSystemService(android.content.Context.LOCATION_SERVICE);

        mMap = googleMap;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                userLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                Double lat = getIntent().getExtras().getDouble("latitude");
                Double lng = getIntent().getExtras().getDouble("longitude");
                String name = getIntent().getExtras().getString("routename");






                // Set listeners for click events.
                mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                    @Override
                    public void onPolygonClick(@NonNull Polygon polygon) {

                    }
                });
                LatLng mylat = new LatLng(lat, lng);
                String conv = mylat.toString();
                Location userlocation = new Location("point A");
                userlocation.setLatitude(35.2191);
                userlocation.setLongitude(33.4175);
                Location nearest = new Location("point B");


                nearest.setLongitude(lng);
                nearest.setLatitude(lat);

                int distance = (int) userlocation.distanceTo(nearest);
                double convertm = distance / 1000;
                DecimalFormat twoDForm = new DecimalFormat("#.###");
                convertm =  Double.valueOf(twoDForm.format(convertm));
                int speedIs1KmMinute = 200;
                double estimatedDriveTimeInMinutes = distance / speedIs1KmMinute;
                int legtime = 40;

                int time1 = (int) (estimatedDriveTimeInMinutes / 60);
                int days = time1/24;

                if (estimatedDriveTimeInMinutes <= 1440 && estimatedDriveTimeInMinutes >=60) {

                    textView.setText(time1 + "\t" + "hr" + "(" + convertm + ")" + "\t" + "km");
                }
                    if(estimatedDriveTimeInMinutes < 60){
                    textView.setText(estimatedDriveTimeInMinutes + "\t" + "min" + "(" + convertm + ")" + "\t" + "km");


                }
                    if(estimatedDriveTimeInMinutes>1440){
                        textView.setText(days + "\t" + "daya" + "(" + convertm + ")" + "\t" + "km");

                    }




                // mMap.addMarker(new MarkerOptions().position(userLatLong).title("")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.drawcircle));
                place1 = new MarkerOptions().position(new LatLng(lat, lng)).title(name).icon(BitmapDescriptorFactory.fromBitmap(BitMarker3));

                place2 = new MarkerOptions().position(new LatLng(35.2191, 33.4175)).title("Cyprus International University");

                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + userLatLong.toString() + "&destinations=" + conv + "&mode=driving&language=fr-FR&avoid=tolls&key=" + getString(R.string.google_maps_key);
                new FetchURL(RouteandBus.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");


                if (currLocationMarker != null) {
                    currLocationMarker.remove();


                }
                if (getCurrLocationMarker != null) {
                    getCurrLocationMarker.remove();
                }
                currLocationMarker = mMap.addMarker(place1);

                boolean zoomUpdated = false;



                getCurrLocationMarker = mMap.addMarker(place2);



            }
        };
        askLocation();

    }
    private LatLng setCameraWithCoordinationBounds(LatLng latLng){


            float zoomLevel = 20.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
            return latLng;



    }


    private void drawCircle(LatLng point) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(0.4);

        // Border color of the circle
        circleOptions.strokeColor(android.R.color.black);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(15);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }

    private String getDistanceUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + str_origin + "&destinations=" + str_dest + "&mode=driving&language=fr-FR&avoid=tolls&key=" + getString(R.string.google_maps_key);
        return url;
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {


            }
        } catch (Resources.NotFoundException e) {

        }
        mMap = googleMap;
        LatLngBounds mybound = new LatLngBounds(
                // SW bounds

                new LatLng(35.0322, 32.4184),new LatLng(35.5347, 34.1866)// NE bounds
        );
        mMap.setLatLngBoundsForCameraTarget(mybound);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));


        // Add a marker in Sydney and move the camera


        getLoc(mMap);
        addstops(mMap);
        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot s : snapshot.getChildren()) {


                        DriverInfo users = s.getValue(DriverInfo.class);
                        //function call to fectch data


                        LatLng location = new LatLng(users.lat, users.lng);

                        //adding the fectched data on the map
                        Double lat = getIntent().getExtras().getDouble("latitude");
                        Double lng = getIntent().getExtras().getDouble("longitude");
                        String name = getIntent().getExtras().getString("routename");
                        Marker mine = null;

                        mine = mMap.addMarker(new MarkerOptions().position(location).title(users.vehiclenumber).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker)));
                        mine.getRotation();
                        if (location.equals(new LatLng(35.2191, 33.4175))) {
                            Toast.makeText(getApplicationContext(), "The bus is in the school", Toast.LENGTH_SHORT).show();
                        }
                        if (location.equals(new LatLng(lat, lng))) {
                            Toast.makeText(getApplicationContext(), "The bus is at the last route", Toast.LENGTH_SHORT).show();
                        }
                        if (location.equals(userLatLong)) {
                            Toast.makeText(getApplicationContext(), "The bus has reached your Destination", Toast.LENGTH_SHORT).show();
                        }



                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


    }

    public void askLocation() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                userLatLong = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                //Circle circle = mMap.addCircle(new CircleOptions().center(userLatLong).radius(10000).strokeColor(Color.WHITE).fillColor(Color.RED).strokeWidth(15));


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }

    public void clicked(View view) {

    }
    @Override
    public void onLocationChanged(Location location ) {


        // Getting latitude of the current location
        //adding the fectched data on the map
        Double lat = getIntent().getExtras().getDouble("latitude");
        Double lng = getIntent().getExtras().getDouble("longitude");
        String name = getIntent().getExtras().getString("routename");
        location.setLongitude(lng);
        location.setLatitude(lat);
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
       mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
       mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }


}
