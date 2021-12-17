package Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gpstracking.Adapter.ListAdapter;
import com.example.gpstracking.DriverInfo;
import com.example.gpstracking.MapsActivity;
import com.example.gpstracking.Model.Routes.Route;

import com.example.gpstracking.R;
import com.example.gpstracking.RouteandBus;
import com.example.gpstracking.TrackBus;
import com.example.gpstracking.databinding.ActivityMapsBinding;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.acl.Group;
import java.util.ArrayList;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gpstracking.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.ChildEventListener;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NearbyBuses extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap mMap;
    private DatabaseReference mUser;
    public ActivityMapsBinding binding;


    private BottomSheetBehavior bottomSheetBehavior;
    private ChildEventListener mChildEventListener;
    Marker marker;
    GestureDetector gestureDetector;

    LocationManager locationManager;
    LocationListener locationListener;

    LatLng userLatLong, lats;
    DatabaseReference reference;
    FusedLocationProviderClient client;

    private BottomNavigationView bottomr;
    private Fragment selectorFragment;
    private Group group;
    private SearchView searchView;
    private  BottomSheetBehavior bottomsheet;
    private String Names, Emails;
    RecyclerView recyclerView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteFragment newInstance(String param1, String param2) {
        RouteFragment fragment = new RouteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NearbyBuses() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclecontent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reference = FirebaseDatabase.getInstance().getReference("Drivers");
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;



    }

    @Override
    public void onStart() {
        super.onStart();


        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();

                    if (location != null) {
                        Double lat = location.getLatitude();
                        Double lng = location.getLongitude();
                        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Route>().setQuery(reference, Route.class).build();
                        FirebaseRecyclerAdapter<Route, Nearestbus.RouteHolder> adapter = new FirebaseRecyclerAdapter<Route, Nearestbus.RouteHolder>(options) {
                            @NonNull
                            @Override
                            public Nearestbus.RouteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false
                                );
                                Nearestbus.RouteHolder routeHolder = new Nearestbus.RouteHolder(views);
                                return  routeHolder;
                            }



                            @Override
                            protected void onBindViewHolder(@NonNull Nearestbus.RouteHolder holder, int position, @NonNull Route model) {
                                String name1 = getRef(position).getKey();



                                reference.child(name1).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("vehiclenumber")) {

                                            Location userlocation = new Location("point A");
                                            userlocation.setLatitude(location.getLatitude());
                                            userlocation.setLongitude(location.getLongitude());
                                            Location nearest = new Location("point B");


                                            nearest.setLongitude(Double.parseDouble(snapshot.child("lng").getValue().toString()));
                                            nearest.setLatitude(Double.parseDouble(snapshot.child("lat").getValue().toString()));
                                            String name = "Bus"+ "\t"+ snapshot.child("vehiclenumber").getValue().toString();
                                            Double lats = Double.parseDouble(snapshot.child("lat").getValue().toString());
                                            Double lng = Double.parseDouble(snapshot.child("lng").getValue().toString());

                                            int distance = (int) userlocation.distanceTo(nearest);


                                            if (distance < 10000) {
                                                holder.placename.setText(name);
                                                holder.location.setImageResource(R.drawable.routeicon );





                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getContext(), TrackBus.class);
                                                        intent.putExtra("latitude", lats);
                                                        intent.putExtra("longitude", lng);
                                                        intent.putExtra("routename", name);

                                                       startActivity(intent);

                                                    }
                                                });


                                            }

                                            else {
                                                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                                params.height = 0;
                                                params.width = 0;



                                                holder.placename.setVisibility(View.GONE);

                                                holder.location.setVisibility(View.GONE);
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(params);





                                                // repeat this for each childView inside your ViewHolder

                                            }







                                        }
                                    }



                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        } ;



                        recyclerView.setAdapter(adapter);

                        adapter.startListening();



                    } else {
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(String key, GeoLocation location) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                    }

                }
            });

        }



    }
    public static class RouteHolder extends RecyclerView.ViewHolder{
        public ImageView location;
        TextView placename;
        public RouteHolder(@NonNull View itemView) {
            super(itemView);
            placename = itemView.findViewById(R.id.place);
            location = itemView.findViewById(R.id.routess);
        }

    }


}
