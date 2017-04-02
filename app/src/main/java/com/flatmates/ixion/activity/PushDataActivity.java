package com.flatmates.ixion.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flatmates.ixion.R;
import com.flatmates.ixion.model.Data;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PushDataActivity extends AppCompatActivity {

    EditText lon, lat;
    String lon_str, lat_str;
    Data data;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefUserData = database.getReference("Data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_data);

        lon = (EditText) findViewById(R.id.lon);
        lat = (EditText) findViewById(R.id.lat);

        Button btn = (Button) findViewById(R.id.btn_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                lon_str = lon.getText().toString();
//                lat_str = lat.getText().toString();
//
//                data =  new Data();
//                data.setLon(lon_str);
//                data.setLat(lat_str);
//
//                System.out.println(lon_str+"  "+lat_str);
//
//                myRefUserData.push().setValue(data);
            }
        });



    }
}
