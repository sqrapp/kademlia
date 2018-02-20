package com.soriole.dht;

import com.soriole.dht.kademlia.JKademliaNode;
import com.soriole.dht.kademlia.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class KademliaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KademliaApplication.class, args);
    }

}
