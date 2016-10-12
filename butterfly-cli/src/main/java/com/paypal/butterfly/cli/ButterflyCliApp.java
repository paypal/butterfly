package com.paypal.butterfly.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * Butterfly Command Line Interface application
 *
 * @author facarvalho
 */
@SpringBootApplication
public class ButterflyCliApp {

    public static void main(String... arguments) throws IOException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ButterflyCliApp.class, arguments);
        ButterflyCli butterflyCli = applicationContext.getBean(ButterflyCli.class);
        int status = butterflyCli.run(arguments);

        System.exit(status);
    }

}