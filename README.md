## Quick start

There are a few special annotations in *Spring Framework* which begin with `@Enable...` prefix.
Working on my own annotation of that type, I came across one counter-intuitive feature. 
Sometimes, the values of the attributes passed to an `@Enable...` like annotation differ from the ones obtained by the code which is intended to process them. The goal of this project is to provide a simple codebase which shows this situation in practice.  

To achieve that goal, this codebase introduces the implementation of an exemplary [`@EnableSomething`](src/main/com/example/annotation/EnableSomething.java) annotation. One of the ways to come up with a custom  `@Enamble...` annotation is to provide a class which implements the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface. This project shows that exactly the same attributes passed to the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation can be perceived differently even by the same implementation of the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface. In the provided examples, the actual values of these attributes, as they are seen by the class which implements the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface, depend on the fact if the configuration which incorporates the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation has been picked up by the `@ComponentScan` mechanism or if it has been chosen manually (e.g. with the help of an `AnnotationConfigApplicationContext` class constructor parameter). As we will see, the mentioned behaviour takes place when two conditions are met. First, the `@Enable...` annotation has to be defined together with an attribute which is an array (of whatever) and the attribute is assigned a default value of a not empty array. Second, in the place where the annotation is actually applied, the actual attribute has to be passed explicitly and has to be set to an empty array.

Each executable and test launched in the below procedure reports its view of the `elements` attribute of the same `@EnableSometning` annotation used on the same `@Configuration` class:

```java
@Configuration
@EnableSomething(elements = {})
public class SharedConfiguration {
}
```

Note that the above [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation usage, provides the empty array as the `elements` attribute value, while the below definition of the same annotation, declares the default values for the same `elements` attribute. 


```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EnableSomethingSelector.class)
public @interface EnableSomething {
    String[] elements() default {"first-default-element", "second-default-element"};
}
```

Referred in the above definition, the `EnableSomethingSelector` class implements the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface. In our simple example, all functionality of this class boils down to a peace of a code which prints out all items of the `elements` array. This array contains the values which according to the internal *Spring Framework* mechanisms where passed to the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation through the attribute of the same name (i.e. `elements` attribute).

The code snippet which does this job is here:

```java
System.out.println(">>>> List of items read from elements array:\n");
Arrays.stream(attributes.getStringArray("elements")).forEach(it -> {
            System.out.println(">>>> item in elements array: " + it);
});
```

To observe quickly the aforementioned phenomenon, proceed the below procedure (the more detailed analyses sits in further sections of this description).


###### Requirements

To go through the below procedure you need these components to be pre-installed on your computer:
* git
* JDK 1.8+
* Maven 3.3+

###### 1. Clone the source code.

```bash
git clone git@github.com:komputerowiec/enable-annotation-attributes-examination.git 
```

###### 2. Package the project (omitting test execution for now)

```bash
cd enable-annotation-attributes-examination
mvn package -DskipTests
```

###### 3. Execute the `PointedConfigurationApplication` executable class. 

In this executable, the configuration class is pointed out manually through the `AnnotationConfigApplicationContext` class constructor.

__WINDOWS__
 ```bash
java -cp "logback;target/demo.jar" com.example.PointedConfigurationApplication
```
__LINUX__, __MAC__
 ```bash
java -cp "logback:target/demo.jar" com.example.PointedConfigurationApplication
```
__NOTE__: In JRE 9+, the CGLIB library spits out warnings which can obscure the readability of the actual output. To suppress this warning add `--illegal-access=deny` option to the `java` command invocation. 

This execution should end up with this output:

```
>>>> selectImports(...) called

>>>> List of items read from elements array:

Hello, this time the list of items of the "elements" attribute should be empty !!!
```

According to the above report, the `elements` attribute doesn't contain any items, so it is an empty array, and it matches the explicitly passed value (see the above `SharedConfiguration` configuration class and the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation which decorates this class).

###### 4. Execute the `ComponentScanApplication` executable class.

This time let's launch executable which uses `@ComponentScan` to discover the configuration file (of course eventually it is the same configuration as the one used in the previous step).

__WINDOWS__
 ```bash
java -cp "logback;target/demo.jar" com.example.ComponentScanApplication
```
__LINUX__, __MAC__
 ```bash
java -cp "logback:target/demo.jar" com.example.ComponentScanApplication
```

This time the outcome should look slightly different:

```
>>>> selectImports(...) called

>>>> List of items read from elements array:

>>>> item in elements array: first-default-element
>>>> item in elements array: second-default-element

Hello, this time the list of items of "elements" array should contain default values.
```

Even though, the same `SharedConfiguration` configuration class was used in both above examples, the second execution reveals that this time the explicitly used annotation attribute has been ignored and the default values have been used instead. 

###### 5. Execute `PointedConfigurationApplicationTest` test.

The tests included in this project are not tests in the strict sense of the word. They are not designed to pass or fail depending on the defined assertions. They are designed to show off that the presented counter-intuitive behaviour is repeatable in jUnit testing environment as well.

First, let's execute the test in which the `SharedConfiguration` class is pointed "manually" with the help of the `@ContextConfiguration(classes = {SharedConfiguration.class})` annotation:

```bash
mvn -Pnolog test -Dtest=PointedConfigurationApplicationTest
```

__NOTE__: In JRE 9+, the CGLIB library spits out warnings which can spoil the readability of the actual output. To suppress this warning add `--DargLine="--illegal-access=deny""` option to the `mvn` command invocation.

Among other maven related messages, the following output should appear in a terminal:

```
Running com.example.PointedConfigurationApplicationTest

>>>> selectImports(...) called

>>>> List of items read from elements array:

Hello, this time the list of items of the "elements" attribute should be empty !!!
```

Because in this test, the configuration was pointed "manually", the value of  the `elements` attribute matches the one specified explicitly with the `SharedConfiguration` configuration class (it is an empty array of Strings).

###### 6. Execute `ComponentScanApplicationTest` test.

Finally, let's launch the test which finds the `SharedConfiguration` class with the help of `@ComponentScan` mechanism. To accomplish it, the `ComponentScanApplicationTest` test class is annotated with the `@ContextConfiguration(classes = {ComponentScanApplication.class})` annotation, and then the referred `ComponentScanApplication` class is annotated with `@ComponentScan`.

```bash
mvn -Pnolog test -Dtest=ComponentScanApplicationTest
```

Within the messages printed by maven itself, you should find the ones printed by the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) implementation class:

