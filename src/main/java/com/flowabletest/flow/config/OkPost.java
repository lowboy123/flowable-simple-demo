package com.flowabletest.flow.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkPost {

    private static Logger logger = LoggerFactory.getLogger(OkPost.class);

    public static void postUseOkhttp(String url, String requestBody){
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, requestBody))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(5000, TimeUnit.MILLISECONDS).readTimeout(6000,TimeUnit.MILLISECONDS).build();
        // execute还是会超时
        /*try {
            Response response = okHttpClient.newCall(request).execute();
//            result = response.body().string();
        } catch (IOException e) {
            LOGGER.info(">>>>>>>>>>>>>>>>post请求发生错误,错误信息:{}",e);
        }*/
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.error(">>>>>>>>>>>>>>>>请求发生错误,错误信息:{}",e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.info(response.protocol() + " " +response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    logger.error( headers.name(i) + ":" + headers.value(i));
                }
                logger.error( "onResponse: " + response.body().string());
            }
        });
    }
}
