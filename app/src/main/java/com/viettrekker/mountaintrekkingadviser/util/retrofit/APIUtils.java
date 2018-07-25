package com.viettrekker.mountaintrekkingadviser.util.retrofit;

import com.viettrekker.mountaintrekkingadviser.Constant;

public class APIUtils {

    private APIUtils() {
    }

    public static APIService getWebService() {
        return new RetrofitClient().getClient(Constant.BASE_URL_API).create(APIService.class);
    }
}
