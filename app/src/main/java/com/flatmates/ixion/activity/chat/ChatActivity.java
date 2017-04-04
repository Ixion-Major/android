package com.flatmates.ixion.activity.chat;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flatmates.ixion.InitApplication;
import com.flatmates.ixion.R;
import com.flatmates.ixion.activity.LoginActivity;
import com.flatmates.ixion.activity.MapsActivity;
import com.flatmates.ixion.model.UserMessage;
import com.flatmates.ixion.utils.Constants;
import com.flatmates.ixion.utils.Endpoints;
import com.flatmates.ixion.utils.NetworkConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.view.View.GONE;
import static com.flatmates.ixion.utils.Constants.IS_USER_LOGGED_IN;
import static com.flatmates.ixion.utils.Constants.IS_USER_ORDER_COMPLETE;
import static com.flatmates.ixion.utils.Constants.KEY_AREA;
import static com.flatmates.ixion.utils.Constants.KEY_BEDROOMS;
import static com.flatmates.ixion.utils.Constants.KEY_BUDGET;
import static com.flatmates.ixion.utils.Constants.KEY_BUNDLE;
import static com.flatmates.ixion.utils.Constants.KEY_CITY;
import static com.flatmates.ixion.utils.Constants.KEY_FEATURE;
import static com.flatmates.ixion.utils.Constants.KEY_STATE;

