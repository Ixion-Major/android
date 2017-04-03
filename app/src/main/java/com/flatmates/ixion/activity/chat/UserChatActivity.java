package com.flatmates.ixion.activity.chat;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.flatmates.ixion.R;

public class UserChatActivity extends AppCompatActivity {

    SharedPreferences preferences;

    private static final String TAG = UserChatActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //TODO: https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397

//        SendBird.connect(Utils.getSha256(preferences.getString(USER_EMAIL, "")),
//                new SendBird.ConnectHandler() {
//                    @Override
//                    public void onConnected(User user, SendBirdException e) {
//                        if (e != null) {
//                            e.printStackTrace();
//                            Toast.makeText(UserChatActivity.this, "An error occurred",
//                                    Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        Toast.makeText(UserChatActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
//                    }
//                });

    }


    @Override
    protected void onStart() {
        super.onStart();



//        List<String> userIDs = new ArrayList<>();
//        userIDs.add(Utils.getSha256(preferences.getString(USER_EMAIL, "")));
////        userIDs.add(//TODO: add sha256 of other user's email);
//        //TODO: use other method with name and image
//        GroupChannel.createChannelWithUserIds(userIDs, true, new GroupChannel.GroupChannelCreateHandler() {
//            @Override
//            public void onResult(GroupChannel groupChannel, SendBirdException e) {
//
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SendBird.disconnect(new SendBird.DisconnectHandler() {
//            @Override
//            public void onDisconnected() {
//                Log.i(TAG, "onDisconnected: Disconnected");
//            }
//        });
    }
}
