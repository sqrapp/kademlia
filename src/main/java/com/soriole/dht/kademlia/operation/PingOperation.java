/**
 * Implementation of the Kademlia Ping operation,
 * This is on hold at the moment since I'm not sure if we'll use ping given the improvements mentioned in the paper.
 *
 * @author Joshua Kissoon
 * @since 20140218
 */
package com.soriole.dht.kademlia.operation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import com.soriole.dht.kademlia.KadServer;
import com.soriole.dht.kademlia.KademliaNode;
import com.soriole.dht.kademlia.exceptions.RoutingException;
import com.soriole.dht.kademlia.message.Message;
import com.soriole.dht.kademlia.message.PingMessage;
import com.soriole.dht.kademlia.message.Receiver;
import com.soriole.dht.kademlia.node.Node;
import com.soriole.dht.kademlia.routing.KademliaRoutingTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingOperation implements Operation {
    static Logger logger= LoggerFactory.getLogger(Receiver.class);
    private final KadServer server;
    private final KademliaNode kadNode;
    private final Node localNode;
    private final Node toPing;


    /**
     * @param server The Kademlia server used to send & receive messages
     * @param local  The local node
     * @param toPing The node to send the ping message to
     */
    public PingOperation(KadServer server, Node local, Node toPing, KademliaNode app) {
        this.server = server;
        this.localNode = local;
        this.toPing = toPing;
        this.kadNode=app;
    }


    @Override
    public void execute() throws IOException, RoutingException {
       try {
           PingMessage ping = new PingMessage(kadNode.getRoutingTable());
           server.sendMessage(toPing, ping, ping);
       }
       catch (Exception e){
           e.printStackTrace();
       }

    }

}

