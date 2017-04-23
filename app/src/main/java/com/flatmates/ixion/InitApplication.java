package com.flatmates.ixion;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.sendbird.android.SendBird;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by gurpreet on 16/02/17.
 */

public class InitApplication extends Application {

    private RequestQueue queue;
    private static InitApplication sInstance;

    private static final String TAG = InitApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        SendBird.init(BuildConfig.SENDBIRD_APP_ID, getApplicationContext());

        //TODO: remove
        Stetho.initializeWithDefaults(this);
//        Stetho.initialize(
//                Stetho.newInitializerBuilder(this)
//                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
//                        .build());

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

    }

    public static synchronized InitApplication getInstance() {
        return sInstance;
    }

    public <T> void addToQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getQueue().add(req);
    }

    public <T> void addToQueue(Request<T> req) {
        req.setTag(TAG);
        getQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (queue != null) {
            queue.cancelAll(tag);
        }
    }

    public RequestQueue getQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(getApplicationContext());
            return queue;
        }
        return queue;
    }

}
