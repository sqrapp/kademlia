package com.soriole.dht.kademlia.simulations;

import com.soriole.dht.kademlia.GetParameter;
import com.soriole.dht.kademlia.JKademliaNode;
import com.soriole.dht.kademlia.KademliaApplication;
import com.soriole.dht.kademlia.KademliaStorageEntry;
import com.soriole.dht.kademlia.exceptions.ContentNotFoundException;
import com.soriole.dht.kademlia.node.KademliaId;
import com.soriole.dht.kademlia.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.sound.sampled.Port;

public class BootstrapTest2 {
    private static final Logger logger = LoggerFactory.getLogger(BootstrapTest2.class);

    public static void main(String[] args) {
        try {

            Node boostrap1 = new Node("12345678901234567890", InetAddress.getByName("playground.sireto.com"), 10210);
            int i = 0;
            int port = 7580;
          
            JKademliaNode kad1 = new JKademliaNode("Bootstrap Tester", new KademliaId(), port);
            logger.info("Bootstrap Tester Id:{}",kad1.getNode().getNodeId().hexRepresentation());
            try {
            kad1.bootstrap(boostrap1);
            do {
            	 List<Node> allNodes = kad1.getRoutingTable().getAllNodes();
                 logger.info("Peers:{}:{}", allNodes.size(), allNodes);
                 
                 for(Node n: allNodes) {
                 	logger.info("peer:{}:ip:{}:port:{}",n.getNodeId(),n.getSocketAddress(),n.getPort());
                 	
                 }
                 
                 Thread.sleep(10000);
                 
            } while (true);
            }catch (Exception e) {
 				e.printStackTrace();
 			}
        
           
          
            

 

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
