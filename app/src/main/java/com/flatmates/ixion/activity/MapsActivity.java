package com.flatmates.ixion.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.flatmates.ixion.utils.Constants.IS_USER_LOGGED_IN;
import static com.flatmates.ixion.utils.Constants.IS_USER_ORDER_COMPLETE;
import static com.flatmates.ixion.utils.Constants.KEY_ADDRESS;
import static com.flatmates.ixion.utils.Constants.KEY_AREA;
import static com.flatmates.ixion.utils.Constants.KEY_BEDROOMS;
import static com.flatmates.ixion.utils.Constants.KEY_BUDGET;
import static com.flatmates.ixion.utils.Constants.KEY_BUNDLE;
import static com.flatmates.ixion.utils.Constants.KEY_CITY;
import static com.flatmates.ixion.utils.Constants.KEY_EMAIL;
import static com.flatmates.ixion.utils.Constants.KEY_IMAGE;
import static com.flatmates.ixion.utils.Constants.KEY_MOBILE;
import static com.flatmates.ixion.utils.Constants.KEY_NAME;
import static com.flatmates.ixion.utils.Constants.KEY_RENT;
import static com.flatmates.ixion.utils.Constants.KEY_STATE;
import static com.flatmates.ixion.utils.Constants.USER_EMAIL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String area = "";
    String bhk = "";
    String city = "";
    String state = "";
    String rent = "";
    String name = "";
    String budget = "";
    int minBudget;
    int maxBudget;
    double lat;
    double lon;
    boolean dataFound;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefData = database.getReference("Data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //TODO: crashes here when you go to VR/userChat activity and come back -> Bundle is null
        Bundle bundle = getIntent().getExtras().getBundle(Constants.KEY_BUNDLE);
        if (bundle.getString(KEY_AREA) != null)
            area = bundle.getString(KEY_AREA).toLowerCase();
        if (bundle.getString(KEY_BEDROOMS) != null) {
            if (bundle.getString(KEY_BEDROOMS).length() > 0) {  //TODO: crash due to 0 length in case of null -> was fixed
                bhk = bundle.getString(KEY_BEDROOMS);
                bhk = bhk.substring(0, 1);
            }
        }
        if (bundle.getString(KEY_CITY) != null)
            city = bundle.getString(KEY_CITY).toLowerCase();
        if (bundle.getString(KEY_STATE) != null)
            state = bundle.getString(KEY_STATE).toLowerCase();
        if (bundle.getString(KEY_BUDGET) != null && bundle.getString(KEY_BUDGET).length() > 3) {
            budget = bundle.getString(KEY_BUDGET);
            if (budget.contains("-")) {
                String[] parts = budget.split("-");
                minBudget = Integer.parseInt(parts[0]);
                maxBudget = Integer.parseInt(parts[1]);
            } else {
                int bud = Integer.parseInt(budget);
                minBudget = bud - 2000;
                maxBudget = bud + 2000;
            }
        }

        dataFound = false;

        if (!city.equals("") && budget.equals("") && bhk.equals(""))
            fetchData(city);
        else if (!area.equals("") && budget.equals("") && bhk.equals(""))
            fetchData(area);
        else if (!state.equals("") && budget.equals("") && bhk.equals(""))
            fetchData(state);
        else if (!budget.equals("") && !area.equals(""))
            fetchBUDAREA(minBudget, maxBudget, area);
        else if (!budget.equals("") && !city.equals(""))
            fetchBUDAREA(minBudget, maxBudget, city);
        else if (!budget.equals("") && !state.equals(""))
            fetchBUDAREA(minBudget, maxBudget, state);
        else if (!budget.equals("") && city.equals(""))
            fetchBudget(minBudget, maxBudget);
        else if (!area.equals("") && !bhk.equals(""))
            fetchAB(area, bhk);
        else if (!city.equals("") && !bhk.equals(""))
            fetchAB(city, bhk);
        else if (!state.equals("") && !bhk.equals(""))
            fetchAB(state, bhk);
        else if (!bhk.equals(""))
            fetchBhk(bhk);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

                            final String area = data.getArea();
                            final String bhk = data.getBhk();
                            final String rent = data.getRent();
                            final String city = data.getCity();
                            final String state = data.getState();
                            final String name = data.getName();
                            final String email = data.getEmail();
                            final String mobile = data.getMobile();
                            final String address = data.getAddress();
                            final String image = data.getPurl();

                            new MaterialDialog.Builder(MapsActivity.this)
                                    .title(name)
                                    .content("Rent: " + rent + "\nNo. of Rooms: "+ bhk+"\nAddress: " + address)
                                    .positiveText("SHOW MORE")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                                            bundle.putString(KEY_IMAGE, image);
                                            Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);
                                            intent.putExtra(KEY_BUNDLE, bundle);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        String email = preferences.getString(USER_EMAIL, "");
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString(USER_EMAIL, email);
        editor.putBoolean(IS_USER_LOGGED_IN, true);
        editor.apply();
    }

    private void showMarker(String name, Double lati, Double loni) {
        LatLng apna = new LatLng(lati, loni);
        mMap.addMarker(new MarkerOptions().position(apna).title(name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(apna, 10.0f));

    }

    private void fetchData(final String match) {

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
                    name = data.getName();
                    lat = Double.parseDouble(data.getLat());
                    lon = Double.parseDouble(data.getLon());
                    if (match.equals(map_area) || match.equals(map_city) || match.equals(map_state)) {
                        showMarker(name, lat, lon);
                        dataFound = true;
                    }
                }
                if (!dataFound) {
                    new MaterialDialog.Builder(MapsActivity.this)
                            .title("Oops!")
                            .content("No result found")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    onBackPressed();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });
    }

    private void fetchBhk(final String match) {

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
                    map_bhk = map_bhk.substring(0, 1);
                    name = data.getName();
                    lat = Double.parseDouble(data.getLat());
                    lon = Double.parseDouble(data.getLon());
                    if (match.equals(map_bhk)) {
                        showMarker(name, lat, lon);
                        dataFound = true;
                    }
                }
                if (!dataFound) {
                    new MaterialDialog.Builder(MapsActivity.this)
                            .positiveText("OK")
                            .title("Oops!")
                            .content("No result found")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    onBackPressed();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });
    }

    private void fetchAB(final String match, final String m_bhk) {

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
                    map_bhk = map_bhk.substring(0, 1);
                    name = data.getName();
                    lat = Double.parseDouble(data.getLat());
                    lon = Double.parseDouble(data.getLon());
                    if (match.equals(map_area) || match.equals(map_city) || match.equals(map_state)) {
                        if (m_bhk.equals(map_bhk)) {
                            showMarker(name, lat, lon);
                            dataFound = true;
                        }
                    }
                }
                if (!dataFound) {
                    new MaterialDialog.Builder(MapsActivity.this)
                            .title("Oops!")
                            .positiveText("OK")
                            .content("No result found")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    onBackPressed();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });

    }

    private void fetchBudget(final int min, final int max) {

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
                    int mapRent = Integer.parseInt(map_rent);
                    name = data.getName();
                    lat = Double.parseDouble(data.getLat());
                    lon = Double.parseDouble(data.getLon());
                    if (mapRent >= min && mapRent <= max) {
                        showMarker(name, lat, lon);
                        dataFound = true;
                    }
                }
                if (!dataFound) {
                    new MaterialDialog.Builder(MapsActivity.this)
                            .title("Oops!")
                            .positiveText("OK")
                            .content("No result found")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    onBackPressed();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });

    }

    private void fetchBUDAREA(final int min, final int max, final String match) {

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
                    name = data.getName();
                    int mapRent = Integer.parseInt(map_rent);
                    lat = Double.parseDouble(data.getLat());
                    lon = Double.parseDouble(data.getLon());
                    if (match.equals(map_area) || match.equals(map_city) || match.equals(map_state)) {
                        if (mapRent >= min && mapRent <= max) {
                            showMarker(name, lat, lon);
                            dataFound = true;
                        }
                    }
                    if (!dataFound) {
                        new MaterialDialog.Builder(MapsActivity.this)
                                .title("Oops!")
                                .positiveText("OK")
                                .content("No result found")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        onBackPressed();
                                    }
                                })
                                .show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });

    }
}