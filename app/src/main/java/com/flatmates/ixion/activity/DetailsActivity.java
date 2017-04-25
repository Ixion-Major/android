package com.flatmates.ixion.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flatmates.ixion.R;
import com.flatmates.ixion.activity.chat.UserChatActivity;
import com.flatmates.ixion.activity.ui.FullImageActivity;
import com.flatmates.ixion.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.flatmates.ixion.utils.Constants.KEY_ADDRESS;
import static com.flatmates.ixion.utils.Constants.KEY_AREA;
import static com.flatmates.ixion.utils.Constants.KEY_BEDROOMS;
import static com.flatmates.ixion.utils.Constants.KEY_CITY;
import static com.flatmates.ixion.utils.Constants.KEY_EMAIL;
import static com.flatmates.ixion.utils.Constants.KEY_IMAGE;
import static com.flatmates.ixion.utils.Constants.KEY_IMAGE1;
import static com.flatmates.ixion.utils.Constants.KEY_IMAGE2;
import static com.flatmates.ixion.utils.Constants.KEY_IMAGE3;
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
    @BindView(R.id.image_thumb2)
    ImageView img_thumb2;
    @BindView(R.id.image_thumb3)
    ImageView img_thumb3;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_chat)
    FloatingActionButton fabChat;

    String area;
    String bhk;
    String city;
    String state;
    String rent, name, email, mobile, address, image, fullAddress, image1, image2, image3;

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
        image1 = bundle.getString(KEY_IMAGE1);
        image2 = bundle.getString(KEY_IMAGE2);
        image3 = bundle.getString(KEY_IMAGE3);

        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        if (city.equals(state)) {
            fullAddress = address + ", " + area + ", " + city;
        } else
            fullAddress = address + ", " + area + ", " + city + ", " + state;


        txt_name.setText(name);
        txt_address.setText(fullAddress);
        txt_bhk.setText(bhk);
        txt_rent.setText(rent);
//        txt_mobile.setText(mobile);
        txt_email.setText(email);

        Glide.with(this)
                .load(image)
                .fitCenter()
                .placeholder(getResources().getDrawable(R.drawable.placeholder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
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
                        txt_name.setBackgroundColor(colorImageBack);
                        return false;
                    }
                })
                .into(img_thumb);

        img_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, FullImageActivity.class);
                intent.putExtra("image", image );
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(DetailsActivity.this, (View)img_thumb, "profile");
                startActivity(intent, options.toBundle());

            }
        });

//        Glide.with(this)
//                .load(image1)
//                .placeholder(getResources().getDrawable(R.drawable.placeholder))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(img_thumb1);
        Glide.with(this)
                .load(image2)
                .fitCenter()
                .placeholder(getResources().getDrawable(R.drawable.placeholder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_thumb2);
        img_thumb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, FullImageActivity.class);
                intent.putExtra("image", image );
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(DetailsActivity.this, (View)img_thumb2, "profile");
                startActivity(intent, options.toBundle());

            }
        });
        Glide.with(this)
                .load(image3)
                .fitCenter()
                .placeholder(getResources().getDrawable(R.drawable.placeholder))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_thumb3);
        img_thumb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, FullImageActivity.class);
                intent.putExtra("image", image );
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(DetailsActivity.this, (View)img_thumb3, "profile");
                startActivity(intent, options.toBundle());

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                DetailsActivity.this.finish();
                break;
            case R.id.action_panaroma:
                Intent intent = new Intent(DetailsActivity.this, VRActivity.class);
                intent.putExtra(KEY_NAME, name);
                startActivity(intent);
                break;
        }
        return true;
    }

}
