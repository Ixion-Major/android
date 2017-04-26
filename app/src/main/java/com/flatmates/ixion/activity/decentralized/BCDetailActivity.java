package com.flatmates.ixion.activity.decentralized;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flatmates.ixion.InitApplication;
import com.flatmates.ixion.R;
import com.flatmates.ixion.model.BlockchainData;
import com.flatmates.ixion.utils.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.flatmates.ixion.utils.Constants.KEY_BC_ITEM;

public class BCDetailActivity extends AppCompatActivity {

    @BindView(R.id.bc_title)
    TextView txtTitle;
    @BindView(R.id.bc_description)
    TextView txtDescription;
    @BindView(R.id.bc_price)
    TextView txtPrice;
    @BindView(R.id.bc_contract)
    TextView txtContract;
    @BindView(R.id.bc_vendor_location)
    TextView txtVendorLocation;
    @BindView(R.id.bc_vendor_name)
    TextView txtVendorName;
    @BindView(R.id.bc_image)
    CircleImageView imageView;
    @BindView(R.id.bc_image_background)
    LinearLayout linearLayout;
    @BindView(R.id.bc_title_background)
    LinearLayout titleLayout;
    @BindView(R.id.button_view_more_info)
    Button buttonViewMoreInfo;
    @BindView(R.id.drawer_layout)
    DrawerLayout navDrawer;
    @BindView(R.id.left_drawer)
    ListView navDrawerList;

    ActionBarDrawerToggle mDrawerToggle;
    BlockchainData data;
    String images;

    private static final String TAG = BCDetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcdetail);
        ButterKnife.bind(this);
        data = getIntent().getParcelableExtra(KEY_BC_ITEM);
        images = getIntent().getStringExtra("image");
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: image: " + Endpoints.endpointFetchImage(
                data.getGUID(),
                data.getImageHash()));
        Glide.with(this)
                .load(images)
                .override(720, 480)
                .fitCenter()
                .error(getResources().getDrawable(R.drawable.placeholder))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                        Palette palette = Palette.generate(bitmap);
                        int defaultColor = 0xFF333333;
                        int color = palette.getMutedColor(defaultColor);
                        int colorImageBack = palette.getDarkMutedColor(defaultColor);
                        linearLayout.setBackgroundColor(color);
                        titleLayout.setBackgroundColor(colorImageBack);
                        return false;
                    }
                })
                .into(imageView);

        txtTitle.setText(data.getTitle());
        txtDescription.setText(data.getDescription());
        txtPrice.setText(data.getPrice());
        txtContract.setText(data.getContractID());
        txtVendorLocation.setText(data.getVendorLocation());
        txtVendorName.setText(data.getVendorName());

        setupNavDrawer(data);

    }


    @OnClick(R.id.button_view_more_info)
    public void openDrawer() {
        navDrawer.openDrawer(Gravity.START);
    }


    private void setupNavDrawer(BlockchainData data) {
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] elements = {
                "GUID: " + data.getGUID(),
                "Payment currency: BTC",
                "Object ID: " + data.getObjectID(),
                "IUID: " + data.getImageHash(),
                "View contract",
                "BUY"
        };

        navDrawerList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, elements));
        navDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                BCDetailActivity.this, /* host Activity */
                navDrawer,             /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        navDrawer.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 4:
                    try {
                        new MaterialDialog.Builder(BCDetailActivity.this)
                                .title(R.string.property_contract)
                                .content(new JSONObject(data.getContract()).toString(4))
                                .positiveText(android.R.string.ok)
                                .neutralText(R.string.pay)
                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog,
                                                        @NonNull DialogAction which) {
                                        JsonObjectRequest request = new JsonObjectRequest(
                                                Request.Method.POST,
                                                Endpoints.endpointPurchaseContract(),
                                                null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        new MaterialDialog.Builder(BCDetailActivity.this)
                                                                .title(R.string.transfer_money)
                                                                .content("Transfer " +
                                                                        String.valueOf(Integer.valueOf(data.getPrice()) / 80572)
                                                                        + " BTC to address \'3J98t1WpEZ73CNmQviecRnyiWrnqRhWNLy\'" +
                                                                        " to buy property")
                                                                .positiveText(android.R.string.ok)
                                                                .cancelable(false)
                                                                .build()
                                                                .show();
                                                        Log.i(TAG, "onResponse: " + response);
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.e(TAG, "onErrorResponse: ", error);
                                                    }
                                                }
                                        ) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                HashMap<String, String> map = new HashMap<>();
                                                map.put("id", data.getContractID());
                                                Log.i(TAG, "getParams: " + data.getContractID());
                                                //TODO: ask for user info
                                                map.put("quantity", "1");
                                                map.put("ship_to", "12/1");
                                                map.put("address", "Naresh Vihar");
                                                map.put("city", "new Delhi");
                                                map.put("state", "Delhi");
                                                map.put("postal_code", "110001");
                                                map.put("moderator", "1010c55065e1248ce55485b92f7b3cf408b2c9aa");
                                                map.put("options", "");
                                                return map;
                                            }
                                        };
                                        InitApplication.getInstance().addToQueue(request);
                                    }
                                })
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    new MaterialDialog.Builder(BCDetailActivity.this)
                            .title(R.string.transfer_money)
                            .content("Transfer " +
                                    String.valueOf(Integer.valueOf(data.getPrice()) / 80572)
                                    + " BTC to address \n3J98t1WpEZ73CNmQviecRnyiWrnqRhWNLy\n" +
                                    " to buy property")
                            .positiveText(android.R.string.ok)
                            .cancelable(false)
                            .build()
                            .show();
                    break;
            }
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(navDrawer.isDrawerOpen(navDrawerList)) {
                    navDrawer.closeDrawer(navDrawerList);
                }
                else {
                    navDrawer.openDrawer(navDrawerList);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
