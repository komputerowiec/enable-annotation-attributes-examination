package com.example;

import com.example.configuration.SharedConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CoreApplication {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SharedConfiguration.class);

        System.out.println("\n\nHello world from CoreApplication !!!!!!!!!!!!!!!!!\n\n");

        ((AnnotationConfigApplicationContext) ctx).close();
    }
}
