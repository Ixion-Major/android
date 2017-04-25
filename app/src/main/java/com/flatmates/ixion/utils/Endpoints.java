package com.flatmates.ixion.utils;

/**
 * Created by gurpreet on 19/02/17.
 */

public class Endpoints {

    //https://ixion-backend-python1049.herokuapp.com
    //http://778a8ad9.ngrok.io

    private static final String BASE_URL = "https://ixion-backend-python1049.herokuapp.com";
    public static final String AUTH_TOKEN = "jbfsj32094bsjab0*)&*)&)*&3bkdsjs&*&kbdk";
    public static final String SEARCH_TOKEN = "jdf34bj#*&$bcj[kbb>?H)U(#*)QBjbsf&*372";
    private static final String NGROK_BASE_URL = "https://4f72eaf8.ngrok.io";    //TODO: set


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

    public static String endpointContractRegister(String sha256) {
        return "https://proofofexistence.com/api/v1/register?d=" + sha256;
    }

    //TODO: store all contract SHAs
    public static String endpointContractStatus(String sha256) {
        return "https://proofofexistence.com/api/v1/status?d=" + sha256;
    }


    public static String endpointOBSearch() {
        return "https://ixion-search.herokuapp.com/v1/ob/search";
    }


    public static String endpointCreateListing() {
        return NGROK_BASE_URL + "/api/v1/contracts";
    }


    public static String endpointFetchImage(String guid, String hash) {
        return NGROK_BASE_URL + "/api/v1/get_image?guid=" + guid + "&hash=" + hash;
    }

    public static String endpointPurchaseContract() {
        return NGROK_BASE_URL + "/api/v1/purchase_contract";
    }

}
