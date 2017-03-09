package com.flatmates.ixion.model;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by daman on 8/3/17.
 */

public class Data extends RealmObject {

    String lat;
    String lon;

    public Data(){
    }

    public Data(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

}
