package com.soriole.dht.kademlia.simulations;

import com.soriole.dht.kademlia.JKademliaNode;
import com.soriole.dht.kademlia.node.KademliaId;

import java.io.IOException;

/**
 * Testing connecting 2 nodes to each other
 *
 * @author Joshua Kissoon
 * @created 20140219
 */
public class NodeConnectionTest {

    public static void main(String[] args) {
        try {
            /* Setting up 2 Kad networks */
            JKademliaNode kad1 = new JKademliaNode("JoshuaK", new KademliaId("ASF45678947584567467"), 7574);
            System.out.println("Created Node Kad 1: " + kad1.getLocalNode().getNodeId());

            JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("ASERTKJDHGVHERJHGFLK"), 7572);
            //NodeId diff12 = kad1.getLocalNode().getNodeId().xor(kad2.getLocalNode().getNodeId());
            System.out.println("Created Node Kad 2: " + kad2.getLocalNode().getNodeId());
//            System.out.println(kad1.getLocalNode().getNodeId() + " ^ " + kad2.getLocalNode().getNodeId() + " = " + diff12);
//            System.out.println("Kad 1 - Kad 2 distance: " + diff12.getFirstSetBitIndex());

            /* Connecting 2 to 1 */
            System.out.println("Connecting Kad 1 and Kad 2");
            kad1.bootstrap(kad2.getLocalNode());

//            System.out.println("Kad 1: ");
//            System.out.println(kad1.getLocalNode().getRoutingTable());
//            System.out.println("Kad 2: ");
//            System.out.println(kad2.getLocalNode().getRoutingTable());

            /* Creating a new node 3 and connecting it to 1, hoping it'll get onto 2 also */
            JKademliaNode kad3 = new JKademliaNode("Jessica", new KademliaId("ASERTKJDOLKMNBVFR45G"), 7783);
            System.out.println("\n\n\n\n\n\nCreated Node Kad 3: " + kad3.getLocalNode().getNodeId());

            System.out.println("Connecting Kad 3 and Kad 2");
            kad3.bootstrap(kad2.getLocalNode());

//            NodeId diff32 = kad3.getLocalNode().getNodeId().xor(kad2.getLocalNode().getNodeId());
//            NodeId diff31 = kad1.getLocalNode().getNodeId().xor(kad3.getLocalNode().getNodeId());
//            System.out.println("Kad 3 - Kad 1 distance: " + diff31.getFirstSetBitIndex());
//            System.out.println("Kad 3 - Kad 2 distance: " + diff32.getFirstSetBitIndex());
            JKademliaNode kad4 = new JKademliaNode("Sandy", new KademliaId("ASERTK85OLKMN85FR4SS"), 7789);
            System.out.println("\n\n\n\n\n\nCreated Node Kad 4: " + kad4.getLocalNode().getNodeId());

            System.out.println("Connecting Kad 4 and Kad 2");
            kad4.bootstrap(kad2.getLocalNode());

            System.out.println("\n\nKad 1: " + kad1.getLocalNode().getNodeId() + " Routing Table: ");
            System.out.println(kad1.getRoutingTable());
            System.out.println("\n\nKad 2: " + kad2.getLocalNode().getNodeId() + " Routing Table: ");
            System.out.println(kad2.getRoutingTable());
            System.out.println("\n\nKad 3: " + kad3.getLocalNode().getNodeId() + " Routing Table: ");
            System.out.println(kad3.getRoutingTable());
            System.out.println("\n\nKad 4: " + kad4.getLocalNode().getNodeId() + " Routing Table: ");
            System.out.println(kad4.getRoutingTable());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
