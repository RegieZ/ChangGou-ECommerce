package com.changgou.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UrlFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("经过第2个过滤器UrlFilter");
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        System.out.println("url:" + url);
        return chain.filter(exchange);
    }

    //1--->/goods/brand
    //2---->/brand
    /*
        配置文件中配置了路由，路由本质也是filter,并且路由filter的优先级是2
        如果自定义的filter优先级>=2,那么先执行路由规则（仅仅是执行路由规则，并不会真正路由服务
        只有所有filter执行完毕后再路由）
        ，再执行自定义的filter，一般建议设置优先级小于2
     */
    @Override
    public int getOrder() {
        return 1;
    }
}