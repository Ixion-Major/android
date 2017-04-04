package com.flatmates.ixion.utils;

/**
 * Created by gurpreet on 19/02/17.
 */

public class Endpoints {

    private static final String BASE_URL = "https://ixion-backend-python1049.herokuapp.com";
    public static final String AUTH_TOKEN = "jbfsj32094bsjab0*)&*)&)*&3bkdsjs&*&kbdk";


    public static String endpointChatbot() {
        return BASE_URL + "/property-bot/chatbot";
    }

    public static String endpointFetchBedrooms() {
        return BASE_URL + "/property-bot/fetch-bedrooms";
    }

    public static String endpointFetchCity() {
        return BASE_URL + "/property-bot/fetch-city";
    }

    public static String endpointFetchArea() {
        return BASE_URL + "/property-bot/fetch-area";
    }

    public static String endpointFetchState() {
        return BASE_URL + "/property-bot/fetch-state";
    }

    public static String endpointFetchBudget() {
        return BASE_URL + "/property-bot/fetch-budget";
    }

    public String endpointFetchFeature() {
        return BASE_URL + "/property-bot/fetch-feature";
    }

}
