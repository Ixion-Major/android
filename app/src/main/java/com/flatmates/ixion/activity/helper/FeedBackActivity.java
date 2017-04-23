package com.flatmates.ixion.activity.helper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.flatmates.ixion.R;
import com.flatmates.ixion.model.Data;
import com.flatmates.ixion.model.FeedBackData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedBackActivity extends AppCompatActivity {

    @BindView(R.id.button_fb_submit)
    Button btnSubmit;
    @BindView(R.id.editText_fb_title)
    MaterialEditText etTitle;
    @BindView(R.id.editText_fb_description)
    MaterialEditText etDescription;

    FeedBackData feedBackData;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefUserData = database.getReference("FeedBack");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_fb_submit)
    public void submitFeedBack(){
        feedBackData = new FeedBackData();
        feedBackData.setTitle(etTitle.getText().toString().trim());
        feedBackData.setDescription(etDescription.getText().toString().trim());
        if(!etTitle.getText().toString().equals("") && !etDescription.getText().toString().equals("")) {
            myRefUserData.push().setValue(feedBackData);
            Toast.makeText(this, "Your feedback is submitted successfully!", Toast.LENGTH_SHORT).show();
            etTitle.getText().clear();
            etDescription.getText().clear();
        }
        else
            Toast.makeText(this, "Please fill all information", Toast.LENGTH_SHORT).show();
    }
}