public class ChatActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,
        TextToSpeech.OnUtteranceCompletedListener {

    @BindView(R.id.imagebutton_speak)
    ImageButton imagebuttonSpeak;
    @BindView(R.id.message_view)
    LinearLayout messageView;
    @BindView(R.id.scrollview)
    ScrollView scrollView;
    @BindView(R.id.edittext_user_message)
    MaterialEditText edittextUserMessage;
    @BindView(R.id.button_send)
    ImageButton buttonSend;
    @BindView(R.id.button_show_results)
    Button buttonShowResults;

    TextToSpeech tts;
    Bundle bundle;
    SharedPreferences preferences;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final String TAG = ChatActivity.class.getSimpleName();

//    TODO: save user city, etc here and ask for more info of not given by user- CLIENT SIDE,
//          incorporate other helper endpoints

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: show empty view
//        if (messageView.getChildAt(0) == null) {
//            setContentView(R.layout.layout_empty_view);
//        } else {
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);

        showPreviousConversation();
        bundle = new Bundle();

        String enabledMethods =
                Settings.Secure.getString(ChatActivity.this.getContentResolver(),
                        Settings.Secure.ENABLED_INPUT_METHODS);

        /*
         * If voice search is enabled, show UI, else
         * show dialog with info to turn on voice search
         */
        if (enabledMethods.contains("voicesearch")) {
            Log.i(TAG, "onCreate: " + enabledMethods);

            tts = new TextToSpeech(ChatActivity.this, this);

            imagebuttonSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkConnection.isNetworkConnected(ChatActivity.this)) {
                        promptSpeechInput();
                    } else {
                        Toast.makeText(ChatActivity.this, "Please connect to network",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            String msg = "On your device go to\nSettings " +
                    "-> Language and Input\nand turn Google voice input on";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatActivity.this);
            alertDialogBuilder.setTitle("Google voice input required")
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        }
//        }

    }


    @OnClick(R.id.button_send)
    public void sendToServer() {
        String input = edittextUserMessage.getText().toString().trim();
        if (input.equals(""))
            Toast.makeText(ChatActivity.this, "Enter some query", Toast.LENGTH_SHORT).show();
        else {
            edittextUserMessage.setText("");
            sendInputToServer(input);
            showUserInputBubble(input);
        }
    }


    @OnClick({R.id.edittext_user_message, R.id.imagebutton_speak})
    public void setFABVisibilityToGone() {
        buttonShowResults.animate().alpha(0.0f).setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        buttonShowResults.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
    }


    @OnClick(R.id.button_show_results)
    public void showResults() {
        clearRealmDB();
        messageView.removeAllViews();
        Intent intent = new Intent(ChatActivity.this, MapsActivity.class);
        intent.putExtra(KEY_BUNDLE, bundle);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        buttonShowResults.setVisibility(GONE);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                });
    }


    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(ChatActivity.this,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Receiving speech input
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String input = result.get(0).toLowerCase();
                    sendInputToServer(input);
                    showUserInputBubble(input);
                }
                break;
            }
        }
    }


    private void showUserInputBubble(final String input) {
        TextView userMessage = new TextView(ChatActivity.this);
        userMessage.setText(input);
        userMessage.setGravity(Gravity.END);
        userMessage.setTextSize(18);
//        userMessage.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
//        userMessage.setBackground(getResources().getDrawable(R.drawable.incoming_message_bubble));
        userMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        LinearLayout.LayoutParams llp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        llp.setMargins(100, 20, 10, 20); // llp.setMargins(left, top, right, bottom);
        userMessage.setLayoutParams(llp);

        messageView.addView(userMessage);
    }


    private void sendInputToServer(final String input) {
        //TODO: judge here what API endpoint to use
        //store in db and send to server for response

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UserMessage message = realm.createObject(UserMessage.class);
                    message.setMessage(input);
                    message.setUserSent(true);
                }
            });
        } finally {
            if (realm != null)
                realm.close();
        }

        StringRequest request = new StringRequest(Request.Method.POST,
                Endpoints.endpointChatbot(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String serverResponse) {
                        String area, bedrooms, city, state, budget, feature, message;
                        try {
                            JSONObject object = new JSONObject(serverResponse);
                            if (object.getString("status").equals("1")) {
                                //TODO: do stuff with the extracted information

                                Log.i(TAG, "onResponse: " + serverResponse);
                                /*
                                {
                                     "area": null,
                                     "bedrooms": "3bhk",
                                     "budget": null,
                                     "city": null,
                                     "feature": "hospital",
                                     "message": "",
                                     "state": null,
                                     "status": "1"
                                 }
                                 */

                                area = object.getString("area");
                                city = object.getString("city");
                                state = object.getString("state");
                                bedrooms = object.getString("bedrooms");
                                budget = object.getString("budget");
                                feature = object.getString("feature");
                                //TODO: use other parameters

                                bundle.putString(KEY_AREA, area);
                                bundle.putString(KEY_BEDROOMS, bedrooms);
                                bundle.putString(KEY_CITY, city);
                                bundle.putString(KEY_STATE, state);
                                bundle.putString(KEY_FEATURE, feature);
                                bundle.putString(KEY_BUDGET, budget);

                                SharedPreferences.Editor editor = preferences.edit();
                                if (area != null)
                                    editor.putString(KEY_AREA, area);
                                if (bedrooms != null)
                                    editor.putString(KEY_BEDROOMS, bedrooms);
                                if (city != null)
                                    editor.putString(KEY_CITY, city);
                                if (state != null)
                                    editor.putString(KEY_STATE, state);
                                if (feature != null)
                                    editor.putString(KEY_FEATURE, feature);
                                if (budget != null)
                                    editor.putString(KEY_BUDGET, budget);
                                editor.apply();


                                Realm realm = null;
                                try {
                                    realm = Realm.getDefaultInstance();
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            UserMessage message = realm.createObject(UserMessage.class);
                                            message.setMessage(serverResponse);
                                            message.setUserSent(false);
                                        }
                                    });
                                } finally {
                                    if (realm != null)
                                        realm.close();
                                }

                                showServerResponseBubble(serverResponse, bundle); //TODO: set this -> ask for all info, use preferences
                                buttonShowResults.animate().alpha(1.0f).setDuration(700)
                                        .setListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {
                                                buttonShowResults.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {
                                            }
                                        });
                            } else {
                                //TODO: remove this toast
                                Toast.makeText(ChatActivity.this, "status 0", Toast.LENGTH_SHORT).show();
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
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Endpoints.AUTH_TOKEN);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_message", input.toLowerCase());
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        InitApplication.getInstance().addToQueue(request,
                Constants.REQUEST_SEND_SPEECH_INPUT_TO_SERVER);
    }


    //TODO: setup this method on long click or some other event
    private void speakOut(final String textToSpeak) {

        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
        showServerResponseBubble(textToSpeak, null);

    }


    private void showServerResponseBubble(final String serverResponse, Bundle bundle) {

//        String area, bedrooms, city, state, budget, feature;
        String toAsk = "";

        if (bundle != null) {
            //TODO: manipulate all the data that's null and ask from user, if willing to provide
            if (bundle.getString(KEY_FEATURE) == null)
                toAsk += "facilities ";
            if (bundle.getString(KEY_AREA) == null)
                toAsk += "area ";
            if (bundle.getString(KEY_BEDROOMS) == null)
                toAsk += "number ";
            if (bundle.getString(KEY_CITY) == null)
                toAsk += "city ";
            if (bundle.getString(KEY_STATE) == null)
                toAsk += "state ";
            if (bundle.getString(KEY_BUDGET) == null)
                toAsk += "budget ";
        }

        String reply = "You haven't entered " + toAsk.replace(" ", ", ") +
                " \nWould you like to enter these or search right now?";

        TextView serverMessage = new TextView(ChatActivity.this);
        serverMessage.setText(reply);
        serverMessage.setGravity(Gravity.START);
        serverMessage.setTextSize(18);
        serverMessage.setTextColor(getResources().getColor(android.R.color.black));
        LinearLayout.LayoutParams llp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        llp.setMargins(10, 20, 100, 20); // llp.setMargins(left, top, right, bottom);
        serverMessage.setLayoutParams(llp);

        messageView.addView(serverMessage);
    }


    private void showPreviousConversation() {
        if (!preferences.getBoolean(IS_USER_ORDER_COMPLETE, false)) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                RealmResults<UserMessage> messages = realm.where(UserMessage.class).findAll();
                for (int i = 0; i < messages.size(); i++) {
                    if (messages.get(i).isUserSent())
                        showUserInputBubble(messages.get(i).getMessage());
                    else
                        showServerResponseBubble(messages.get(i).getMessage(), null);
                }
            } finally {
                if (realm != null)
                    realm.close();
            }
        } else {
            showServerResponseBubble("Hi! How may I help you?", null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logoutUser();
            case R.id.action_clear_session:
                clearRealmDB();
                buttonShowResults.setVisibility(GONE);
                messageView.removeAllViews();
        }
        return true;
    }


    private void logoutUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser();
        clearRealmDB();
        firebaseAuth.signOut();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_USER_LOGGED_IN, false);
        editor.apply();
        Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ChatActivity.this, LoginActivity.class));
        ChatActivity.this.finish();
    }


    private void clearRealmDB() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<UserMessage> results = realm.where(UserMessage.class).findAll();
                    results.deleteAllFromRealm();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null)
                realm.close();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (tts != null)
            tts.stop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null)
            tts.shutdown();
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                Toast.makeText(ChatActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
            } else
                imagebuttonSpeak.setEnabled(true);

        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }


    @Override
    public void onUtteranceCompleted(String utteranceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imagebuttonSpeak.performClick();
            }
        });
    }

}