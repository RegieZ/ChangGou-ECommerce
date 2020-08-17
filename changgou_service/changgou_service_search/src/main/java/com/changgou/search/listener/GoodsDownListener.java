package com.changgou.search.listener;

import com.changgou.search.service.ManagerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "search_del_queue")
public class GoodsDownListener {

    private Logger log = LoggerFactory.getLogger(GoodsDownListener.class);

    @Autowired
    private ManagerService managerService;

    /**
     * 根据spuId下架
     *
     * @param spuId
     */
    @RabbitHandler
    public void receiveMsg(String spuId) {
        log.info("接收到下架的spuId : {}", spuId);
        if (StringUtils.isNotEmpty(spuId)) {
            managerService.deleteBySpuId(spuId);
        }
    }
}