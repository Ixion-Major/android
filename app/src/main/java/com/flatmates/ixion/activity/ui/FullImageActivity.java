package com.flatmates.ixion.activity.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flatmates.ixion.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullImageActivity extends AppCompatActivity {

    @BindView(R.id.full_image)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        ButterKnife.bind(this);
        String image = getIntent().getStringExtra("image");
        Glide.with(this)
                .load(image)
                .fitCenter()
                .placeholder(getResources().getDrawable(R.drawable.placeholder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }
}
