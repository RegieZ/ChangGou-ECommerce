package com.changgou.business.listener;

import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "ad_update_queue")
public class AdListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RabbitHandler
    public void receiveMsg(String msg) {
        logger.info("接收到广告更新位置消息： {}", msg);
        if (StringUtils.isNotEmpty(msg)) {
            String url = "http://192.168.200.128/ad_update?position=" + msg;
            //构建okhttpclient
            OkHttpClient okHttpClient = new OkHttpClient();
            //构建request请求对象
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                //调用失败
                @Override
                public void onFailure(Call call, IOException e) {
                    logger.error("大广告更新调用失败： {}", e.getMessage());
                }

                //调用成功
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    logger.info("大广告更新调用成功： {}", response.message());
                }
            });

        }
    }
}