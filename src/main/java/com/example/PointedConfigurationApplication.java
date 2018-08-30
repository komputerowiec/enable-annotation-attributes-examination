package com.example;

import com.example.configuration.SharedConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PointedConfigurationApplication {
    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SharedConfiguration.class);

        System.out.println("Hello, this time the list of items of the \"elements\" attribute should be empty !!!\n");

        ((AnnotationConfigApplicationContext) ctx).close();
    }
}
