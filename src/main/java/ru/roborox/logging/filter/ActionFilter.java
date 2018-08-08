package ru.roborox.logging.filter;

import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.util.UUID;

import reactor.core.publisher.Mono;
import ru.roborox.logging.utils.LoggingUtils;

public class ActionFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String action = getAction(exchange);
        return chain.filter(exchange)
                .subscriberContext(context -> context.put(LoggingUtils.ACTION_HEADER, action));
    }

    private String getAction(ServerWebExchange exchange) {
        String action = exchange.getRequest().getHeaders().getFirst(LoggingUtils.ACTION_HEADER);
        if (StringUtils.isEmpty(action)) {
            action = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return action;
    }

}
