package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ComponentScanApplication.class})
public class ComponentScanApplicationTest {
    @Test
    public void contextLoads() {
        System.out.println("\nHello, this time the list of items of \"elements\" attribute should contain default values.\n");
    }
}
