package com.flatmates.ixion.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.flatmates.ixion.InitApplication;
import com.flatmates.ixion.R;
import com.flatmates.ixion.model.BlockchainData;
import com.flatmates.ixion.model.BlockchainTable;
import com.flatmates.ixion.model.Data;
import com.flatmates.ixion.model.UserMessage;
import com.flatmates.ixion.utils.Constants;
import com.flatmates.ixion.utils.Endpoints;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.fab_openbazaar_search)
    FloatingActionButton fabOBSearch;

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

    private static final String TAG = MapsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //TODO: crashes here when you go to VR/userChat activity and come back -> Bundle is null
        Bundle bundle = getIntent().getExtras().getBundle(Constants.KEY_BUNDLE);
        if (bundle != null) {
            try {
                if (bundle.getString(KEY_AREA) != null)
                    area = bundle.getString(KEY_AREA).toLowerCase();
                if (bundle.getString(KEY_BEDROOMS) != null) {
                    if (bundle.getString(KEY_BEDROOMS).length() > 0) {
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
            } catch (Exception e) {
                Log.e(TAG, "onCreate: ", e);
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
                                    .content("Rent: " + rent + "\nNo. of Rooms: " + bhk + "\nAddress: " + address)
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


    @OnClick(R.id.fab_openbazaar_search)
    public void openSearchBazaarActivity() {
        final MaterialDialog dialog = new MaterialDialog.Builder(MapsActivity.this)
                .title("Fetching Data")
                .content("Loading latest listings from peers across the world")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();

        final Intent intent = new Intent(MapsActivity.this, BazaarSearchActivity.class);
        //TODO: send bundle??
        StringRequest request = new StringRequest(Request.Method.POST,
                Endpoints.endpointOBSearch(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: " + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray array = jsonResponse.getJSONArray("hits");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String objectID = object.getString("objectID");
                                Log.i(TAG, "onResponse: objectID: " + objectID);

                                String contractID = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getString("contract_id");
                                Log.i(TAG, "onResponse: contract_id: " + contractID);

                                String GUID = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("id")
                                        .getString("guid");
                                Log.i(TAG, "onResponse: guid: " + GUID);

                                String title = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("item")
                                        .getString("title");
                                Log.i(TAG, "onResponse: title: " + title);

                                String description = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("item")
                                        .getString("description");
                                Log.i(TAG, "onResponse: desc: " + description);

                                String price = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("item")
                                        .getJSONObject("price_per_unit")
                                        .getJSONObject("fiat").getString("price");
                                String currency = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("item")
                                        .getJSONObject("price_per_unit")
                                        .getJSONObject("fiat").getString("currency_code");
                                Log.i(TAG, "onResponse: price: " + price + " " + currency);

                                String imageHash = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("item")
                                        .getJSONArray("image_hashes").getString(0);
                                Log.i(TAG, "onResponse: imageHash: " + imageHash);

                                String vendorName = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("vendor")
                                        .getString("name");
                                Log.i(TAG, "onResponse: vendor name: " + vendorName);

                                String vendorHeaderHash = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("vendor")
                                        .getString("header_hash");
                                Log.i(TAG, "onResponse: vendor header hash: " + vendorHeaderHash);
                                String vendorLocation = object.getJSONObject("vendor_offer")
                                        .getJSONObject("listing")
                                        .getJSONObject("vendor")
                                        .getString("location");
                                Log.i(TAG, "onResponse: vendor location: " + vendorLocation);

                                BlockchainData data = new BlockchainData();
                                data.setGUID(GUID);
                                data.setContractID(contractID);
                                data.setDescription(description);
                                data.setImageHash(imageHash);
                                data.setObjectID(objectID);
                                data.setPrice(price);
                                data.setTitle(title);
                                data.setVendorHeaderHash(vendorHeaderHash);
                                data.setVendorLocation(vendorLocation);
                                data.setVendorName(vendorName);
                                data.setCurrency(currency);

                                try {
                                    getContentResolver().insert(BlockchainTable.CONTENT_URI,
                                            BlockchainTable.getContentValues(data, true));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                        dialog.dismiss();
                        new MaterialDialog.Builder(MapsActivity.this)
                                .title("Something went wrong")
                                .content("Couldn't fetch data from the Blockchain")
                                .progress(true, 0)
                                .cancelable(false)
                                .build()
                                .show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Endpoints.SEARCH_TOKEN);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("q", "ixion");    //TODO: think about this
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToQueue(request);
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(apna, 14.0f));
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
                    //TODO: all these will show multiple dialogs if more than one filters are not found
                    new MaterialDialog.Builder(MapsActivity.this)
                            .title("Oops!")
                            .content("No result found")
                            .positiveText("Search De-Network")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    openSearchBazaarActivity();
                                }
                            })
                            .negativeText("GO BACK")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    onBackPressed();
                                }
                            })
                            .build()
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
                            .title("Oops!")
                            .content("No result found")
                            .positiveText("Search De-Network")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    openSearchBazaarActivity();
                                }
                            })
                            .negativeText("GO BACK")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    onBackPressed();
                                }
                            })
                            .build()
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
                            .content("No result found")
                            .positiveText("Search De-Network")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    openSearchBazaarActivity();
                                }
                            })
                            .negativeText("GO BACK")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    onBackPressed();
                                }
                            })
                            .build()
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
                            .content("No result found")
                            .positiveText("Search De-Network")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    openSearchBazaarActivity();
                                }
                            })
                            .negativeText("GO BACK")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    onBackPressed();
                                }
                            })
                            .build()
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
                                .content("No result found")
                                .positiveText("Search De-Network")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        openSearchBazaarActivity();
                                    }
                                })
                                .negativeText("GO BACK")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        onBackPressed();
                                    }
                                })
                                .build()
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