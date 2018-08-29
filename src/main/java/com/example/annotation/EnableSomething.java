package com.example.annotation;


import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EnableSomethingSelector.class)
public @interface EnableSomething {
    String[] elements() default {"first-default-element", "second-default-element"};
}
