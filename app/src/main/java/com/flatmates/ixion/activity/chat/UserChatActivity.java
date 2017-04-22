package com.flatmates.ixion.activity.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import static com.flatmates.ixion.utils.Constants.KEY_EMAIL;
import static com.flatmates.ixion.utils.Constants.KEY_MOBILE;
import static com.flatmates.ixion.utils.Constants.KEY_NAME;
import static com.flatmates.ixion.utils.Constants.USER_EMAIL;

public class UserChatActivity extends AppCompatActivity {

    @BindView(R.id.imagebutton_send)
    ImageButton sendButton;
    @BindView(R.id.chat_llayout)
    LinearLayout linearLayout;
    @BindView(R.id.editText)
    EditText et;
    @BindView(R.id.scrollView2)
    ScrollView scrollView;

    String textContract = "";
    private String userEmail, temp_key;
    private DatabaseReference root;
    SharedPreferences preferences;

    private static final String TAG = UserChatActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(UserChatActivity.this);
        userEmail = preferences.getString(USER_EMAIL, "").replace(".", "_@_");
        if (getIntent().getStringExtra(KEY_EMAIL) == null)
            root = FirebaseDatabase.getInstance().getReference()
                    .child("chatroom")
                    .child("singhdaman4321@gmail_@_com" + "," + userEmail);
        else {

            String ownerEmail = getIntent().getStringExtra(KEY_EMAIL).replace(".", "_@_");

            if (ownerEmail.compareTo(userEmail) > 0)
                root = FirebaseDatabase.getInstance().getReference()
                        .child("chatroom")
                        .child(ownerEmail + "," + userEmail);
            else
                root = FirebaseDatabase.getInstance().getReference()
                        .child("chatroom")
                        .child(userEmail + "," + ownerEmail);
        }
        //TODO: need a separate chat panel for owners where they get pings on receiving message

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
        setTitle("Private Chat");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference messageRoot = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("name", userEmail);
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
//            String chatUsername = ((String) ((DataSnapshot) i.next()).getValue()).split("@")[0];
            String chatUsername = ((String) ((DataSnapshot) i.next()).getValue());
            textContract =  chatUsername + " :- " + chatMsg + "\n" + textContract;
            if (chatUsername.equals(userEmail)) {
                showUserInputBubble(chatMsg);
            } else
                showServerResponseBubble(chatMsg);
                et.setText("");
        }
    }

    private void showUserInputBubble(final String input) {
        Button userMessage = new Button(UserChatActivity.this);
        userMessage.setTransformationMethod(null);
        userMessage.setText(input);
        userMessage.setGravity(Gravity.START);
        userMessage.setTextSize(16);
        userMessage.setTextColor(getResources().getColor(android.R.color.white));
        userMessage.setPadding(20, 20, 40, 20);
        userMessage.setBackground(getResources().getDrawable(R.drawable.outgoing_message_bubble));
        LinearLayout.LayoutParams llp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        llp.gravity = Gravity.END;
        llp.setMargins(150, 10, 0, 10); // llp.setMargins(left, top, right, bottom);
        userMessage.setLayoutParams(llp);

        linearLayout.addView(userMessage);
    }

    private void showServerResponseBubble(String serverResponse) {
        Button serverMessage = new Button(UserChatActivity.this);
        serverMessage.setTransformationMethod(null);
        serverMessage.setText(serverResponse);
        serverMessage.setGravity(Gravity.START);
        serverMessage.setTextSize(16);
        serverMessage.setPadding(60, 20, 20, 20);
        serverMessage.setTextColor(getResources().getColor(android.R.color.white));
        serverMessage.setBackground(getResources().getDrawable(R.drawable.incoming_message_bubble));
        LinearLayout.LayoutParams llp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        llp.setMargins(0, 10, 150, 10); // llp.setMargins(left, top, right, bottom);
        llp.gravity = Gravity.START;
        serverMessage.setLayoutParams(llp);

        linearLayout.addView(serverMessage);
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
                                createContract(textContract);
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
                        .content("This chat will be deleted after you've paid for the contract")
                        .positiveText("Create")
                        .negativeText("Exit")
                        .build()
                        .show();
                break;
            case android.R.id.home:
                onBackPressed();
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