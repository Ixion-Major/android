package com.flatmates.ixion.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flatmates.ixion.InitApplication;
import com.flatmates.ixion.R;
import com.flatmates.ixion.activity.decentralized.BazaarSearchActivity;
import com.flatmates.ixion.model.BlockchainData;
import com.flatmates.ixion.model.BlockchainTable;
import com.flatmates.ixion.model.Data;
import com.flatmates.ixion.utils.Constants;
import com.flatmates.ixion.utils.Endpoints;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import static com.flatmates.ixion.utils.Constants.IS_USER_LOGGED_IN;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //TODO: show loading dialog to user until results are shown on map

    @BindView(R.id.fab_openbazaar_search)
    FloatingActionButton fabOBSearch;

    private GoogleMap mMap;
    String area = "";
    String bhk = "";
    String city = "";
    String state = "";
    String name = "";
    String budget = "";
    int minBudget;
    int maxBudget;
    double lat;
    double lon;
    double latitude, longitude;
    int dataCount = 0;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final int LOCATION_PERMISSION_REQUEST = 1001;

    private static Location mLastLocation;
    protected LocationRequest mLocationRequest;
    MaterialDialog placesDialog;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

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

        if (checkPlayServicesAvailablity()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

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

        if (!city.equals("") && !budget.equals("") && !bhk.equals(""))
            fetchBUDAREABHK(minBudget, maxBudget, city, bhk);
        else if (!area.equals("") && !budget.equals("") && !bhk.equals(""))
            fetchBUDAREABHK(minBudget, maxBudget, area, bhk);
        else if (!state.equals("") && !budget.equals("") && !bhk.equals(""))
            fetchBUDAREABHK(minBudget, maxBudget, state, bhk);
        else if (!city.equals("") && budget.equals("") && bhk.equals(""))
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
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void showNearbyPlaces(String lat, String lon) {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(new LatLngBounds(
                    new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)),
                    new LatLng(Double.parseDouble(lat), Double.parseDouble(lon))));
            Intent intent = intentBuilder.build(this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, 1);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        } finally {
            placesDialog.dismiss();
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Please provide location permission",
                    Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        try {
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            LatLng apna = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(apna).title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        } else {
//            Toast.makeText(this, "Current location not available. Fetching previous location.",
//                    Toast.LENGTH_SHORT).show();
            latitude = 28.567333;
            longitude = 77.318373;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        createLocationRequest();
    }

    private boolean checkPlayServicesAvailablity() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServicesAvailablity();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        try {
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Once connected with google api, get the location
        displayLocation();
    }

    protected void startLocationUpdates() {
        ActivityCompat.requestPermissions(MapsActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //TODO: move relevant code here
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                            mLocationRequest,
                            new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    mLastLocation = location;
                                }
                            });

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(1000000000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(100000000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final String lon = String.valueOf(marker.getPosition().longitude);
                final String lat = String.valueOf(marker.getPosition().latitude);
                Query recentPostsQuery = myRefData.orderByChild("lon").equalTo(lon);
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
                                    .content("Rent: " + rent + "\nNo. of Rooms: " + bhk + "\nAddress: " + address)
                                    .positiveText("MORE")
                                    .negativeText("PLACES")
                                    .neutralText("NAVIGATION")
                                    .btnStackedGravity(GravityEnum.START)
                                    .buttonsGravity(GravityEnum.START)
                                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                    Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + lat + "," + lon));
                                            startActivity(intent);
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            placesDialog = new MaterialDialog.Builder(MapsActivity.this)
                                                    .content("Fetching places")
                                                    .progress(true, 0)
                                                    .build();
                                            placesDialog.show();
                                            showNearbyPlaces(lat, lon);
                                        }
                                    })
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
                        databaseError.toException().printStackTrace();
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
                        dataCount++;
                    }
                }
                if (dataCount == 0) {
                    //TODO: all these will show multiple dialogs if more than one filters are not found
                    noResultDialog();
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
                        dataCount++;
                    }
                }
                if (dataCount == 0) {
                    noResultDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });
    }

    private void fetchAB(final String match, final String mBhk) {
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
                        if (mBhk.equals(map_bhk)) {
                            showMarker(name, lat, lon);
                            dataCount++;
                        }
                    }
                }
                if (dataCount == 0) {
                    noResultDialog();
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
                        dataCount++;
                    }
                }
                if (dataCount == 0) {
                    noResultDialog();
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
                            dataCount++;
                        }
                    }
                    if (dataCount == 0) {
                        noResultDialog();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });
    }

    private void fetchBUDAREABHK(final int min, final int max, final String match, final String mBHK) {
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
                    int mapRent = Integer.parseInt(map_rent);
                    lat = Double.parseDouble(data.getLat());
                    lon = Double.parseDouble(data.getLon());
                    if (match.equals(map_area) || match.equals(map_city) || match.equals(map_state)) {
                        if (mapRent >= min && mapRent <= max && map_bhk.equals(mBHK)) {
                            showMarker(name, lat, lon);
                            dataCount++;
                        }
                    }
                }
                if (dataCount == 0) {
                    noResultDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", databaseError.getDetails());
            }
        });
    }

    private void noResultDialog() {
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