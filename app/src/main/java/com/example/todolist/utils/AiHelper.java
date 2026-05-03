package com.example.todolist.utils;

import com.example.todolist.network.GeminiApiService;
import com.example.todolist.network.GeminiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiHelper {

    private static final String API_KEY = "AIzaSyA5B-QTMI0K2Pw37_M-XWdm-mpNKNwPtd4";

    public interface AiPriorityCallback {
        void onResult(int priority);
        void onError(String error);
    }

    public static void detectPriority(String taskTitle, AiPriorityCallback callback) {
        GeminiApiService apiService = GeminiClient.getApiService();


        String prompt = "Give priority number (1, 2, or 3) for: " + taskTitle + ". Return only the number.";

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            JSONArray parts = new JSONArray();

            part.put("text", prompt);
            parts.put(part);
            contents.put(new JSONObject().put("parts", parts));
            jsonBody.put("contents", contents);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonBody.toString());

            apiService.getPriority(API_KEY, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String rawResponse = response.body().string();

                            // --- INI BUAT CEK HASIL API ---
                            android.util.Log.d("CEK_GEMINI", "Respon Berhasil: " + rawResponse);

                            JSONObject json = new JSONObject(rawResponse);
                            String aiText = json.getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text").trim();

                            int priority = Integer.parseInt(aiText.replaceAll("[^0-9]", ""));
                            callback.onResult(priority);
                        } else {
                            // Kalau dapet error (misal 403 atau 400)
                            android.util.Log.e("CEK_GEMINI", "API Error: " + response.code() + " - " + response.message());
                            callback.onResult(2);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("CEK_GEMINI", "Parsing Error: " + e.getMessage());
                        callback.onResult(2);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Kalau nggak konek internet atau URL salah
                    android.util.Log.e("CEK_GEMINI", "Koneksi Gagal: " + t.getMessage());
                    callback.onResult(2);
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}