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
import com.soriole.dht.kademlia.message.PongMessage;
import com.soriole.dht.kademlia.message.Receiver;
import com.soriole.dht.kademlia.node.Node;
import com.soriole.dht.kademlia.routing.KademliaRoutingTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingPongOperation implements Operation,Receiver {
    static Logger logger= LoggerFactory.getLogger(Receiver.class);
    private final KademliaNode kadNode;
    private final Node toPing;
    private PingMessage ping;
    /**
     * @param toPing The node to send the ping message to
     */
    public PingPongOperation(Node toPing, KademliaNode app) {
        this.toPing = toPing;
        this.kadNode=app;

    }


    @Override
    public void execute() throws IOException {
       try {
           ping = new PingMessage(kadNode.getRoutingTable());
           kadNode.getServer().sendMessage(toPing, ping, this);
       }
       catch (Exception e){
           e.printStackTrace();
       }

    }


    @Override
    public void receive(Message incoming, int conversationId) throws IOException {
        PongMessage pong= (PongMessage) incoming;
        if(ping.random==pong.random){
            System.out.println("Peer \""+incoming.sender.getNodeId()+"\" correctly responded our ping message");
        }
    }

    @Override
    public void timeout(int conversationId) throws IOException {
        System.out.println("Peer didn't care about our Ping Message");
    }
}

