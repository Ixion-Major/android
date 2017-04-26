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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BazaarSearchActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private String query = "";

    private static final String TAG = BazaarSearchActivity.class.getSimpleName();
    ArrayList<String> images = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazaar_search);
        ButterKnife.bind(this);
        query = getIntent().getStringExtra("query");
        if(query == null){
            query = "";
        }
        images.add("http://static.panoramio.com/photos/large/29471556.jpg");
        images.add("https://imganuncios.mitula.net/varapuzha_ernakulam_2bhk_upstair_house_for_lease_at_rs500000_3360054484869764375.jpg");
        images.add("https://media-cdn.tripadvisor.com/media/photo-s/02/c4/3d/85/grace-home.jpg");
        images.add("https://i2.wp.com/www.wiwigo.com/blog/wp-content/uploads/2016/07/hqdefault.jpg?resize=579%2C434&ssl=1");
        images.add("http://www.mytriptokerala.com/images/MunnarHomeStay/IMG-20140420-WA0003.jpg");
        images.add("http://mustardcountry.com/images/img_7.jpg");
        images.add("https://i.ytimg.com/vi/GLRhaaXwNa0/0.jpg");
        images.add("http://teakdoor.com/Gallery/albums/userpics/10004/normal_penang_chinese_temple_little_india_3.JPG");
        images.add("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSpRGAv46MitaKEk_OZcRueO7Dz92c76dz5ORuGZ7V3OSNO-w9W");
        images.add("https://3.imimg.com/data3/QD/IX/MY-3111081/home-front-view-designing-services-500x500.jpg");
        images.add("https://files.propertywala.com/photos/3f/J119083891.front-view.67647l.jpg");
    }


    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(BazaarSearchActivity.this);
        BazaarDataAdapter adapter = new BazaarDataAdapter(BazaarSearchActivity.this, getBlockchainData(), images);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }


    private List<BlockchainData> getBlockchainData() {
        Cursor cursor;
        if (!query.isEmpty())
            cursor = getContentResolver().query(BlockchainTable.CONTENT_URI,
                    null,
                    "categories='" + query.toLowerCase().trim() + "'",
                    null,
                    null);
        else
            cursor = getContentResolver().query(BlockchainTable.CONTENT_URI, null, null, null, null);
        return BlockchainTable.getRows(cursor, true);
    }

}
