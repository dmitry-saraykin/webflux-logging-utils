package ru.roborox.logging.utils;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

import java.util.function.Function;

import reactor.core.publisher.Mono;

public class LoggingUtils {
    public static final String ACTION_FIELD = "action";
    public static final String ACTION_HEADER = "X-ACTION-ID";

    public static <T> Mono<T> withAction(Function<String, Mono<T>> action) {
        return Mono.subscriberContext().flatMap(context -> {
            return action.apply(context.get(ACTION_HEADER));
        });
    }
    
    public static <T> Mono<T> withMarker(Function<LogstashMarker, Mono<T>> action) {
        return Mono.subscriberContext().flatMap(context -> {
            return action.apply(Markers.append(ACTION_FIELD, context.get(ACTION_HEADER)));
        });
    }
}
