package com.soriole.dht.kademlia;

import com.soriole.dht.kademlia.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.net.InetAddress;
import java.util.List;

@SpringBootApplication
public class KademliaApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(KademliaApplication.class);

    @Value("${kademlia.node.id:12345678901234567890}")
    private String kadId;

    @Value("${kademlia.node.name:boostrap1}")
    private String peerName;

    @Value("${kademlia.node.ip:127.0.0.1}")
    private String peerIp;

    @Value("${kademlia.node.port:13030}")
    private int peerPort;


    public static void main(String[] args) {
        SpringApplication.run(KademliaApplication.class, args);
    }

    //access command line arguments
    @Override
    public void run(String... args) throws Exception {
    	System.out.println(peerPort);
        Node boostrap1 = new Node(kadId, InetAddress.getByName(peerIp), peerPort);
        JKademliaNode bot1 = new JKademliaNode(peerName, boostrap1);
        System.out.println(bot1.getPort());

        do {
            List peers = bot1.getRoutingTable().getAllNodes();
            logger.info("Peers:{}:{}", peers.size(), peers);
            Thread.sleep(5000);
        } while (true);
    }
}
