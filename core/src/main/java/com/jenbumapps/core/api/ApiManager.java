package com.jenbumapps.core.api;

import com.jenbumapps.core.utility.connect.NetworkService;

public class ApiManager {

    // EPassApi
    public static EPassApi form() {
        return NetworkService.get().create(EPassApi.class);
    }

    // EPassTermApi
    public static EPassTermApi term() {
        return NetworkService.get().create(EPassTermApi.class);
    }

    // GlobalParamApi
    public static GlobalParamApi globalParam() {
        return NetworkService.get().create(GlobalParamApi.class);
    }

}
