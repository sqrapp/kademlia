package com.soriole.dht.service;

import com.soriole.dht.kademlia.JKademliaNode;
import com.soriole.dht.kademlia.exceptions.RoutingException;
import com.soriole.dht.kademlia.node.KademliaId;
import com.soriole.dht.kademlia.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service("kademliaService")
public class KademliaService {
    private static final Logger logger = LoggerFactory.getLogger(KademliaService.class);

    private JKademliaNode kadNode;
    private boolean isRunning;

    @Value("${kademlia.node.id}")
    private String kadId;

    @Value("${kademlia.node.name}")
    private String nodeName;

    @Value("${kademlia.node.ip:127.0.0.1}")
    private String nodeIp;

    @Value("${kademlia.node.port:13030}")
    private int nodePort;

    @Value("${kademlia.node.bootstrap.address}")
    private String bootstrapNodes;

    @Value("${kademlia.node.bootstrap.id:}")
    private String bootstrapId;

    public KademliaService() {
        if (kadId == null || kadId.isEmpty()) {
            kadId = getRandomNumberString(20);
        }
        if (nodeName == null || nodeName.isEmpty()) {
            nodeName = getRandomString(20);
        }

        isRunning = false;
    }

    @PostConstruct
    public void setupKademliaNode() {
        setupKademliaNode(kadId, nodeIp, nodePort, bootstrapNodes);
    }

    public void setupKademliaNode(String kadId, String nodeIp, int nodePort, String bootNodeAddresses) {
        try {
            Node node = new Node(kadId, InetAddress.getByName(nodeIp), nodePort);
            kadNode = new JKademliaNode(nodeName, node);

            isRunning = kadNode.getServer().isRunning();


            // get list of kademlia id from the  bootstrap ids;
            KademliaId[] bootstrapKadIds={};
            if(bootstrapId!=null){
                String[] bootstrapIds={};
                if(!bootstrapId.isEmpty()){
                    if(bootstrapId.contains(",")){
                        bootstrapIds=bootstrapId.split(",");
                    }
                    else{
                        bootstrapIds=new String[]{bootstrapId};
                    }
                    bootstrapKadIds=new KademliaId[bootstrapIds.length];
                    for(int i=0;i<bootstrapIds.length;i++){
                        bootstrapKadIds[i]=new KademliaId(bootstrapIds[i]);
                    }
                }

            }

            // get list of bootstrap addresses
            String[] bootstrapAddresses = null;
            if(bootNodeAddresses != null) {
                if (bootNodeAddresses.contains(",")) {
                    bootstrapAddresses = bootNodeAddresses.split(",");
                } else {
                    bootstrapAddresses = new String[]{bootNodeAddresses};
                }
            }

            // if the no of ip addresses is not equal to the no of ids, we set all the ids as null
            if(bootstrapAddresses.length!=bootstrapKadIds.length){
                logger.warn("Public key of bootstrap nodes are not configured. Man in the middle attack is possible");
                bootstrapKadIds=new KademliaId[bootNodeAddresses.length()];
            }

            for(int i=0;i<bootstrapAddresses.length;i++){
                String bootstrapAddress=bootstrapAddresses[i];
                if(!bootstrapAddress.contains(":")) continue;
                String[] split = bootstrapAddress.split(":");
                try{
                    Node bootNode = new Node(bootstrapKadIds[i], InetAddress.getByName(split[0]), Integer.parseInt(split[1]));
                    kadNode.bootstrap(bootNode);
                }catch(RoutingException re){
                    logger.error("Could not connect to bootstrap:", re);
                }catch(NumberFormatException nfe){
                    logger.error("Invalid bootstrap port", nfe);
                } catch (UnknownHostException e) {
                    logger.error("Invalid bootstrap host", e);
                }
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRateString = "2000")
    public void printRoutingTable() {
        List peers = kadNode.getRoutingTable().getAllNodes();
        logger.info("Peers:{}:{}", peers.size(), peers);
    }

    public JKademliaNode getKadNode() {
        return kadNode;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i <= length / 32; i++) {
            buffer.append(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        return buffer.substring(0, length);
    }

    private String getRandomNumberString(int length) {
        Random random = new SecureRandom();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i <= length; i++) {
            buffer.append(random.nextInt(10));
        }
        return buffer.toString();
    }

}
