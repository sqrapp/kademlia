package com.soriole.dht;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KademliaSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(KademliaSpringApplication.class, args);
    }

}
