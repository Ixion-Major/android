package com.flatmates.ixion.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.flatmates.ixion.R;
import com.flatmates.ixion.model.Data;
import com.flatmates.ixion.model.UserMessage;
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
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.flatmates.ixion.utils.Constants.IS_USER_ORDER_COMPLETE;
import static com.flatmates.ixion.utils.Constants.KEY_AREA;
import static com.flatmates.ixion.utils.Constants.KEY_BEDROOMS;
import static com.flatmates.ixion.utils.Constants.KEY_CITY;
import static com.flatmates.ixion.utils.Constants.KEY_STATE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String area;
    String bedrooms;
    String city;
    String state;
    double lat;
    double lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras().getBundle(Constants.KEY_BUNDLE);
        area = bundle.getString(KEY_AREA);
        bedrooms = bundle.getString(KEY_BEDROOMS);
        city = bundle.getString(KEY_CITY);
        state = bundle.getString(KEY_STATE);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (state.toLowerCase().matches("delhi") || city.toLowerCase().matches("delhi")) {
            lat = 28.648669;
            lon = 77.061945;
            LatLng sydney = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(sydney).title(lat + "  " + lon));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10.0f));

            lat = 28.778669;
            lon = 77.111945;
            LatLng sydney2 = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(sydney2).title(lat + "  " + lon));

            lat = 28.578669;
            lon = 76.921945;
            LatLng sydney3 = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(sydney3).title(lat + "  " + lon));

            lat = 28.818669;
            lon = 77.1601945;
            LatLng sydney4 = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(sydney4).title(lat + "  " + lon));

            lat = 28.775362;
            lon = 77.041945;
            LatLng sydney5 = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(sydney5).title(lat + "  " + lon));

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }
}
