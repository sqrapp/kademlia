package com.soriole.dht.kademlia.message;

import com.soriole.dht.kademlia.node.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A message used to acknowledge a request from a node; can be used in many situations.
 * - Mainly used to acknowledge a connect message
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public class AcknowledgeMessage extends Message {

    public static final byte MSG_CODE = 0x01;

    public AcknowledgeMessage(Node origin) {
        this.origin = origin;
    }

    public AcknowledgeMessage(DataInputStream in) throws IOException {
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
        return "AcknowledgeMessage[origin=" + origin.getNodeId() + "]";
    }
}
