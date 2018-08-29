package com.example.configuration;

import com.example.annotation.EnableSomething;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSomething(elements = {})
public class SharedConfiguration {
}
