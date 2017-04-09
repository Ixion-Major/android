package com.flatmates.ixion.activity.decentralized;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.flatmates.ixion.R;
import com.flatmates.ixion.adapter.BazaarDataAdapter;
import com.flatmates.ixion.model.BlockchainData;
import com.flatmates.ixion.model.BlockchainTable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BazaarSearchActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private static final String TAG = BazaarSearchActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazaar_search);
        ButterKnife.bind(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(BazaarSearchActivity.this);
        BazaarDataAdapter adapter = new BazaarDataAdapter(BazaarSearchActivity.this, getBlockchainData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }


    private List<BlockchainData> getBlockchainData() {
        Cursor cursor = getContentResolver().query(BlockchainTable.CONTENT_URI, null, null, null, null);
        return BlockchainTable.getRows(cursor, true);
    }

}
