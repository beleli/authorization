package br.com.blitech.authorization.api.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number (0..N)", schema = @Schema(type = "integer", defaultValue = "0"))
@Parameter(in = ParameterIn.QUERY, name = "size", description = "The size of the page", schema = @Schema(type = "integer", defaultValue = "10"))
@Parameter(in = ParameterIn.QUERY, name = "sort", description = "Sort: property(asc|desc)", examples = { @ExampleObject("name"), @ExampleObject("name,asc") })
public @interface PageableParameter { }
