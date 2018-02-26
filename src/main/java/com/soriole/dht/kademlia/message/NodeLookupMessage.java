package com.soriole.dht.kademlia.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.soriole.dht.kademlia.node.Node;
import com.soriole.dht.kademlia.node.KademliaId;

/**
 * A message sent to other nodes requesting the K-Closest nodes to a key sent in this message.
 * When you send Node LookupMessage you send your own node info first then other data.
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public class NodeLookupMessage extends Message
{

    private KademliaId lookupId;

    public static final byte CODE = 0x05;

    /**
     * A new NodeLookupMessage to find nodes
     *
     * @param sender The Node from which the message is coming from
     * @param lookup The key for which to lookup nodes for
     */
    public NodeLookupMessage(Node sender, KademliaId lookup)
    {
        this.sender = sender;
        this.lookupId = lookup;
    }

    public NodeLookupMessage(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
        this.lookupId = new KademliaId(in);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        this.lookupId.toStream(out);
    }
    public KademliaId getLookupId()
    {
        return this.lookupId;
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public String toString()
    {
        return "NodeLookupMessage[sender=" + sender + ",lookup=" + lookupId + "]";
    }
}
