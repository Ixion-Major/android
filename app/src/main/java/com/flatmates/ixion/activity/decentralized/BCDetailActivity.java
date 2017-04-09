package com.flatmates.ixion.activity.decentralized;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.flatmates.ixion.R;
import com.flatmates.ixion.model.BlockchainData;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.flatmates.ixion.utils.Constants.KEY_BC_ITEM;

public class BCDetailActivity extends AppCompatActivity {

    @BindView(R.id.textview)
    TextView textView;

    private static final String TAG = BCDetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcdetail);
        ButterKnife.bind(this);

        //TODO: set layout

        BlockchainData data = getIntent().getParcelableExtra(KEY_BC_ITEM);

        textView.setText(data.getGUID() + "\n" + data.getTitle() + "\n" + data.getContractID());

    }
}
