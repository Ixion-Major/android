package com.flatmates.ixion.activity.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flatmates.ixion.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    //TODO:add name of the user to variable "username"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        ButterKnife.bind(this);
//        SharedPreferences preference = this.getPreferences(MODE_PRIVATE);
        
        root = FirebaseDatabase.getInstance().getReference().child("chatroom");
        username = "Gurpreet";
//        mp=MediaPlayer.create(this, R.raw.button);

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
//                mp.start();

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
            tv.append(chatUsername + " :- " + chatMsg + "\n");
            et.setText("");
        }
    }

}