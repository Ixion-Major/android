package com.flatmates.ixion.activity.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.flatmates.ixion.R;
import com.flatmates.ixion.model.Data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.flatmates.ixion.utils.Constants.KEY_CHATS;
import static com.flatmates.ixion.utils.Constants.KEY_EMAIL;
import static com.flatmates.ixion.utils.Constants.USER_EMAIL;

public class AllChatsActivity extends AppCompatActivity {

    @BindView(R.id.listview_chats)
    ListView listviewChats;

    SharedPreferences preferences;

    ArrayList<String> chats = new ArrayList<>();

    private static final String TAG = AllChatsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);
        ButterKnife.bind(this);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

        setTitle("All Chats");

        preferences = PreferenceManager.getDefaultSharedPreferences(AllChatsActivity.this);

        chats = getIntent().getStringArrayListExtra(KEY_CHATS);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(AllChatsActivity.this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                chats);
        listviewChats.setAdapter(adapter);
        listviewChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllChatsActivity.this, UserChatActivity.class);
                intent.putExtra(KEY_EMAIL, chats.get(position));
                startActivity(intent);
            }
        });

    }


}