```
Running com.example.ComponentScanApplicationTest

>>>> selectImports(...) called

>>>> List of items read from elements array:

>>>> item in elements array: first-default-element
>>>> item in elements array: second-default-element

Hello, this time the list of items of "elements" attribute should contain default values.
```

According to the above output, this time the `elements` attribute found out by [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) consists of default elements. It can be a little counter-intuitive, because the `SharedConfiguration` configuration class explicitly sets this attribute to an empty array.

## Theoretical description

The "quick start" section has just finished up. From now on, the same ideas are presented once again, but in a little bit more deep way.

The *Spring Framework* provides developers with a mechanism to write java annotations which can be used by other developers to get an easy access to the whole subsystems of a specific functionality. In *Spring Framework* itself, this mechanism is used to provide us with annotations like [`@EnableWebMvc`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/web/servlet/config/annotation/EnableWebMvc.html), [`@EnableScheduling`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/scheduling/annotation/EnableScheduling.html), [`@EnableCaching`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/cache/annotation/EnableCaching.html) and so on.

The specific `@Enable...` annotations can take in attributes which configure the relevant subsystems. However, depending on the fact if given `@Enable...` annotation has been picked up by the [`@ComponentScan`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ComponentScan.html) mechanism or has been grabbed in a more traditional way, as a part of an explicitly pointed context configuration, the same attributes passed to the `@Enable...` annotation, can be seen differently by the internal *Spring Framework* mechanisms. 

One way to introduce a custom `@Enable…` annotation is to define a new `@Enable...` annotation in a usual way and then additionally annotate this declaration with an extra [`@Import`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/Import.html) annotation which should import a [`@Configuration`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html) class which in turn implements the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface. An example of such an `@Enable...` annotation declaration could look like this:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EnableSomethingSelector.class)
public @interface EnableSomething {
   String[] elements() default {"first-default-element", "second-default-element"};
}
```

The above example defines the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation. The definition brings in the attribute named `elements`. The type of the attribute is an array of Strings, and the default array assigned to the `elements` attribute consists of the two String items (`"first-default-element"` and `"second-default-element"`). The fact that the attribute has default value different from an empty array is of the special significance here.

Pay attention to the usage of the [`@Import`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/Import.html) meta annotation and to the attribute passed to it. The attribute is the `EnableSomethingSelector` class and the assumption is that it implements the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface.:


```java
public class EnableSomethingSelector implements ImportSelector {

