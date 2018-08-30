package com.example;

import com.example.configuration.SharedConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SharedConfiguration.class})
public class PointedConfigurationApplicationTest {
    @Test
    public void contextLoads() {
        System.out.println("Hello, this time the list of items of the \"elements\" attribute should be empty !!!\n");
    }
}
