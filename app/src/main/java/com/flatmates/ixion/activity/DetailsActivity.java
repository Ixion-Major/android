package com.flatmates.ixion.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flatmates.ixion.R;
import com.flatmates.ixion.activity.chat.UserChatActivity;
import com.flatmates.ixion.utils.Constants;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flatmates.ixion.utils.Constants.KEY_ADDRESS;
import static com.flatmates.ixion.utils.Constants.KEY_AREA;
import static com.flatmates.ixion.utils.Constants.KEY_BEDROOMS;
import static com.flatmates.ixion.utils.Constants.KEY_CITY;
import static com.flatmates.ixion.utils.Constants.KEY_EMAIL;
import static com.flatmates.ixion.utils.Constants.KEY_IMAGE;
import static com.flatmates.ixion.utils.Constants.KEY_MOBILE;
import static com.flatmates.ixion.utils.Constants.KEY_NAME;
import static com.flatmates.ixion.utils.Constants.KEY_RENT;
import static com.flatmates.ixion.utils.Constants.KEY_STATE;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.owner_name)
    TextView txt_name;
    @BindView(R.id.address)
    TextView txt_address;
    @BindView(R.id.rent)
    TextView txt_rent;
    @BindView(R.id.bhk)
    TextView txt_bhk;
    @BindView(R.id.email)
    TextView txt_email;
    //    @BindView(R.id.mobile)
//    TextView txt_mobile;
    @BindView(R.id.image_thumb)
    ImageView img_thumb;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_chat)
    FloatingActionButton fabChat;

    String area;
    String bhk;
    String city;
    String state;
    String rent, name, email, mobile, address, image;

    private static final String TAG = DetailsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

        Bundle bundle = getIntent().getExtras().getBundle(Constants.KEY_BUNDLE);
        area = bundle.getString(KEY_AREA).toLowerCase();
        bhk = bundle.getString(KEY_BEDROOMS);
        city = bundle.getString(KEY_CITY).toLowerCase();
        state = bundle.getString(KEY_STATE).toLowerCase();
        rent = bundle.getString(KEY_RENT);
        name = bundle.getString(KEY_NAME);
        email = bundle.getString(KEY_EMAIL);
        mobile = bundle.getString(KEY_MOBILE);
        address = bundle.getString(KEY_ADDRESS);
        image = bundle.getString(KEY_IMAGE);

//        new AlertDialog.Builder(this)
//                .setTitle("Information")
//                .setMessage("Area: " + area + "\nRent: " + rent + "\nCity: " + city +
//                        "\nState: " + state + "\nSize: " + bhk +name+email+mobile+address+image)
//                .show();

        txt_name.setText(name);
        txt_address.setText(address);
        txt_bhk.setText(bhk);
        txt_rent.setText(rent);
//        txt_mobile.setText(mobile);
        txt_email.setText(email);

        Picasso.with(this)
                .load(image)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .into(img_thumb);

        img_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, VRActivity.class);
                intent.putExtra(KEY_BEDROOMS, bhk);
                startActivity(intent);

            }
        });
    }


    @OnClick(R.id.fab_chat)
    public void openChatActivity() {
        Intent intent = new Intent(DetailsActivity.this, UserChatActivity.class);
        intent.putExtra(KEY_NAME, name);
        intent.putExtra(KEY_EMAIL, email);
        intent.putExtra(KEY_ADDRESS, address);
        intent.putExtra(KEY_MOBILE, mobile);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                DetailsActivity.this.finish();
        }
        return true;
    }

}
