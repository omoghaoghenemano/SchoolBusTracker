package Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gpstracking.Model.Routes.Route;
import com.example.gpstracking.Model.findingdist;
import com.example.gpstracking.R;
import com.example.gpstracking.RouteandBus;
import com.example.gpstracking.StopDirection;
import com.example.gpstracking.databinding.ActivityMapsBinding;
import com.firebase.geofire.GeoFire;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Nearestbus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Nearestbus extends Fragment implements Comparator<int[]> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;

    private GoogleMap mMap;
    private DatabaseReference mUser;
    public ActivityMapsBinding binding;
    GeoLocation geoLocation;


    private final boolean busFound = false;
    private final String busDriverKey = "";
    private BottomSheetBehavior bottomSheetBehavior;
    private ChildEventListener mChildEventListener;
    Marker marker;
    GestureDetector gestureDetector;

    LocationManager locationManager;
    LocationListener locationListener;
    private LinearLayout layout;
    Location mylocation;
    public static final LatLng mylocations = null;

    LatLng userLatLong, lats;
    DatabaseReference reference, reference1;

    private final List<String> businessIds = new ArrayList<>();

    private DatabaseReference refBase = null;
    private final DatabaseReference refLocation = null;
    private DatabaseReference refUser = null;
    private BottomNavigationView bottomr;
    private Fragment selectorFragment;
    private ImageView imageView;
    private Group group;

    private SearchView searchView;
    private BottomSheetBehavior bottomsheet;
    DatabaseReference nearestBus;
    private ListView listView;
    private String Names, Emails;
    GeoFire geoFire;
    FusedLocationProviderClient client;
    private LinearLayout.LayoutParams params;


    public Nearestbus() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Nearestbus.
     */
    // TODO: Rename and change types and number of parameters
    public static Nearestbus newInstance(String param1, String param2) {
        Nearestbus fragment = new Nearestbus();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        View view = inflater.inflate(R.layout.fragment_nearestbus, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclecontent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        reference = FirebaseDatabase.getInstance().getReference("Stops");
        nearestBus = FirebaseDatabase.getInstance().getReference().child("Stops");
        layout = view.findViewById(R.id.nearbuslayout);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        client = LocationServices.getFusedLocationProviderClient(getActivity());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Stops");
        GeoFire geoFire = new GeoFire(ref);
        getCurrentLocation();
        this.setReferences();
        listView = (ListView) view.findViewById(R.id.list_item);

        // Inflate the layout for this fragment
        imageView = view.findViewById(R.id.routess);
        return view;
    }

    private void setReferences() {
        this.refBase = FirebaseDatabase.getInstance().getReference();
        this.refUser = refBase.child("Routes");

        this.geoFire = new GeoFire(this.refUser);
    }

    private void searchNearby(double latitude, double longitude, double radius) {
        this.searchNearby(new GeoLocation(latitude, longitude), radius);
    }

    private void searchNearby(GeoLocation location, double radius) {

        GeoQuery geoQuery = this.geoFire.queryAtLocation(location, radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                String loc = location.latitude + ", " + location.longitude;
                Log.d("LOG_TAG", "onKeyEntered: " + key + " @ " + loc);

                /* once the key is known, one can lookup the associated record */
                refUser.child(key).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("LOG_TAG", "onDataChange: " + dataSnapshot.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError firebaseError) {
                        Log.e("LOG_TAG", "onCancelled: " + firebaseError.getMessage());
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                Log.d("LOG_TAG", "onKeyExited: " + key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("LOG_TAG", "onKeyMoved: " + key);
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("LOG_TAG", "onGeoQueryReady");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("LOG_TAG", "onGeoQueryError" + error.getMessage());
            }
        });
    }

    public void getCurrentLocation() {
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
                        findingdist dist = new findingdist();
                        dist.setLatitude(location.getLatitude());
                        dist.setLongitude(location.getLongitude());


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
                                View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false
                                );
                                Nearestbus.RouteHolder routeHolder = new Nearestbus.RouteHolder(views);
                                return routeHolder;
                            }


                            @Override
                            protected void onBindViewHolder(@NonNull Nearestbus.RouteHolder holder, int position, @NonNull Route model) {
                                String name1 = getRef(position).getKey();


                                reference.child(name1).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("routeName")) {

                                            Location userlocation = new Location("point A");
                                            userlocation.setLatitude(location.getLatitude());
                                            userlocation.setLongitude(location.getLongitude());
                                            Location nearest = new Location("point B");


                                            nearest.setLongitude(Double.parseDouble(snapshot.child("lng").getValue().toString()));
                                            nearest.setLatitude(Double.parseDouble(snapshot.child("lat").getValue().toString()));

                                            String name = snapshot.child("routeName").getValue().toString();
                                            Double lats = Double.parseDouble(snapshot.child("lat").getValue().toString());
                                            Double lng = Double.parseDouble(snapshot.child("lng").getValue().toString());

                                            int distance = (int) userlocation.distanceTo(nearest);
                                            try {


                                                if (distance < 1300) {

                                                    holder.placename.setText(name);
                                                    holder.location.setImageResource(R.drawable.routeicon);


                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent(getContext(), StopDirection.class);
                                                            intent.putExtra("latitude", lats);
                                                            intent.putExtra("longitude", lng);
                                                            intent.putExtra("routename", name);
                                                            startActivity(intent);


                                                        }
                                                    });


                                                } else {
                                                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                                    params.height = 0;
                                                    params.width = 0;


                                                    holder.placename.setVisibility(View.GONE);

                                                    holder.location.setVisibility(View.GONE);
                                                    holder.itemView.setVisibility(View.GONE);
                                                    holder.itemView.setLayoutParams(params);


                                                    // repeat this for each childView inside your ViewHolder

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }


                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        };


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

    @Override
    public int compare(int[] ints, int[] t1) {
        return 0;
    }

    public int getItemCount() {
        return 0;
    }

    public static class RouteHolder extends RecyclerView.ViewHolder {
        TextView placename;
        ImageView location;

        public RouteHolder(@NonNull View itemView) {
            super(itemView);
            placename = itemView.findViewById(R.id.place);
            location = itemView.findViewById(R.id.routess);
        }

    }

}

