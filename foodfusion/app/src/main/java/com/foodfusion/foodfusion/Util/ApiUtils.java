package com.foodfusion.foodfusion.Util;

import com.foodfusion.foodfusion.Network.RetrofitClient;

/**
 * Created by Rameez on 2/27/2018.
 */

public class ApiUtils {
    private ApiUtils() {}

    public static final String BASE_URL = "http://foodfusion.com/services/";

    public static RetrofitClient.APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(RetrofitClient.APIService.class);
    }
}
