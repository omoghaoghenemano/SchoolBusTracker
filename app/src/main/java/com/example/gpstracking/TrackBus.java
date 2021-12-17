package com.example.gpstracking;

import static com.example.gpstracking.R.layout.activity_stop_direction;
import static com.example.gpstracking.R.layout.activity_track_bus;

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

public class TrackBus extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

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
    private ImageView mylocation;
    private Polyline currentPolyline;


    private BottomNavigationView bottomr;
    private Fragment selectorFragment, myfrag;
    private Group group;
    private SearchView searchView;
    private BottomSheetBehavior bottomsheet;
    private String Names, Emails;
    private NavigationView navigationView;
    private TextView tv_result2;
    private FrameLayout myframe;

    Bitmap BitMapMarker, BitMapMarker1, BitMarker3;
    private Marker carMarker, carMarker1;


    Location myLocation = null;
    private ImageView getPoly;

    Location myUpdatedLocation = null;
    float Bearing = 0;
    boolean AnimationStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(activity_track_bus);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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


        backbutton = findViewById(R.id.backbutton);
        getlocation = findViewById(R.id.getmylocation);
        textView = findViewById(R.id.myduration);
        directionwalk = findViewById(R.id.myduration1);

        myframe = findViewById(R.id.myframe);
        mUser = FirebaseDatabase.getInstance().getReference("Drivers");
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrackBus.this, MapsActivity.class));
            }
        });
        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mapFragment.getMapAsync(this);
    }

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

    public void myLocation() {

        locationManager = (LocationManager) this.getSystemService(android.content.Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (AnimationStatus) {
                    myUpdatedLocation = location;
                }

                myLocation = location;

                userLatLong = new LatLng(location.getLatitude(), location.getLongitude());

                if (carMarker != null) {
                    carMarker.remove();
                }
                carMarker = mMap.addMarker(new MarkerOptions().position(userLatLong).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker1)));


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
                userlocation.setLatitude(location.getLatitude());
                userlocation.setLongitude(location.getLongitude());
                Location nearest = new Location("point B");


                nearest.setLongitude(lng);
                nearest.setLatitude(lat);

                int distance = (int) userlocation.distanceTo(nearest);
               double convertm = distance / 1000;
                DecimalFormat  twoDForm = new DecimalFormat("#.###");
                convertm =  Double.valueOf(twoDForm.format(convertm));
                System.out.println("The distance from proint a TO BE IS" + convertm);
                int speedIs1KmMinute = 200;
                double estimatedDriveTimeInMinutes = distance / speedIs1KmMinute;
                int legtime = 40;
                double estimatedwalktimeinminutes = distance / legtime;
                int time1 = (int) (estimatedDriveTimeInMinutes / 60);
                int days = time1/24;
                int time = (int) (estimatedwalktimeinminutes / 60);
                int dayswalk = time/24;

                if (estimatedwalktimeinminutes <= 1440 && estimatedwalktimeinminutes >=60) {

                    directionwalk.setText(time + "\t" + "hr" + "(" + convertm + ")" + "\t" + "km");

                }
                if(estimatedwalktimeinminutes < 60){
                    directionwalk.setText(estimatedwalktimeinminutes + "\t" + "min" + "(" + convertm + ")" + "\t" + "km");

                }
                if(estimatedwalktimeinminutes > 1440){
                    directionwalk.setText(dayswalk+ "\t" + "days" + "(" + convertm + ")" + "\t" + "km");

                }
                if (estimatedDriveTimeInMinutes <= 1440 && estimatedDriveTimeInMinutes >=60) {


                    textView.setText(time1 + "\t" + "hr" + "(" + convertm + ")" + "\t" + "km");
                } if(estimatedDriveTimeInMinutes < 60){
                    textView.setText(estimatedDriveTimeInMinutes + "\t" + "min" + "(" + convertm + ")" + "\t" + "km");


                }
                if(estimatedDriveTimeInMinutes>1440){
                    textView.setText(days + "\t" + "days" + "(" + convertm + ")" + "\t" + "km");

                }


                // mMap.addMarker(new MarkerOptions().position(userLatLong).title("")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.drawcircle));
                place1 = new MarkerOptions().position(new LatLng(lat, lng)).title("stops").icon(BitmapDescriptorFactory.fromBitmap(BitMarker3));

                place2 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location");

                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + userLatLong.toString() + "&destinations=" + conv + "&mode=driving&language=fr-FR&avoid=tolls&key=" + getString(R.string.google_maps_key);
                new FetchURL(TrackBus.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");


                if (currLocationMarker != null) {
                    currLocationMarker.remove();
                }
                if (getCurrLocationMarker != null) {
                    getCurrLocationMarker.remove();
                }
                currLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).rotation(-90).title(name).anchor((float) 0.5, (float) 0.5).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker)));


                getlocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myLocation();
                        setCameraWithCoordinationBounds(userLatLong);


                    }
                });


            }
        };
        askLocation();

    }

    private LatLng setCameraWithCoordinationBounds(LatLng latLng) {


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

    GoogleMap addstops(GoogleMap googleMap) {
        mMap = googleMap;
        mstops.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                if (snapshots.exists()) {
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
        return googleMap;


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


        // Add a marker in Sydney and move the camera

        LatLngBounds mybound = new LatLngBounds(
                // SW bounds

                new LatLng(35.0322, 32.4184), new LatLng(35.5347, 34.1866)// NE bounds
        );
        mMap.setLatLngBoundsForCameraTarget(mybound);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));


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

                        mMap.addMarker(new MarkerOptions().position(new LatLng(users.lat, users.lng)).rotation(-90).title(users.vehiclenumber).anchor((float) 0.5, (float) 0.5).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker)));




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
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


}
