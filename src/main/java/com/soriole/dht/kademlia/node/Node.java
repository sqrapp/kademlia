package com.soriole.dht.kademlia.node;

import com.soriole.dht.kademlia.message.Streamable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * A Node in the Kademlia network - Contains basic node network information.
 *
 * @author Joshua Kissoon
 * @version 0.1
 * @since 20140202
 */
public class Node implements Streamable, Serializable {

    private KademliaId nodeId;
    private InetAddress inetAddress;
    private int port;

    public Node() {
    }

    public Node copy() {

        Node n = new Node();
        n.nodeId = new KademliaId(nodeId.getBytes());
        n.inetAddress = this.inetAddress;
        n.port = port;
        return n;

    }

    public Node(KademliaId nid, InetAddress ip, int port) {
        this.nodeId = nid;
        this.inetAddress = ip;
        this.port = port;
    }

    public Node(String kadId, InetAddress ip, int port) {
        this.nodeId = new KademliaId(kadId);
        this.inetAddress = ip;
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setNodeId(KademliaId id) {
        this.nodeId = id;
    }

    /**
     * Load the Node's data from a DataInput stream
     *
     * @param in
     * @throws IOException
     */
    public Node(DataInputStream in) throws IOException {
        this.fromStream(in);
    }

    /**
     * Set the InetAddress of this node
     *
     * @param addr The new InetAddress of this node
     */
    public void setInetAddress(InetAddress addr) {
        this.inetAddress = addr;
    }

    /**
     * @return The NodeId object of this node
     */
    public KademliaId getNodeId() {
        return this.nodeId;
    }

    /**
     * Creates a SocketAddress for this node
     *
     * @return
     */
    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(this.inetAddress, this.port);
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException {
        /* Add the NodeId to the stream */
        this.nodeId.toStream(out);

        /* Add the Node's IP address to the stream */
        byte[] a = inetAddress.getAddress();
        if (a.length != 4) {
            throw new RuntimeException("Expected InetAddress of 4 bytes, got " + a.length);
        }
        out.write(a);

        /* Add the port to the stream */
        out.writeInt(port);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException {
        /* Load the NodeId */
        this.nodeId = new KademliaId(in);

        /* Load the IP Address */
        byte[] ip = new byte[4];
        in.readFully(ip);
        this.inetAddress = InetAddress.getByAddress(ip);

        /* Read in the port */
        this.port = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            Node n = (Node) o;
            if (n == this) {
                return true;
            }
            return this.getNodeId().equals(n.getNodeId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getNodeId().hashCode();
    }

    @Override
    public String toString() {
        return this.getNodeId().toString();
    }

    public String toDetailString() {
        return this.getNodeId() + " || " + this.getSocketAddress();
    }
}
