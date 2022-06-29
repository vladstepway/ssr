package ru.croc.ugd.ssr.config;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import ru.reinform.cdp.utils.springboot.cache.HazelcastClientCondition;

public class NotHazelcastClientCondition extends HazelcastClientCondition {
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !super.matches(context, metadata);
    }
}
