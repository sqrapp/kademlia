package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.node.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A message sent to another node requesting to connect to them.
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public class ConnectMessage implements Message {

    private Node origin;
    public static final byte MSG_CODE = 0x02;

    public ConnectMessage(Node origin) {
        this.origin = origin;
    }

    public ConnectMessage(DataInputStream in) throws IOException {
        this.fromStream(in);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException {
        this.origin = new Node(in);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException {
        origin.toStream(out);
    }

    public Node getOrigin() {
        return this.origin;
    }

    @Override
    public byte code() {
        return MSG_CODE;
    }

    @Override
    public String toString() {
        return "ConnectMessage[origin NodeId=" + origin.getNodeId() + "]";
    }
}
