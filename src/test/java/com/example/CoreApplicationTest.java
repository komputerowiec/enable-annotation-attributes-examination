package com.example;

import com.example.configuration.SharedConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SharedConfiguration.class})
public class CoreApplicationTest {

    @Test
    public void contextLoads() {
        System.out.println("\nHello world from CoreApplication test !!!!!!!!!!!!!!!!!\n");
    }
}