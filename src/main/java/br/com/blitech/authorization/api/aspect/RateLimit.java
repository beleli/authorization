package br.com.blitech.authorization.api.aspect;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int maxRequests() default 60;
    long timeValue() default 5;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
