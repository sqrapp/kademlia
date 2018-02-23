package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.KadServer;
import com.soriole.dht.kademlia.KademliaNode;

import java.io.IOException;

/**
 * Receives a ConnectMessage and sends an AcknowledgeMessage as random.
 *
 * @author Joshua Kissoon
 * @created 20140219
 */
public class ConnectReceiver implements Receiver {

    private final KadServer server;
    private final KademliaNode localNode;

    public ConnectReceiver(KadServer server, KademliaNode local) {
        this.server = server;
        this.localNode = local;
    }

    /**
     * Handle receiving a ConnectMessage
     *
     * @param comm
     * @throws java.io.IOException
     */
    @Override
    public void receive(Message incoming, int comm) throws IOException {
        ConnectMessage mess = (ConnectMessage) incoming;

        /* Update the local space by inserting the sender node. */
        this.localNode.getRoutingTable().insert(mess.getSender());

        /* Respond to the connect request */
        AcknowledgeMessage msg = new AcknowledgeMessage(mess.getSender());
        msg.sender=mess.receiver;
        /* Reply to the connect message with an Acknowledgement */
        this.server.reply(mess.getSender(), msg, comm);
    }


    /**
     * We don't need to do anything here
     *
     * @param comm
     * @throws java.io.IOException
     */

    @Override
    public void timeout(int comm) throws IOException {
        // Simply ignored
    }
}