   @Override
   public String[] selectImports(AnnotationMetadata annotationMetadata) {

    /*
     * ACTUAL CODE WOULD GO HERE
     */

   }
}
```

The [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface contract says that the `selectImports(...)` method should return the names of the [`@Configuration`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html) classes which further are processed as common [`@Configuration`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html) classes (that is they provide declarations of the beans).

An essential part of the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface is the parameter of the `selectImports(AnnotationMetadata annotationMetadata)` method. The method parameter of the [`AnnotationMetadata`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/type/AnnotationMetadata.html) type, gives an access to the attributes passed to the `@Enable…` annotation. Thanks to that, it is relatively easy to return different sets of the [`@Configuration`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html) classes depending on the values of these attributes.

For instance, to get access to the `elements` attribute of above the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation, the related `selectImports(AnnotationMetadata annotationMetadata)` method in the `EnableSomethingSelector` class should contain the below snippet of the code (don’t bother about auxiliary methods in [`AnnotationAttributes`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/annotation/AnnotationAttributes.html) class, they simply facilitate an access to the annotation attributes):

```java
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
               annotationMetadata.getAnnotationAttributes(
                       EnableSomething.class.getName(), false));
        
        String[] elementsAttributeValue = attributes.getStringArray("elements");
    
        /*
         * MORE, ACTUAL CODE GOES HERE
         */
        
    }
```

Because, the `elements` attribute of the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation has been defined with the default value in place, therefore if you use the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation and don't provide the attribute explicitly, the default one is used.

It means that if someone uses the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation like this:

```java
@Configuration
@EnableSomething
public class SomeConfiguration {
}
```

the value of the `elements` attribute obtained in the `selectImports(...)` method with the help of [`AnnotationMetadata`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/type/AnnotationMetadata.html) will be the array of default values:

```java
{"first-default-element", "second-default-element"}
```

Similarly, if someone provides the elements attribute explicitly:

```java
@Configuration
@EnableSomething(elements = {"one", "two", "three"})
public class SomeConfiguration {
}
```

the value of the `elements` attribute acquired via [`AnnotationMetadata`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/type/AnnotationMetadata.html) in the `selectImports(...)` method will be, as expected, the three item array:

```java
{"one", "two", "tree"}
```

Now, let's look over another usage of the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation:

```java
@Configuration
@EnableSomething(elements = {})
public class SomeConfiguration {
}
```

This time, the `elements` attribute has been passed explicitly as an empty array of strings.

However, assuming that the [`@ComponentScan`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ComponentScan.html) mechanism was used to pick up this configuration, the value of the `elements` attribute obtained in the aforementioned `selectImports(...)` method with the help of [`AnnotationMetadata`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/type/AnnotationMetadata.html) is not an empty array, but an array of default values as they are declared together with [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation definition:

```java
{"first-default-element", "second-default-element"}
```


On the other hand, in scenarios where [`@ComponentScan`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ComponentScan.html) is not used, the same attribute obtained in the `selectImports(...)` method  from [`AnnotationMetadata`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/type/AnnotationMetadata.html) will be an empty array:

```java
{}
```

## Codebase examination

This simple project provides the definition of an imaginary [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation and the associated `EnableSomethingSelector` class which implements the the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface. 

Actually, the `selectImports(...)` method of the `EnableSomethingSelector` class does nothing but prints out information that the *Spring Framework* called it, and then obtains and prints out the value of the `elements` attribute of the involved [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation.

Both, definition of the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation and the related implementation of the [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) interface are here:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EnableSomethingSelector.class)
public @interface EnableSomething {
    String[] elements() default {"first-default-element", "second-default-element"};
}
```

```java
public class EnableSomethingSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {

        // I print it out just to accent that ImportSelector has been called
        System.out.println("\n>>>> selectImports(...) called\n");

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(
                        EnableSomething.class.getName(), false));

        // loop over values in "elements" array
        System.out.println(">>>> List of items read from elements array:\n");
        Arrays.stream(attributes.getStringArray("elements")).forEach(it -> {
                    System.out.println(">>>> item in elements array: " + it);
        });

        // I should return an array of names of additional configuration classes here,
        // but as this is a fake ImportSelector, I return an empty array
        return new String[0];
    }
}
```

