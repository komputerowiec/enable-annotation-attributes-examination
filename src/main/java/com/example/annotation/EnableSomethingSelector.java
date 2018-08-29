package com.example.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;

public class EnableSomethingSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {

        // I print it out just to accent that ImportSelector has been called
        System.out.println("\n\n>>>> selectImports(...) in " + this.getClass().getSimpleName() + " has been called\n");

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(
                        EnableSomething.class.getName(), false));

        // loop over values in "elements" array which is an attribute of our EnableSomething annotation
        System.out.println(">>>> List of items read from elements array:\n");
        Arrays.stream(attributes.getStringArray("elements")).forEach(it -> {
                    System.out.println(">>>> item in elements array: " + it);
        });


        // I should return an array of names of additional configuration classes here,
        // but as this is s fake ImportSelector, I return an empty array
        return new String[0];
    }
}
