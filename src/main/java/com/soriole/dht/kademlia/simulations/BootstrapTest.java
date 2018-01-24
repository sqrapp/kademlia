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

public class BootstrapTest {
    private static final Logger logger = LoggerFactory.getLogger(BootstrapTest.class);

    public static void main(String[] args) {
        try {

            Node boostrap1 = new Node("10000000000000000001", InetAddress.getByName("127.0.0.1"), 10210);
            JKademliaNode kad1 = new JKademliaNode("Bootstrap Tester", new KademliaId(), 7576);
            logger.info("Bootstrap Tester Id:{}",kad1.getNode().getNodeId().hexRepresentation());
            kad1.bootstrap(boostrap1);

            List allNodes = kad1.getRoutingTable().getAllNodes();
            logger.info("Peers:{}:{}", allNodes.size(), allNodes);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
