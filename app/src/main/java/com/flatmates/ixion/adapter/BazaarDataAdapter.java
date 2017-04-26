package com.flatmates.ixion.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flatmates.ixion.R;
import com.flatmates.ixion.activity.decentralized.BCDetailActivity;
import com.flatmates.ixion.model.BlockchainData;
import com.flatmates.ixion.utils.Endpoints;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.flatmates.ixion.utils.Constants.KEY_BC_ITEM;

/**
 * Created by Gurpreet on 07/04/17.
 */

public class BazaarDataAdapter extends RecyclerView.Adapter<BazaarDataAdapter.MyViewHolder> {

    private Context context;
    private List<BlockchainData> dataArrayList;
    private ArrayList<String> images;

    private static final String TAG = BazaarDataAdapter.class.getSimpleName();


    public BazaarDataAdapter(Context context, List<BlockchainData> dataArrayList, ArrayList<String> images) {
        this.context = context;
        this.dataArrayList = dataArrayList;
        this.images = images;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_bazaar_element, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.textviewOwner.setText(dataArrayList.get(holder.getAdapterPosition()).getVendorName());
        holder.textviewDescription.setText(dataArrayList.get(holder.getAdapterPosition()).getDescription());
        holder.textviewPrice.setText(dataArrayList.get(holder.getAdapterPosition()).getCurrency() + " " +
                dataArrayList.get(holder.getAdapterPosition()).getPrice());
        holder.textviewTitle.setText(dataArrayList.get(holder.getAdapterPosition()).getTitle());
        Glide.with(context)
                .load(images.get(position))
                .error(context.getResources().getDrawable(R.drawable.placeholder))
                .fitCenter()
                .into(holder.imageviewProperty);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BCDetailActivity.class);
                intent.putExtra(KEY_BC_ITEM, dataArrayList.get(holder.getAdapterPosition()));
                intent.putExtra("image", images.get(position));
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textview_owner)
        TextView textviewOwner;
        @BindView(R.id.textview_description)
        TextView textviewDescription;
        @BindView(R.id.textview_price)
        TextView textviewPrice;
        @BindView(R.id.textview_title)
        TextView textviewTitle;
        @BindView(R.id.imageview_property)
        CircleImageView imageviewProperty;
        @BindView(R.id.linearlayout_property_item)
        LinearLayout linearLayout;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
