package com.flatmates.ixion.utils;

/**
 * Created by gurpreet on 19/02/17.
 */

public class Endpoints {

    private static final String BASE_URL = "https://ixion-backend-python1049.herokuapp.com";

    public static String endpointChatbot() {
        return BASE_URL + "/property-bot/chatbot";
    }

}
