package ru.roborox.logging.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import reactor.core.publisher.Mono;
import ru.roborox.logging.utils.LoggingUtils;

public class LoggingExceptionHandler implements WebExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        return LoggingUtils.withMarker(marker -> {
            if (ex instanceof ResponseStatusException) {
                HttpStatus status = ((ResponseStatusException) ex).getStatus();
                if (exchange.getResponse().setStatusCode(status)) {
                    if (status.is5xxServerError() || status == HttpStatus.NOT_FOUND) {
                        logger.error(marker, buildMessage(exchange.getRequest(), ex));
                    } else if (status == HttpStatus.BAD_REQUEST) {
                        logger.warn(marker, buildMessage(exchange.getRequest(), ex));
                    } else {
                        logger.trace(marker, buildMessage(exchange.getRequest(), ex));
                    }
                    return exchange.getResponse().setComplete();
                }
            } else {
                if (exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)) {
                    logger.error(marker, buildMessage(exchange.getRequest(), ex));
                    return exchange.getResponse().setComplete();
                }
            }
            return Mono.error(ex);
        });
    }

    private String buildMessage(ServerHttpRequest request, Throwable ex) {
        return "Failed to handle request [" + request.getMethodValue() + " "
                + request.getURI() + "]: " + ex.getMessage();
    }
}
