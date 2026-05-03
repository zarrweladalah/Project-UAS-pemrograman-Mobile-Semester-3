package com.example.todolist.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {
    @POST("v1beta/models/gemini-pro:generateContent")
    Call<ResponseBody> getPriority(
            @Query("key") String apiKey,
            @Body RequestBody body
    );
}