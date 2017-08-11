package com.testapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@org.springframework.boot.autoconfigure.EnableAutoConfiguration
@ComponentScan
@SuppressWarnings
@MyAnnotation
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class);
    }

}
