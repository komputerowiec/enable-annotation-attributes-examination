package com.example;

import com.example.configuration.SharedConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class ComponentScanApplication {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(ComponentScanApplication.class);

        System.out.println("\nHello, this time the list of items of \"elements\" attribute should contain default values.\n");

        ((AnnotationConfigApplicationContext) ctx).close();
    }

}
