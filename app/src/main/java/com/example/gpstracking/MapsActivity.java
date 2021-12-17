package com.example.gpstracking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Interpolator;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.animation.AccelerateDecelerateInterpolator;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.gpstracking.databinding.ActivityMapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.acl.Group;

import Fragments.BusMenu;
import Fragments.HomeFragment;
import Fragments.MapsFragment;
import Fragments.RoutesMenu;
import Fragments.StopMenu;
import Fragments.WebViewFragment;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap, mmap1;
    private DatabaseReference mUser, mroutes, mstops;
    public ActivityMapsBinding binding;
    private ImageView movingbus;
    private ImageView mylocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Polyline polyline;
    private Marker mLastKnownLocationMarker;

    private BottomSheetBehavior bottomSheetBehavior;
    private ChildEventListener mChildEventListener;
    Marker marker, stopmarker;
    GestureDetector gestureDetector;
    static Marker carMarker, carMarker1, carMarker2;
    LocationManager locationManager;
    LocationListener locationListener;
    TabLayout tabLayout;
    private Geocoder geocoder;
    ViewPager viewPager;


    LatLng userLatLong, lats;
    boolean locationPermission = false;
    Location myLocation = null;
    Location myUpdatedLocation = null;
    float Bearing = 0;
    boolean AnimationStatus = false;

    Bitmap BitMapMarker, BitMapMarker1, BitMarker3;

    private final static int LOCATION_REQUEST_CODE = 23;

    private ConnectivityManager connectivityManager;
    DatabaseReference reference;
    RecyclerView recyclerView;
    private BottomNavigationView bottomr;
    private Fragment selectorFragment;
    private Fragment myfrag;
    private Group group;
    private SearchView searchView;
    private BottomSheetBehavior bottomsheet;
    private String Names, Emails;
    private NavigationView navigationView;


    public MapsActivity() {


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*/LinearLayout linearLayout = findViewById(R.id.design_bottom_sheet);
        bottomsheet = BottomSheetBehavior.from(linearLayout);
        bottomsheet.setFitToContents(true);
        bottomsheet.setPeekHeight(120);

/*/
        movingbus = findViewById(R.id.getmymovinglocation);
        mylocation = findViewById(R.id.getmylocation);

        BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.posimarker);
        Bitmap a = bitmapdraw1.getBitmap();

        BitMapMarker1 = Bitmap.createScaledBitmap(a, 100, 100, false);


        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bustruck);
        Bitmap b = bitmapdraw.getBitmap();
        BitMapMarker = Bitmap.createScaledBitmap(b, 160, 100, false);

        ChildEventListener mChildEventListener;
        //adding the branches from the database
        BitmapDrawable bitmapdraw3 = (BitmapDrawable) getResources().getDrawable(R.drawable.pickups);
        Bitmap c = bitmapdraw3.getBitmap();

        BitMarker3 = Bitmap.createScaledBitmap(c, 150, 100, false);


        mstops = FirebaseDatabase.getInstance().getReference("Stops");
        mstops.push().setValue(stopmarker);

        mUser = FirebaseDatabase.getInstance().getReference("Drivers");

        mUser.push().setValue(marker);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        navigationView = findViewById(R.id.directionsnav);
        bottomr = findViewById(R.id.bottom_navigation);
        geocoder = new Geocoder(this);
        // searchView = findViewById(R.id.Sv_location);
        Intent intent = this.getIntent();
        Places.initialize(getApplicationContext(), "AIzaSyCeXaH70kNxwqHkErhubPpqP2X6wrhW6TE");


        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLocation();
                askLocation();
                boolean zoomUpdated = false;
                if (!zoomUpdated) {
                    float zoomLevel = 20.0f; //This goes up to 21
                    if (userLatLong != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLong));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                        zoomUpdated = true;
                    }

                }


            }
        });
        movingbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mMap.clear();
                            if (stopmarker != null) {
                                stopmarker.remove();
                            }

                            for (DataSnapshot s : snapshot.getChildren()) {


                                DriverInfo users = s.getValue(DriverInfo.class);


                                stopmarker = mMap.addMarker(new MarkerOptions().position(new LatLng(users.lat, users.lng)).rotation(-90).title(users.vehiclenumber).anchor((float) 0.5, (float) 0.5).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker)));


                                LatLng h = new LatLng(users.lat, users.lng);
                                setCameraWithCoordinationBounds(h);


                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "could not find this location", Toast.LENGTH_SHORT).show();
                    }
                    Address address = addressList.get(0);
                    mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                        @Override
                        public void onCameraMove() {


                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());


//                            mMap.addMarker(new MarkerOptions().position(latLng).title(location));





                        }
                    });



                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

         //retreiving the data from the database


        /*
        refer.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {






            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());


            }
        });
        /
         */


        mapFragment.getMapAsync(this);
        navigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomsheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        bottomSheet.setVisibility(newState);
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem items) {
                switch (items.getItemId()) {
                    case R.id.location:
                        myfrag = new MapsFragment();
                        break;
                    case R.id.schedule:
                        myfrag = new WebViewFragment();


                        break;

                }
                if (myfrag != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myfrag).commit();
                }

                return true;
            }
        });


        bottomr.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetworkInfo != null &&
                        activeNetworkInfo.isConnectedOrConnecting();
                if (isConnected) {

                } else {

                    Intent intent1 = new Intent(MapsActivity.this, NoInternet.class);
                    startActivity(intent1);
                }


                switch (item.getItemId()) {
                    case R.id.homemenu:
                        selectorFragment = new HomeFragment();
                        break;
                    case R.id.road:
                        selectorFragment = new RoutesMenu();
                        break;
                    case R.id.mores:
                        mores h = new mores();
                        h.show(getSupportFragmentManager(), "example");
                        break;
                    case R.id.location:
                        selectorFragment = new StopMenu();
                        break;
                    case R.id.trackbus:
                        selectorFragment = new BusMenu();


                }


                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
                return true;
            }

        });


    }

    private LatLng setCameraWithCoordinationBounds(LatLng latLng) {


        float zoomLevel = 7; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        return latLng;


    }

    void displayuser(GoogleMap googleMap) {

        mMap = googleMap;


        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists()) {


                    for (DataSnapshot s : snapshot.getChildren()) {


                        DriverInfo users = s.getValue(DriverInfo.class);


                        mMap.addMarker(new MarkerOptions().position(new LatLng(users.lat, users.lng)).rotation(-90).title(users.vehiclenumber).anchor((float) 0.5, (float) 0.5).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker)));


                        LatLng h = new LatLng(users.lat, users.lng);


                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


    }

    void addstops(GoogleMap googleMap) {
        mmap1 = googleMap;
        mstops.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {

                for (DataSnapshot b : snapshots.getChildren()) {

                    GetRoute getRoute = b.getValue(GetRoute.class);
                    LatLng loc = new LatLng(getRoute.lat, getRoute.lng);


                    carMarker1 = mmap1.addMarker(new MarkerOptions().position(loc).title(getRoute.routeName).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMarker3)));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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


                if (t < 1) {

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
        mmap1 = googleMap;

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);

        mMap.getUiSettings().isTiltGesturesEnabled();
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLngBounds mybound = new LatLngBounds(
                // SW bounds

                new LatLng(35.0322, 32.4184), new LatLng(35.5347, 34.1866)// NE bounds
        );
        mMap.setLatLngBoundsForCameraTarget(mybound);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        addstops(mmap1);
        displayuser(mMap);








        /*
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {




                userLatLong = new LatLng(location.getLatitude(), location.getLongitude());



               // Circle circle = mMap.addCircle(new CircleOptions().center(userLatLong).radius(10000).strokeColor(Color.WHITE).fillColor(Color.RED).strokeWidth(15));
               // mMap.addMarker(new MarkerOptions().position(userLatLong).title("locstion"));




            }
        };
        addingBus();
        askLocation();


/*/


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

    public void getLastLocation() {


    }

    //askng user to enter location
    public void askLocation() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationManager.removeUpdates(this);


    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return true;


    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location:
                myfrag = new MapsFragment();
                break;


            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        if (myfrag != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myfrag).commit();
        }

        return true;

    }


}