There is only one configuration class decorated with the `@Configuration` annotation, and it is the `SharedConfiguration` class. At the same time, this class is annotated with the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation which takes in the explicitly defined `elements` attribute. The value of this attribute, deliberately is set to an empty array, because this is the case when the value of the attribute seen by the provided [`ImportSelector`](https://docs.spring.io/spring/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/annotation/ImportSelector.html) will differ depending on using or not using the `@ComponentScan` mechanism. The `SharedConfiguration` class is shared between all examples which comprise this codebase.

The whole definition of the `SharedConfiguration` class is as easy as this:

```java
@Configuration
@EnableSomething(elements = {})
public class SharedConfiguration {
}
``` 

Apart from the already introduced classes and annotations, there are two simple executable classes and two simple jUnit tests. These peaces of code are meant to tie things together and to demonstrate how the `elements` attribute of the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation applied to the `SharedConfiguration` class is seen differently in different usage scenarios (with and without `@ComponentScan`).

## Codebase usage

In this section, the way of compilation and execution of the example executables and jUnit tests is described. The short analysis of the individual examples is also provided.

### Requirements

The environment required to go through the ongoing steps (no changes in respect to Quick start section):

* git (optionally to clone the codebase)
* JDK 1.8+
* Maven 3.3+

### Clone

```bash
git clone git@github.com:komputerowiec/enable-annotation-attributes-examination.git
```

### Build

```bash
cd enable-annotation-attributes-examination
mvn package -DskipTests
```

### Example 1: Run and analyse case with manually pointed configuration

First, let's analyze the `PointedConfigurationApplication` executable class in which I "manually" point to the `SharedConfiguration` class:

```java
public class PointedConfigurationApplication {
    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SharedConfiguration.class);

        System.out.println("Hello, this time the list of items of the \"elements\" attribute should be empty !!!\n");

        ((AnnotationConfigApplicationContext) ctx).close();
    }
}
```

The explicit reference to the `SharedConfiguration` class is in the first statement of the `main(...)` function. To execute this peace of code, use this command:

__WINDOWS__
 ```bash
java -cp "logback;target/demo.jar" com.example.PointedConfigurationApplication
```
__LINUX__, __MAC__
 ```bash
java -cp "logback:target/demo.jar" com.example.PointedConfigurationApplication
```

When you run this command, the *SPRING FRAMEWORK* will analyse the referred `SharedConfiguration` class. This configuration class and the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation which sits on the top of this class have already been presented above. From this description we know that the framework to resolve all configuration dependencies will have to call the `selectImports(...)` method of the `EnableSomethingSelector` class.

The messages printed out by this class look like this:

```
>>>> selectImports(...) called

>>>> List of items read from elements array:
```

At least for me, it stays in line with expectations. Because the relevant [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation has been used with the explicitly passed `elements` attribute, and the attribute has been declared as an empty array, the printed list of items of this array is simply empty.


### Example 2: Run and analyse the case with configuration found by `@ComponentScan`

The `ComponentScanApplication` class is an executable in which the configuration classes are being searched by the `@ComponentsScan` mechanism:

```java
@Configuration
@ComponentScan
public class ComponentScanApplication {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(ComponentScanApplication.class);

        System.out.println("\nHello, this time the list of items of \"elements\" attribute should contain default values.\n");

        ((AnnotationConfigApplicationContext) ctx).close();
    }

}
```

The important detail is that the above executable class is itself decorated with two annotations: `@Configuration` and `@ComponentScan`. Thanks to that, the same class provides both, the application entry point (the `main(...)` method) and the Spring like application context configuration. Therefore, the configuration class passed to the constructor of `AnnotationConfigApplicationContext`, is actually the same class which embraces the `main(...)` method (the class which executes the code refers here to itself). Meaningful is also the second used annotation, the `@ComponentScan` annotation tells to the *Spring Framework* that actually all the other `@Configuration` classes should be discovered automatically. As this annotation has no additional attributes, the default package from which the search will begin, is the current package (that is `com.example` in this case). Obviously, the auto discovery mechanism will find the `SharedConfiguration` configuration class (the same we have used "manually" in the previous step).

As we have already seen, when the `SharedConfiguration` class is being processed by the framework, eventfully the `selectImports(...)` method of the `EnableSomethingSelector` class is executed. However, even though within this class we enquire exactly the same attributes of the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation as in the previous examples, this time the `elements` attribute appears to have a different value.

This time, the output printed out by the `selectImports(...)` method reveals that there are two items in the `elements` array:

```
>>>> selectImports(...) called

>>>> List of items read from elements array:

>>>> item in elements array: first-default-element
>>>> item in elements array: second-default-element
```

It differs slightly from what I expected to see. It seems that the value explicitly assigned to the attribute of the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation, on the top of the `SharedConfiguration` class, has been ignored and the default value of the `elements` attribute has been used instead.

Of course, the presented scenario is a kind of an edge case scenario. Evidently, when the `@ComonentScan` mechanism processes the values of the attributes of auto discovered annotations, it equates an empty array with no array at all, so that it takes into account the default array (when there is no array then it takes the default one, it is the role of the default values after all).

### Example 3: Run and analyse test with the manually pointed configuration

In the jUnit test of the Spring based application, more often than not, we use `SpringRunner` to build the application context. On of ways to provide the `SpringRunner` with the context configuration is to use another `@ContextConfiguration` annotation which takes in the array of the `@Configuraion` classes to be used to build the application context. 

The purpose of this example is to show that if `@ContextConfiguraion` in the jUnit test refers to the same `SharedConfiguration` as it was in the two previous examples, then the value of the `elements` attribute established within the `selectImports(...)` method of the `EnableSomethingSelector` class will be the same as in *Exmaple 1* (the explicitly used, empty array will be recognized).

The actual test case and its configuration looks simple:

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SharedConfiguration.class})
public class PointedConfigurationApplicationTest {
    @Test
    public void contextLoads() {
        System.out.println("Hello, this time the list of items of the \"elements\" attribute should be empty !!!\n");
    }
}
```

To execute this test, type this command:

```bash
mvn -Pnolog test -Dtest=PointedConfigurationApplicationTest
```

Among other messages thrown out by the maven tool, the following report, printed by the `EnableSomethingSelector` class, should pop up:

```
Running com.example.PointedConfigurationApplicationTest

