/**
 * @author Joshua Kissoon
 * @created 20140218
 * @desc Operation that handles connecting to an existing Kademlia network using a bootstrap node
 */
package com.soriole.dht.kademlia.operation;

import com.soriole.dht.kademlia.message.Receiver;

import java.io.IOException;

import com.soriole.dht.kademlia.KadConfiguration;
import com.soriole.dht.kademlia.KadServer;
import com.soriole.dht.kademlia.KademliaNode;
import com.soriole.dht.kademlia.exceptions.RoutingException;
import com.soriole.dht.kademlia.message.AcknowledgeMessage;
import com.soriole.dht.kademlia.message.ConnectMessage;
import com.soriole.dht.kademlia.message.Message;
import com.soriole.dht.kademlia.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectOperation implements Operation, Receiver {
    private static Logger logger = LoggerFactory.getLogger(ConnectOperation.class);
    public static final int MAX_CONNECT_ATTEMPTS = 5;       // Try 5 times to connect to a node

    private final KadServer server;
    private final KademliaNode localNode;
    private final Node bootstrapNode;
    private final KadConfiguration config;

    private boolean error;
    private int attempts;

    /**
     * @param server    The message server used to send/receive messages
     * @param local     The local node
     * @param bootstrap Node to use to bootstrap the local node onto the network
     * @param config
     */
    public ConnectOperation(KadServer server, KademliaNode local, Node bootstrap, KadConfiguration config) {
        this.server = server;
        this.localNode = local;
        this.bootstrapNode = bootstrap;
        this.config = config;
    }

    @Override
    public synchronized void execute() throws IOException {
            /* Contact the bootstrap node */
        this.error = true;
        this.attempts = 0;
        Message m = new ConnectMessage(this.localNode.getLocalNode());

            /* Send a connect message to the bootstrap node */
        server.sendMessage(this.bootstrapNode, m, this);
    }

    /**
     * Receives an AcknowledgeMessage from the bootstrap node.
     *
     * @param comm
     */
    @Override
    public synchronized void receive(Message incoming, int comm) {
        /* The incoming message will be an acknowledgement message */
        AcknowledgeMessage msg = (AcknowledgeMessage) incoming;

        /* The bootstrap node has responded, insert it into our space */
        this.localNode.getRoutingTable().insert(this.bootstrapNode);


        Node me = this.localNode.getPublicNode();
        me.setInetAddress(msg.getMyNode().getInetAddress());
        me.setPort(msg.getMyNode().getPort());
        me.setPort(me.getPort());

        localNode.getRoutingTable().setUnresponsiveContact(localNode.getLocalNode());
        localNode.getRoutingTable().insert(me);

        logger.info("Hurrey!! Bootstrap Node has acknowledged us");
        refresh();
        /* We got a response, so the error is false */
        error = false;
        /* Wake up any waiting thread */
        notify();
    }

    /**
     * Resends a ConnectMessage to the boot strap node a maximum of MAX_ATTEMPTS
     * times.
     *
     * @param comm
     * @throws java.io.IOException
     */
    @Override
    public synchronized void timeout(int comm) throws IOException {
        if (++this.attempts < MAX_CONNECT_ATTEMPTS) {
            logger.info("Attempt to connect to bootstrap node failed. Retrying..");
            this.server.sendMessage(this.bootstrapNode, new ConnectMessage(this.localNode.getLocalNode()), this);
        } else {
            /* We just exit, so notify all other threads that are possibly waiting */
            notify();
        }
    }

    private void refresh() {
        try {
            Operation lookup = new NodeLookupOperation(this.server, this.localNode, this.localNode.getPublicNode().getNodeId(), this.config);
            lookup.execute();
            new BucketRefreshOperation(this.server, this.localNode, this.config).execute();
        } catch (IOException e) {
            logger.warn("Bootstrap Node acknowledged us, but Routing table couldn't be refreshed");
            e.printStackTrace();
        }
    }
}


