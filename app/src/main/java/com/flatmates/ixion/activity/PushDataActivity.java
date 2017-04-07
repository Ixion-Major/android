package com.flatmates.ixion.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flatmates.ixion.InitApplication;
import com.flatmates.ixion.R;
import com.flatmates.ixion.model.Data;
import com.flatmates.ixion.utils.Endpoints;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PushDataActivity extends AppCompatActivity {

    @BindView(R.id.btn_submit)
    Button buttonSubmit;
    @BindView(R.id.switch_upload_decentralised)
    Switch switchUploadDecentralised;

    MaterialEditText lon, lat, etname, etemail, etphone, etaddress,
            etrent, etbhk, etcity, etarea, etstate, etTitle, etDescription;

    String lon_str, lat_str;
    Data data;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefUserData = database.getReference("Data");

    private static final String TAG = PushDataActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_data);
        ButterKnife.bind(this);
        setTitle("List Property");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

        //TODO: get lat lon from address

        etname = (MaterialEditText) findViewById(R.id.EditText_Name);
        etemail = (MaterialEditText) findViewById(R.id.EditText_EMail);
        etphone = (MaterialEditText) findViewById(R.id.EditText_PhoneNo);
        etaddress = (MaterialEditText) findViewById(R.id.EditText_Address);
        etrent = (MaterialEditText) findViewById(R.id.EditText_Rent);
        etcity = (MaterialEditText) findViewById(R.id.EditText_city);
        etarea = (MaterialEditText) findViewById(R.id.EditText_Area);
        etstate = (MaterialEditText) findViewById(R.id.EditText_state);
        etTitle = (MaterialEditText) findViewById(R.id.EditText_title);
        etDescription = (MaterialEditText) findViewById(R.id.EditText_desc);
        etbhk = (MaterialEditText) findViewById(R.id.EditText_BHK);

    }


    @OnClick(R.id.btn_submit)
    public void pushToFirebase() {
//        lon_str = lon.getText().toString();
//                lat_str = lat.getText().toString();
        data = new Data();
        data.setName(etname.getText().toString().trim());
        data.setEmail(etemail.getText().toString().trim());
        data.setMobile(etphone.getText().toString().trim());
        data.setAddress(etaddress.getText().toString().trim());
        data.setRent(etrent.getText().toString().trim());
        data.setCity(etcity.getText().toString().trim());
        data.setArea(etarea.getText().toString().trim());
        data.setState(etstate.getText().toString().trim());
//                System.out.println(lon_str+"  "+lat_str);
//
//        myRefUserData.push().setValue(data);

        if (switchUploadDecentralised.isChecked()) {
            uploadToBlockChain();
        }
    }


    private void uploadToBlockChain() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Endpoints.endpointCreateListing(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("expiration_date", "");
                params.put("metadata_category", "PHYSICAL_GOOD");
                params.put("title", etTitle.getText().toString());
                params.put("description", etDescription.getText().toString());
                params.put("currency_code", "INR");
                params.put("price", etrent.getText().toString());
                params.put("process_time", "3");
                params.put("nsfw", "false");
                params.put("shipping_origin", "India");
                params.put("shipping_regions", "India");
                params.put("est_delivery_domestic", "7");
                params.put("est_delivery_international", "15");
                params.put("keywords", "['ixion']");
                params.put("category", "['" + etbhk.getText().toString() + "', '" + etcity.getText().toString() + "']");
                params.put("terms_conditions", "");
                params.put("returns", "");
                params.put("shipping_currency_code", "INR");
                params.put("shipping_domestic", "100");
                params.put("shipping_international", "500");
                params.put("condition", "new");
                params.put("free_shipping", "false");
                params.put("sku", "");
                params.put("images", "2e541a02e89c532d726d95e62389c721563cdd29");   //TODO: upload image api
                params.put("moderators", "['1010c55065e1248ce55485b92f7b3cf408b2c9aa']");


                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToQueue(request);
    }

}