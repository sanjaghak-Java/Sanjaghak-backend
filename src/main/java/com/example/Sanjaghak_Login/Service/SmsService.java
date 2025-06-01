package com.example.Sanjaghak_Login.Service;


import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SmsService {

    @Value("${sms.api.key}")
    private String apiKey;

    public void sendVerificationCode(String mobile, String code) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String jsonBody = String.format(
                "{\n" +
                        "    \"lineNumber\": 30002108002345,\n" +
                        "    \"messageTexts\": [\n" +
                        "        \"کد تایید شما برای ورود به سنجاقک  : %s\"\n" +
                        "    ],\n" +
                        "    \"mobiles\": [\n" +
                        "        \"%s\"\n" +
                        "    ],\n" +
                        "    \"sendDateTime\": null\n" +
                        "}", code, mobile
        );

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url("https://api.sms.ir/v1/send/likeToLike")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "text/plain")
                .addHeader("X-API-KEY", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            System.out.println("📨 پاسخ SMS.ir: " + responseBody);

            if (!response.isSuccessful()) {
                throw new RuntimeException("❌ ارسال پیامک ناموفق بود: " + responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ خطا در ارتباط با سرور پیامک", e);
        }
    }

}
