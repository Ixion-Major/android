package com.flatmates.ixion.model;

import io.realm.RealmObject;

/**
 * Created by gurpreet on 19/02/17.
 */

public class UserMessage extends RealmObject {

    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
