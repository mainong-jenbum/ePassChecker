package com.jenbumapps.core.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EPassTermApi {

    @GET("/terms-conditions/all")
    Call<List<String>> fetchAll();

}