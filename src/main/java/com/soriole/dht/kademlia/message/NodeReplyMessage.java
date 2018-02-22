package com.soriole.dht.kademlia.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.soriole.dht.kademlia.node.KademliaId;
import com.soriole.dht.kademlia.node.Node;

/**
 * A message used to connect nodes.
 * When a NodeLookup Request comes in, we respond with a NodeReplyMessage.
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public class NodeReplyMessage extends Message
{

    public static final byte CODE = 0x06;
    private List<Node> nodes;

    public NodeReplyMessage(Node origin, List<Node> nodes)
    {
        this.sender = origin;
        this.nodes = nodes;
    }

    public NodeReplyMessage(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {

        sender.setNodeId(new KademliaId(in));

        /* Get the number of incoming nodes */
        int len = in.readInt();
        this.nodes = new ArrayList<>(len);

        /* Read in all nodes */
        for (int i = 0; i < len; i++)
        {
            this.nodes.add(new Node(in));
        }
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        // give the sender his/her own node information
        sender.getNodeId().toStream(out);

        /* Add all other nodes to the stream */
        int len = this.nodes.size();
        if (len > 255)
        {
            throw new IndexOutOfBoundsException("Too many nodes in list to send in NodeReplyMessage. Size: " + len);
        }

        // write the length of node to the packet
        out.writeInt(len);
        for (Node n : this.nodes)
        {
            // loop over each result node.
            n.toStream(out);
        }
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    public List<Node> getNodes()
    {
        return this.nodes;
    }

    @Override
    public String toString()
    {
        return "NodeReplyMessage[sender NodeId=" + sender.getNodeId() + "]";
    }
}
