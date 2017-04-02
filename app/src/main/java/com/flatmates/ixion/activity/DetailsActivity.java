package com.flatmates.ixion.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flatmates.ixion.R;
import com.flatmates.ixion.utils.Constants;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.flatmates.ixion.utils.Constants.KEY_AREA;
import static com.flatmates.ixion.utils.Constants.KEY_BEDROOMS;
import static com.flatmates.ixion.utils.Constants.KEY_CITY;
import static com.flatmates.ixion.utils.Constants.KEY_STATE;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.city)
    TextView txt_city;
    @BindView(R.id.area)
    TextView txt_area;
    @BindView(R.id.state)
    TextView txt_state;
    @BindView(R.id.bhk)
    TextView txt_bhk;


    String area;
    String bhk;
    String city;
    String state;
    String rent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);


        Bundle bundle = getIntent().getExtras().getBundle(Constants.KEY_BUNDLE);
        area = bundle.getString(KEY_AREA).toLowerCase();
        bhk = bundle.getString(KEY_BEDROOMS);
        city = bundle.getString(KEY_CITY).toLowerCase();
        state = bundle.getString(KEY_STATE).toLowerCase();

        new AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage("Area: " + area + "\nRent: " + rent + "\nCity: " + city +
                        "\nState: " + state + "\nSize: " + bhk)
                .show();

        txt_area.setText(area);
        txt_city.setText(city);
        txt_bhk.setText(bhk);
        txt_state.setText(state);

        txt_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailsActivity.this,VRActivity.class));

            }
        });
    }
}
