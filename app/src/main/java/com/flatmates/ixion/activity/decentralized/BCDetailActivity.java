package com.flatmates.ixion.activity.decentralized;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
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
import com.flatmates.ixion.model.BlockchainData;
import com.flatmates.ixion.utils.Endpoints;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private static final String TAG = BCDetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcdetail);
        ButterKnife.bind(this);

        //TODO: set layout

        BlockchainData data = getIntent().getParcelableExtra(KEY_BC_ITEM);

        Glide.with(this)
                .load(Endpoints.endpointFetchImage(
                        data.getGUID(),
                        data.getImageHash()))
                .fitCenter()
                .error(getResources().getDrawable(R.drawable.placeholder))
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

    }
}
