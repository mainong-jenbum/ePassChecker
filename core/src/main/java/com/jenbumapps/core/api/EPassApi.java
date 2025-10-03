package com.jenbumapps.core.api;

import com.jenbumapps.core.model.EPass;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EPassApi {
    // Create e-pass
    @POST("/e-pass/add")
    Call<Void> create(@Body EPass t);

    @GET("/e-pass/all")
    Call<List<EPass>> fetchAll();
    
    @GET("/e-pass/id/{id}")
    Call<EPass> fetchById(@Path("id") int id);

    @GET("/e-pass/applicant_contact/{phone}")
    Call<List<EPass>> fetchByApplicantContact(@Path("phone") long phone);

    @PUT("/e-pass/update-qr/{qr-url}/{id}")
    Call<String> updateQRCode(@Path("qr-url") String qrUrl, @Path("id") int id);

    @DELETE("/e-pass/delete/{id}")
    Call<Void> delete(@Path("id") int couponId);
}