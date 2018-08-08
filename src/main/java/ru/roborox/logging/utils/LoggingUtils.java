package ru.roborox.logging.utils;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.function.Function;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class LoggingUtils {
    public static final String ACTION_FIELD = "action";
    public static final String ACTION_HEADER = "X-ACTION-ID";

    public static <T> Mono<T> withAction(Function<String, Mono<T>> action) {
        return Mono.subscriberContext().flatMap(context -> {
            String actionId = createActionId(context);
            return action.apply(actionId);
        });
    }
    
    public static <T> Flux<T> withActionFlux(Function<String, Flux<T>> action) {
        return Mono.subscriberContext().flatMapMany(context -> {
            String actionId = createActionId(context);
            return action.apply(actionId);
        });
    }

    private static String createActionId(Context context) {
        String actionId = context.getOrDefault(ACTION_HEADER, "");
        if (StringUtils.isEmpty(actionId)) {
            actionId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return actionId;
    }
    
    public static <T> Mono<T> withMarker(Function<LogstashMarker, Mono<T>> action) {
        return Mono.subscriberContext().flatMap(context -> {
            String actionId = createActionId(context);
            return action.apply(Markers.append(ACTION_FIELD, actionId));
        });
    }
    
    public static <T> Flux<T> withMarkerFlux(Function<LogstashMarker, Flux<T>> action) {
        return Mono.subscriberContext().flatMapMany(context -> {
            String actionId = createActionId(context);
            return action.apply(Markers.append(ACTION_FIELD, actionId));
        });
    }
}
