package com.flatmates.ixion.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flatmates.ixion.R;
import com.flatmates.ixion.model.Data;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PushDataActivity extends AppCompatActivity {

    @BindView(R.id.btn_submit)
    Button buttonSubmit;

    EditText lon, lat, etname, etemail, etphone, etaddress, etrent, etbhk, etcity, etarea, etstate;
    String lon_str, lat_str;
    Data data;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefUserData = database.getReference("Data");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_data);
        ButterKnife.bind(this);

        //TODO: get lat lon from address

        etname = (EditText) findViewById(R.id.EditText_Name);
        etemail = (EditText) findViewById(R.id.EditText_EMail);
        etphone = (EditText) findViewById(R.id.EditText_PhoneNo);
        etaddress = (EditText) findViewById(R.id.EditText_Address);
        etrent = (EditText) findViewById(R.id.EditText_Rent);
        etcity = (EditText) findViewById(R.id.EditText_city);
        etarea = (EditText) findViewById(R.id.EditText_Area);
        etstate = (EditText) findViewById(R.id.EditText_state);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               lon_str = lon.getText().toString();
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
                myRefUserData.push().setValue(data);
            }
        });


    }
}