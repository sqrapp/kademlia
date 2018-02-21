package com.soriole.dht.kademlia;

import com.soriole.dht.kademlia.node.KademliaId;
import com.soriole.dht.kademlia.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;


public class TestConnectToSireto {
    private static final Logger logger = LoggerFactory.getLogger(KademliaSpringApplicationTest.class);


    public void testConnection() throws IOException {
        try {
            Node boostrap1 = new Node("29345678901234567890", InetAddress.getByName("playground.sireto.com"), 10210);
            int i = 0;
            int port = 7576;
            while (i < 20) {
                JKademliaNode kad1 = new JKademliaNode("Bootstrap Tester", new KademliaId(), port);
                logger.info("Bootstrap Tester Id:{}", kad1.getLocalNode().getNodeId().hexRepresentation());
                try {
                    kad1.bootstrap(boostrap1);
                    List allNodes = kad1.getRoutingTable().getAllNodes();
                    logger.info("Peers:{}:{}", allNodes.size(), allNodes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i += 1;
                port += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