>>>> selectImports(...) called

>>>> List of items read from elements array:
```

This output shows no items in the `elements` array. It means that similarly like in *Example 1*, the explicitly declared empty array, has been recognized as the value of the attribute of the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation.

### Example 4: Run and analyse test with the configuration discovered by `@ComponentScan`

The last study case is about the jUnit test which takes advantage of `@ComponentScan` to discover the `@Configuration` classes. The source code of the jUnit test is very similar to the one from the previous example. The main difference is the swap of the configuration class which is used with `@ContextConfiguration` to configure the application context:

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ComponentScanApplication.class})
public class ComponentScanApplicationTest {
    @Test
    public void contextLoads() {
        System.out.println("\nHello, this time the list of items of \"elements\" attribute should contain default values.\n");
    }
}
```

The `ComponentScanApplication` class, used above to configure the application context, is itself annotated with the `@ComponentScan` annotation, which results in an automatic discovery of the `SharedConfiguration` configuration (all the time, it is the same configuration class as in the previous examples). We already know that when this configuration class is being resolved, the control flow gets to the `selectImports(...)` method of the `EnableSomethingSelector` class, and that in our slightly contrived examples, this method prints out the value of the `elements` attribute of the [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation.

To execute this test, type in this command:

```bash
mvn -Pnolog test -Dtest=ComponentScanApplicationTest
```

So, this time the message printed from thr `selectImports(...)` method looks this way (this message is to be found among others, maven produced messages):

```
>>>> selectImports(...) called

>>>> List of items read from elements array:

>>>> item in elements array: first-default-element
>>>> item in elements array: second-default-element
```

As we can tell from that outcome, in this case, the value of thr `elements` attribute established within the `selectImports(...)` method consists of the two default items (default according to [`@EnableSomething`](src/com/example/annotation/EnableSomething) annotation definition). Once more, it can be confusing, because in the `SharedConfiguration` class definition, this attribute is being assigned the explicit value of an empty array.  It means that likewise within the main application, within the jUnit tests, the value of the `elements` attribute can depend on the fact if the configuration of the application context was found with `@ComponentScan` or was pointed "manually".
