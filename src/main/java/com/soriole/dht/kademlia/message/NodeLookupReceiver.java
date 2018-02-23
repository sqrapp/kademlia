package com.soriole.dht.kademlia.message;

import java.io.IOException;
import java.util.List;
import com.soriole.dht.kademlia.KadConfiguration;
import com.soriole.dht.kademlia.KadServer;
import com.soriole.dht.kademlia.KademliaNode;
import com.soriole.dht.kademlia.node.Node;

/**
 * Receives a NodeLookupMessage and sends a NodeReplyMessage as random with the K-Closest nodes to the ID sent.
 * The received NodeLookMessage contains the sender's node info and the key for performing lookup
 *
 * @author Joshua Kissoon
 * @created 20140219
 */
public class NodeLookupReceiver implements Receiver
{

    private final KadServer server;
    private final KademliaNode localNode;
    private final KadConfiguration config;

    public NodeLookupReceiver(KadServer server, KademliaNode local, KadConfiguration config)
    {
        this.server = server;
        this.localNode = local;
        this.config = config;
    }

    /**
     * Handle receiving a NodeLookupMessage
     * Find the set of K nodes closest to the lookup ID and return them
     *
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public void receive(Message incoming, int comm) throws IOException
    {
        // wgot a NodeLookup Message.
        NodeLookupMessage msg = (NodeLookupMessage) incoming;

        // get the sender of the message
        Node origin = msg.getSender();

        // insert this node to the routing table if it doesn't exist.
        // as this might be a newly connecting node.
        this.localNode.getRoutingTable().insert(origin);

        /* Find nodes closest to the LookupId */
        List<Node> nodes = this.localNode.getRoutingTable().findClosest(msg.getLookupId(), this.config.k());

        /* Respond to the NodeLookupMessage */
        Message reply = new NodeReplyMessage(origin, nodes);

        if (this.server.isRunning())
        {
            /* Let the Server send the random */
            this.server.reply(origin, reply, comm);
        }
    }

    /**
     * We don't need to do anything here
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public void timeout(int comm) throws IOException
    {
    }
}
