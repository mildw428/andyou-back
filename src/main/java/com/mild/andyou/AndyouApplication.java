package com.mild.andyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;


@EnableRetry
@SpringBootApplication
public class AndyouApplication {

    public static void main(String[] args) {
        SpringApplication.run(AndyouApplication.class, args);
    }

}
