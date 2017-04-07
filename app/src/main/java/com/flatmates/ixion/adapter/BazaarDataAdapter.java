package com.flatmates.ixion.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatmates.ixion.R;
import com.flatmates.ixion.model.BlockchainData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurpreet on 07/04/17.
 */

public class BazaarDataAdapter extends RecyclerView.Adapter<BazaarDataAdapter.MyViewHolder> {

    private Context context;
    private List<BlockchainData> dataArrayList;

    private static final String TAG = BazaarDataAdapter.class.getSimpleName();


    public BazaarDataAdapter(Context context, List<BlockchainData> dataArrayList) {
        this.context = context;
        this.dataArrayList = dataArrayList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_bazaar_element, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textviewOwner.setText(dataArrayList.get(holder.getAdapterPosition()).getVendorName());
        holder.textviewDescription.setText(dataArrayList.get(holder.getAdapterPosition()).getDescription());
        holder.textviewPrice.setText(dataArrayList.get(holder.getAdapterPosition()).getCurrency() + " " +
                dataArrayList.get(holder.getAdapterPosition()).getPrice());
        holder.textviewTitle.setText(dataArrayList.get(holder.getAdapterPosition()).getTitle());
    }


    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textview_owner)
        TextView textviewOwner;
        @BindView(R.id.textview_description)
        TextView textviewDescription;
        @BindView(R.id.textview_price)
        TextView textviewPrice;
        @BindView(R.id.textview_title)
        TextView textviewTitle;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
