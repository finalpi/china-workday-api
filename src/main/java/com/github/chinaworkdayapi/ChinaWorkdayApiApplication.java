package com.github.chinaworkdayapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChinaWorkdayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChinaWorkdayApiApplication.class, args);
    }

}
