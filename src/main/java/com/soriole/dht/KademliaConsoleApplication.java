package com.soriole.dht;

import com.soriole.dht.kademlia.JKademliaNode;
import com.soriole.dht.kademlia.KadServer;
import com.soriole.dht.kademlia.node.KademliaId;
import com.soriole.dht.kademlia.node.Node;
import com.soriole.dht.kademlia.operation.PingPongOperation;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;


public class KademliaConsoleApplication {
    /**
     * Kademlia can be run as console application
     * to directly interact with the node by giving commands
     * commands available
     * 1) peers : list all the peers
     * 2) info : current info of server
     * 3) ping : ping a peer
     */

    private static final Logger logger = LoggerFactory.getLogger(KademliaSpringApplication.class);
    CommandLine comandline;
    JKademliaNode node;
    Options options;

    private void printHelp() {
        new HelpFormatter().printHelp("kademlia", "Bootstrap Kademlia or connect to bootstrap node", options, "");
        System.exit(1);
    }

    public KademliaConsoleApplication(String[] args) throws ParseException {
        final CommandLineParser parser = new BasicParser();
        options = new Options();
        options.addOption("spring",false,"If specified the kademlia Application will run in spring boot mode");
        options.addOption("b", "bootstrap", false, "If specified, it means that it's a bootstrap node");
        options.addOption("a","address",true,"The address to which the local node will be bound to");
        options.addOption("p", "port", true, "Port for local node, choosed dynamically if not specified");
        options.addOption("n", "name", true, "Publicly displayed name of server. Kad-Server by default");
        options.addOption("bp", true, "Bootstrap server's port: 99999 by default");
        options.addOption("ba", true, "Address of bootstrap server");
        options.addOption("bid", true, "Bootstrap server's kademlia id");
        options.addOption("h", false, "Print help");
        this.comandline = new BasicParser().parse(options, args);
        if (comandline.hasOption('h')) {
            printHelp();

        }
        else if(comandline.hasOption("spring")){
            KademliaSpringApplication.main(args);
        }
    }

    public void interpretArgs() throws NoSuchAlgorithmException, IOException {

        // default myport to 0 will result in choosing a random unbound port in the machine.
        int myport = 0;
        String myName = "Kad-Server";
        int peerPort = 9999;
        String myAddress=null;
        String peerAddress = null;
        KademliaId serverId=null;
        String kadid = "";



        if (comandline.hasOption('n')) {
            myName = comandline.getOptionValue('n');
        }
        kadid = myName + new java.sql.Timestamp(System.currentTimeMillis()).toString();
        kadid = Base64.getEncoder().encodeToString(MessageDigest.getInstance("Sha-256").digest(kadid.getBytes()));
        if (comandline.hasOption("a")){
            myAddress=comandline.getOptionValue('a');
        }
        else
            myAddress="127.0.0.1";

        if (comandline.hasOption('p')) {
            myport = Integer.valueOf(comandline.getOptionValue('p'));
        }
        if (comandline.hasOption("bp")) {
            peerPort = Integer.valueOf(comandline.getOptionValue("bp"));
        }
        if (comandline.hasOption("ba")) {
            peerAddress = comandline.getOptionValue("ba");
        }
        if (comandline.hasOption("bid")) {
            String stringId = comandline.getOptionValue("bid");
            KademliaId id = new KademliaId(new BigInteger(stringId, 16).toByteArray());
        }

        Node me = new Node(kadid.substring(0, 20), InetAddress.getByName(myAddress), myport);
        node = new JKademliaNode(myName, me);
        // don't do the auto refresh operation at all
        node.stopRefreshOperation();
        logger.info("Server info :" + node.getLocalNode().toDetailString());

        if (!comandline.hasOption('b')) {
            if (peerAddress == null) {
                logger.error("ba arguments is compulsary if this node is not a bootstrap node\n\n");
                printHelp();
            } else {
                node.bootstrap(new Node(serverId, InetAddress.getByName(peerAddress), peerPort));
            }
        }
    }

    public void commandParser() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean read = true;

        while (read) {
            System.out.print('>');
            String input = scanner.nextLine();
            String[] list = input.split(" ");

            if (list.length == 1) {
                switch (input.toLowerCase()) {
                    case "exit":
                        System.exit(0);
                    case "peers":
                        System.out.print("Peer List : [");
                        for(Node n:node.getRoutingTable().getAllNodes()){
                            System.out.print(n.toDetailString()+", ");
                        }
                        System.out.println(']');
                        continue;
                    case "info":
                        System.out.println("Routing Table : " + node.getRoutingTable().getAllNodes().toString());
                        System.out.println("Messages Sent    : " + KadServer.messagesSent);
                        System.out.println("Messages Received: " + KadServer.messagesReceived);
                        continue;
                    case "refresh":
                        try {
                            node.refresh();
                        } catch (IOException e) {
                            System.out.println(e.getClass().getName()+" : "+e.getMessage());
                        }
                        continue;
                    default:
                        System.out.println("Command couldn't be understood : "+input);
                        continue;
                }
            }
            else if (list[0].equals("info")) {
                try {
                    int val = Integer.valueOf(list[1]);
                    String str = ((Node) node.getRoutingTable().getAllNodes().get(val)).toDetailString();
                    System.out.println(str);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if (list[0].equals("ping")) {
                try {
                    int val = Integer.valueOf(list[1]);
                    Node n = (Node) node.getRoutingTable().getAllNodes().get(val);
                    new PingPongOperation( n, this.node).execute();
                } catch (Exception e) {
                    System.out.println(e.getClass().getName()+" : "+e.getMessage());
                }
            }
            else if (list[0].equals("autorefresh")){
                try {
                    long val = Long.valueOf(list[1]);

                    if(val==0){
                        node.stopRefreshOperation();
                        System.out.println("Autorefresh operation disabled");
                    }
                    else{
                        if (val<1000) val=1000;
                        node.startRefreshOperation(val);
                        System.out.println("Started autorefresh");

                    }
                } catch (Exception e) {
                    System.out.println(e.getClass().getName()+" : "+e.getMessage());
                }

            }
            else{
                System.out.print("["+list[0]);
                for(int i=1;i<list.length;i++) {
                    System.out.print(", "+list[i]);
                }
                System.out.println("]");
            }
        }
    }

    public static void main(String[] args) {
        try {
            KademliaConsoleApplication app = new KademliaConsoleApplication(args);
            app.interpretArgs();
            app.commandParser();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
