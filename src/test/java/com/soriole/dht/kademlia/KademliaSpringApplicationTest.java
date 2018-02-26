package com.soriole.dht.kademlia;

import com.soriole.dht.kademlia.exceptions.RoutingException;
import com.soriole.dht.kademlia.node.KademliaId;
import com.soriole.dht.kademlia.routing.Contact;
import com.soriole.dht.kademlia.routing.KademliaBucket;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;


public class KademliaSpringApplicationTest {

    public KademliaSpringApplicationTest(){

    }
    @Test
    public void testNode() throws IOException, InterruptedException {
        // no of nodes to create
        int testNodes=15;
        // id of starting node
        BigInteger id=new BigInteger("12345678901234567890");
        // serial no of node
        int name_no=1;
        String peerIp="127.0.0.1";
        int port=9000;

        ArrayList<JKademliaNode> nodes=new ArrayList<>();
        boolean yes=false;
        // create testNodes no. of kademlia nodes.
        while(testNodes>0){

            System.out.printf("Working with Node %d\n",name_no);
            JKademliaNode bot1 = new JKademliaNode(id.toString(), new KademliaId(id.toString()), port);
            // first node needs not connect to a bootstrap node. It's the botstrap node :D
            if(yes){
                try {
                    bot1.bootstrap(nodes.get(0).getLocalNode());
                }
                catch (RoutingException e){
                    System.err.printf("Error connecting to bootstrap from nodeid %d\n",name_no);
                }
            }
            // increment each
            name_no+=1;
            id=id.add(new BigInteger("1"));
            port+=1;
            nodes.add(bot1);
            testNodes-=1;
            yes=true;
            Thread.sleep(1000);

        }

        int i=1;
        for(JKademliaNode kadNode :nodes){
            System.out.println("\n\nNode "+String.valueOf(kadNode.getLocalNode()));
            System.out.print(String.valueOf(kadNode.getRoutingTable().getAllNodes().size())+" Total nodes in all Buckets :");
            int j=1;
            for(KademliaBucket bucket :kadNode.getRoutingTable().getBuckets()){
                if (bucket.getContacts().size()>0){
                    System.out.printf("\nBucket %d : %s",j,bucket.getContacts());
                    for(Contact contact : bucket.getContacts()){
                        System.out.printf("%s, ",contact.getNode());

                    }
                }
                j++;
            }
        }
    }


}
