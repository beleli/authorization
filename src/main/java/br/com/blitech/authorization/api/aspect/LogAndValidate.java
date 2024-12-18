package br.com.blitech.authorization.api.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAndValidate {
    boolean validateRequest() default true;
    boolean logResponse() default true;
    Class<?>[] validationGroups() default {};
}
