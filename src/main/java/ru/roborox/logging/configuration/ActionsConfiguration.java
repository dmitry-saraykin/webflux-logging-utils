package ru.roborox.logging.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.WebFilter;

import ru.roborox.logging.filter.ActionFilter;

@Configuration
public class ActionsConfiguration {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public WebFilter elasticApmFilter() {
        return new ActionFilter();
    }
}
