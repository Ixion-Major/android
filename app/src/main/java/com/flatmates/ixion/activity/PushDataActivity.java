package com.flatmates.ixion.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

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
    @BindView(R.id.EditText_Name)
    MaterialEditText etname;
    @BindView(R.id.EditText_EMail)
    MaterialEditText etemail;
    @BindView(R.id.EditText_PhoneNo)
    MaterialEditText etphone;
    @BindView(R.id.EditText_Address)
    MaterialEditText etaddress;
    @BindView(R.id.EditText_Rent)
    MaterialEditText etrent;
    @BindView(R.id.EditText_BHK)
    MaterialEditText etbhk;
    @BindView(R.id.EditText_city)
    MaterialEditText etcity;
    @BindView(R.id.EditText_Area)
    MaterialEditText etarea;
    @BindView(R.id.EditText_state)
    MaterialEditText etstate;
    @BindView(R.id.EditText_title)
    MaterialEditText etTitle;
    @BindView(R.id.EditText_desc)
    MaterialEditText etDescription;

    String lon_str, lat_str;
    Data data;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefUserData = database.getReference("UploadedProperties");

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

        //TODO: get lat lon from address -> GeoEncoding, street view

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
        data.setBhk(etbhk.getText().toString() + "bhk");
//                System.out.println(lon_str+"  "+lat_str);

        //TODO: Check for correct input before pushing
        if (!switchUploadDecentralised.isChecked() && !etTitle.getText().toString().equals("")
                && !etDescription.getText().toString().equals("") && !etbhk.getText().toString().equals("")
                && !etphone.getText().toString().equals("") && !etstate.getText().toString().equals("") &&
                !etarea.getText().toString().equals("") && !etcity.getText().toString().equals("") &&
                !etrent.getText().toString().equals("") && !etname.getText().toString().equals("")) {
            myRefUserData.push().setValue(data);
            Toast.makeText(this, "Property Listed!", Toast.LENGTH_SHORT).show();
            etname.setText("");
            etemail.setText("");
            etbhk.setText("");
            etDescription.setText("");
            etTitle.setText("");
            etaddress.setText("");
            etarea.setText("");
            etphone.setText("");
            etcity.setText("");
            etstate.setText("");
            etrent.setText("");
        } else {
            if (switchUploadDecentralised.isChecked() && !etTitle.getText().toString().equals("")
                    && !etDescription.getText().toString().equals("") && !etbhk.getText().toString().equals("")
                    && !etphone.getText().toString().equals("") && !etstate.getText().toString().equals("") &&
                    !etarea.getText().toString().equals("") && !etcity.getText().toString().equals("") &&
                    !etrent.getText().toString().equals("") && !etname.getText().toString().equals("")) {
                myRefUserData.push().setValue(data);
                uploadToBlockChain();
            } else {
                Toast.makeText(this, "Please fill all information", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadToBlockChain() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Endpoints.endpointCreateListing(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: " + response);
                        if (response.contains("true")) {
                            Toast.makeText(PushDataActivity.this, "Successfully created contract listing",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                        Toast.makeText(PushDataActivity.this, "Couldn't create a contract",
                                Toast.LENGTH_SHORT).show();
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
                params.put("category", "['" + etbhk.getText().toString() + "bhk" + "', '"
                        + etcity.getText().toString() + "']");
                params.put("terms_conditions", "");
                params.put("returns", "");
                params.put("shipping_currency_code", "INR");
                params.put("shipping_domestic", "100");
                params.put("shipping_international", "500");
                params.put("condition", "new");
                params.put("free_shipping", "false");
                params.put("sku", "");
                params.put("images", "2e541a02e89c532d726d95e62389c721563cdd29");   //TODO: use upload image api
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