package com.flatmates.ixion.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.flatmates.ixion.R;
import com.flatmates.ixion.model.Data;
import com.flatmates.ixion.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.flatmates.ixion.utils.Constants.IS_USER_LOGGED_IN;
import static com.flatmates.ixion.utils.Constants.KEY_ADDRESS;
import static com.flatmates.ixion.utils.Constants.KEY_AREA;
import static com.flatmates.ixion.utils.Constants.KEY_BEDROOMS;
import static com.flatmates.ixion.utils.Constants.KEY_BUNDLE;
import static com.flatmates.ixion.utils.Constants.KEY_CITY;
import static com.flatmates.ixion.utils.Constants.KEY_EMAIL;
import static com.flatmates.ixion.utils.Constants.KEY_MOBILE;
import static com.flatmates.ixion.utils.Constants.KEY_NAME;
import static com.flatmates.ixion.utils.Constants.KEY_RENT;
import static com.flatmates.ixion.utils.Constants.KEY_STATE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String area;
    String bhk;
    String city;
    String state;
    String rent;
    double lat;
    double lon;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefData = database.getReference("Data");

    private static final String TAG = MapsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras().getBundle(Constants.KEY_BUNDLE);
        try {
            area = bundle.getString(KEY_AREA).toLowerCase();
            bhk = bundle.getString(KEY_BEDROOMS);
            city = bundle.getString(KEY_CITY).toLowerCase();
            state = bundle.getString(KEY_STATE).toLowerCase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        fetchData();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void showMarker(Double lati, Double loni) {
        LatLng apna = new LatLng(lati, loni);
        mMap.addMarker(new MarkerOptions().position(apna).title(bhk + "   " + area + "  " + city + "  " + state));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(apna, 10.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                double lon = marker.getPosition().longitude;
                Query recentPostsQuery = myRefData.orderByChild("lon").equalTo(String.valueOf(lon));
                recentPostsQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Data data = snapshot.getValue(Data.class);

                            String area = data.getArea();
                            String bhk = data.getBhk();
                            String rent = data.getRent();
                            String city = data.getCity();
                            String state = data.getState();
                            String name = data.getName();
                            String email = data.getEmail();
                            String mobile = data.getMobile();
                            String address = data.getAddress();

                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_AREA, area);
                            bundle.putString(KEY_BEDROOMS, bhk);
                            bundle.putString(KEY_CITY, city);
                            bundle.putString(KEY_STATE, state);
                            bundle.putString(KEY_NAME, name);
                            bundle.putString(KEY_ADDRESS, address);
                            bundle.putString(KEY_MOBILE, mobile);
                            bundle.putString(KEY_EMAIL, email);
                            bundle.putString(KEY_RENT, rent);
                            Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);
                            intent.putExtra(KEY_BUNDLE, bundle);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return false;
            }
        });
    }

    private void fetchData() {
        Log.i(TAG, "onDataChange: " + area + " " + city + " " + state);
        myRefData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data data = snapshot.getValue(Data.class);
                    String map_area = data.getArea();
                    String map_city = data.getCity();
                    String map_state = data.getState();
                    String map_rent = data.getRent();
                    String map_bhk = data.getBhk();
                    lat = Double.parseDouble(data.getLat());
                    lon = Double.parseDouble(data.getLon());
                    System.out.println(map_city);
                    if (area.equals(map_area) || city.equals(map_city) || state.equals(map_state)) {
                        if (bhk.equals("null"))
                            showMarker(lat, lon);
                        else {
                            if (bhk.equals(map_bhk))
                                showMarker(lat, lon);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this).edit();
        editor.clear();
        editor.putBoolean(IS_USER_LOGGED_IN, true);
        editor.apply();
    }
}
