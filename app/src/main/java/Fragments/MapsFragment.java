package Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gpstracking.MapsActivity;
import com.example.gpstracking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.GestureDetector;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;

import com.example.gpstracking.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.acl.Group;
import java.util.List;

public class MapsFragment extends Fragment implements LocationListener {
    private GoogleMap mMap;
    private DatabaseReference mUser;
    public ActivityMapsBinding binding;


    private BottomSheetBehavior bottomSheetBehavior;
    private ChildEventListener mChildEventListener;
    Marker marker;
    GestureDetector gestureDetector;
    private Bitmap BitMapMarker1;

    LocationManager locationManager;
    LocationListener locationListener;

    LatLng userLatLong, lats;
    DatabaseReference reference;
    private ImageView zoomin;

    private BottomNavigationView bottomr;
    private Fragment selectorFragment,myfrag;
    private Group group;
    private SearchView searchView;
    private  BottomSheetBehavior bottomsheet;
    private String Names, Emails;
    private NavigationView navigationView;


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
             try{
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                 boolean success = googleMap.setMapStyle(
                         MapStyleOptions.loadRawResourceStyle(
                                 getContext(), R.raw.style_json));

                 if (!success) {


                }
            } catch (Resources.NotFoundException e) {


            }
            mMap = googleMap;
            googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) getActivity());

            locationManager = (LocationManager) getActivity().getSystemService(android.content.Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    userLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();


                    mMap.addMarker(new MarkerOptions().position(userLatLong).title("My location").icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker1)));
                    mMap.setMaxZoomPreference(100);

                    LatLngBounds mybound = new LatLngBounds(
                            // SW bounds

                            new LatLng(userLatLong.latitude, userLatLong.longitude),new LatLng(35.5347, 34.1866)// NE bounds
                    );
                    mMap.setLatLngBoundsForCameraTarget(mybound);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    // Creates a CameraPosition from the builder









                    zoomin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userLatLong )      // Sets the center of the map to Mountain View
                .zoom(12+1)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



    }
});


                }



                };
            askLocation();
            }


    };
    public void askLocation() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

            }
        }).check();
    }
    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(20);

        // Border color of the circle
        circleOptions.strokeColor(android.R.color.black);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }


            @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_maps, container, false);
        zoomin = view.findViewById(R.id.zoomin);
                BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.posimarker);
                Bitmap a = bitmapdraw1.getBitmap();

                BitMapMarker1 = Bitmap.createScaledBitmap(a, 100, 100, false);


                return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}