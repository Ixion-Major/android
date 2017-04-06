package com.flatmates.ixion.activity.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flatmates.ixion.InitApplication;
import com.flatmates.ixion.R;
import com.flatmates.ixion.utils.Endpoints;
import com.flatmates.ixion.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.flatmates.ixion.utils.Constants.KEY_ADDRESS;
import static com.flatmates.ixion.utils.Constants.KEY_MOBILE;
import static com.flatmates.ixion.utils.Constants.KEY_NAME;
import static com.flatmates.ixion.utils.Constants.USER_EMAIL;

public class UserChatActivity extends AppCompatActivity {

    @BindView(R.id.imagebutton_send)
    ImageButton sendButton;
    @BindView(R.id.textView2)
    TextView tv;
    @BindView(R.id.editText)
    EditText et;
    @BindView(R.id.scrollView2)
    ScrollView scrollView;

    //    MediaPlayer mp;
    private String username, temp_key;
    private DatabaseReference root;
    SharedPreferences preferences;

    private static final String TAG = UserChatActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(UserChatActivity.this);
        root = FirebaseDatabase.getInstance().getReference()
                .child("chatroom")
                .child(getIntent().getStringExtra(KEY_ADDRESS) + getIntent().getStringExtra(KEY_MOBILE));
        username = preferences.getString(USER_EMAIL, "");
        //TODO: need a separate chat panel for owners where they get pings on receiving message

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference messageRoot = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("name", username);
                map2.put("message", et.getText().toString());
                messageRoot.updateChildren(map2);

                //hides the soft keys
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                //Auto scroll down
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void append_chat(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            String chatMsg = (String) ((DataSnapshot) i.next()).getValue();
            String chatUsername = (String) ((DataSnapshot) i.next()).getValue();
            tv.append(chatUsername + " :-  " + chatMsg + "\n");
            et.setText("");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_contract:
                new MaterialDialog.Builder(UserChatActivity.this)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                createContract(tv.getText().toString());
                                //TODO: delete chat after creating contract
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(UserChatActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .title("Create Contract")
                        .content("This chat will be deleted after creating a contract")
                        .positiveText("Create")
                        .negativeText("Exit")
                        .build()
                        .show();
        }


        return true;
    }

    private void createContract(String contractText) {

        JsonObjectRequest request = new JsonObjectRequest(Endpoints.endpointContractRegister(Utils.getSha256(contractText)),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response);
                        /*  {"success":"true",
                            "digest":"c22e7286e039eb49d427df37d0bdde256c843ba69c8c83d493ba0be235cfe842",
                            "pay_address":"13mWyZKEzzmRTxJiDv5EFh8WJ7urQdHvBk",
                            "price":500000}
                         */
                        //OR
                        /*
                            {"success":false,
                            "reason":"existing",
                            "digest":"c22e7286e039eb49d427df37d0bdde256c843ba69c8c83d493ba0be235cfe842"}
                         */

                        try {
                            if (response.getString("success").equals("true")) {
                                String digest = response.getString("digest");
                                String payAddress = response.getString("pay_address");
                                String price = response.getString("price");
                                //TODO: mail user with details / screenshot take
                                new MaterialDialog.Builder(UserChatActivity.this)
                                        .title("Congrats! Contract generated")
                                        .content("Digest: " + digest + "\n\n" +
                                                "Pay Address: " + payAddress + "\n\n" +
                                                "Price: BTC " + price)
                                        .build()
                                        .show();
                            } else {
                                String reason = response.getString("reason");
                                String digest = response.getString("digest");
                                new MaterialDialog.Builder(UserChatActivity.this)
                                        .title("Contract already exists")
                                        .content("Digest: " + digest + "\n\n" +
                                                "Reason: " + reason)
                                        .build()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                    }
                });
        InitApplication.getInstance().addToQueue(request);

    }

}