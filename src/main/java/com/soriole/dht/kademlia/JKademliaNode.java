package com.soriole.dht.kademlia;

import com.soriole.dht.kademlia.exceptions.ContentNotFoundException;
import com.soriole.dht.kademlia.exceptions.RoutingException;
import com.soriole.dht.kademlia.message.MessageFactory;
import com.soriole.dht.kademlia.node.KademliaId;
import com.soriole.dht.kademlia.node.Node;
import com.soriole.dht.kademlia.operation.*;
import com.soriole.dht.kademlia.routing.JKademliaRoutingTable;
import com.soriole.dht.kademlia.routing.KademliaRoutingTable;
import com.soriole.dht.kademlia.util.serializer.JsonDHTSerializer;
import com.soriole.dht.kademlia.util.serializer.JsonRoutingTableSerializer;
import com.soriole.dht.kademlia.util.serializer.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The main Kademlia Node on the network, this node manages everything for this local system.
 *
 * @author Joshua Kissoon
 * @todo When we receive a store message - if we have a newer version of the content, re-send this newer version to that node so as to update their version
 * @todo Handle IPv6 Addresses
 * @since 20140215
 */
public class JKademliaNode implements KademliaNode {
    private static final Logger logger = LoggerFactory.getLogger(JKademliaNode.class);

    /* Kademlia Attributes */
    private final String ownerId;

    /* Objects to be used */
    private final Node localNode;
    private final KadServer server;
    private final KademliaDHT dht;
    private KademliaRoutingTable routingTable;
    private final int udpPort;
    private KadConfiguration config;
    private Node publicNode;

    /* Timer used to execute refresh operations */
    private Timer refreshOperationTimer = null;
    private TimerTask refreshOperationTTask;

    /* Factories */
    private final MessageFactory messageFactory;

    /* Statistics */
    private final KadStatistician statistician;

    /**
     * Creates a Kademlia DistributedMap using the specified name as filename base.
     * If the id cannot be read from disk the specified defaultId is used.
     * The instance is bootstraped to an existing network by specifying the
     * address of a bootstrap node in the network.
     *
     * @param ownerId      The Name of this node used for storage
     * @param localNode    The Local Node for this Kad instance
     * @param dht          The DHT for this instance
     * @param config
     * @throws IOException If an error occurred while reading id or local map
     *                     from disk <i>or</i> a network error occurred while
     *                     attempting to bootstrap to the network
     */

    public JKademliaNode(String ownerId, Node localNode, KademliaDHT dht, KadConfiguration config) throws IOException {
        this.ownerId = ownerId;
        this.dht = dht;
        this.config = config;
        this.statistician = new Statistician();
        this.messageFactory = new MessageFactory(this, this.dht, this.config);
        this.server = new KadServer(this.messageFactory, localNode, this.config, this.statistician);
        this.localNode=server.getLocalNode();
        this.publicNode = this.localNode.copy();
        this.udpPort = this.localNode.getPort();
        this.startRefreshOperation(this.config.restoreInterval());
    }

    public JKademliaNode(String ownerId, Node localNode,  KademliaDHT dht, KademliaRoutingTable routingTable, KadConfiguration config) throws IOException {
        this(ownerId,localNode,dht,config);
        this.routingTable = routingTable;
    }

    public JKademliaNode(String ownerId, Node node, KademliaRoutingTable routingTable, KadConfiguration config) throws IOException {
        this(
                ownerId,
                node,
                new DHT(ownerId, config),
                routingTable,
                config
        );
    }

    public JKademliaNode(String ownerId, Node node, KadConfiguration config) throws IOException {
        this(
                ownerId,
                node,
                new DHT(ownerId, config),
                config
        );
        this.routingTable= new JKademliaRoutingTable(this.localNode,config);
    }

    public JKademliaNode(String ownerId, KademliaId defaultId, int udpPort) throws IOException {
        this(
                ownerId,
                new Node(defaultId, InetAddress.getLocalHost(), udpPort),
                new DefaultConfiguration()
        );
    }

    public JKademliaNode(String ownerId, Node node) throws IOException {
        this(
                ownerId,
                node,
                new DefaultConfiguration()
        );
    }

    /**
     * Load Stored state using default configuration
     *
     * @param ownerId The ID of the owner for the stored state
     * @return A Kademlia instance loaded from a stored state in a file
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public static JKademliaNode loadFromFile(String ownerId) throws FileNotFoundException, IOException, ClassNotFoundException {
        return JKademliaNode.loadFromFile(ownerId, new DefaultConfiguration());
    }

    /**
     * Load Stored state
     *
     * @param ownerId The ID of the owner for the stored state
     * @param iconfig Configuration information to work with
     * @return A Kademlia instance loaded from a stored state in a file
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public static JKademliaNode loadFromFile(String ownerId, KadConfiguration iconfig) throws FileNotFoundException, IOException, ClassNotFoundException {
        DataInputStream din;

        /**
         * @section Read Basic Kad data
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, iconfig) + File.separator + "kad.kns"));
        JKademliaNode ikad = new JsonSerializer<JKademliaNode>().read(din);

        /**
         * @section Read the routing table
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, iconfig) + File.separator + "routingtable.kns"));
        KademliaRoutingTable irtbl = new JsonRoutingTableSerializer(iconfig).read(din);

        /**
         * @section Read the node state
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, iconfig) + File.separator + "node.kns"));
        Node inode = new JsonSerializer<Node>().read(din);

        /**
         * @section Read the DHT
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, iconfig) + File.separator + "dht.kns"));
        KademliaDHT idht = new JsonDHTSerializer().read(din);
        idht.setConfiguration(iconfig);

        return new JKademliaNode(ownerId, inode, idht, irtbl, iconfig);
    }

    @Override
    public final void startRefreshOperation(long interval) {
        if (this.refreshOperationTimer != null) {
            stopRefreshOperation();
        }
        this.refreshOperationTimer = new Timer(true);
        refreshOperationTTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    /* Runs a DHT RefreshOperation  */
                    JKademliaNode.this.refresh();
                } catch (IOException e) {
                    logger.error("KademliaNode: Refresh Operation Failed", e);
                }
            }
        };
        refreshOperationTimer.schedule(refreshOperationTTask, interval, interval);

    }

    @Override
    public final void stopRefreshOperation() {
        if (this.refreshOperationTimer != null) {
            this.refreshOperationTimer.cancel();
            this.refreshOperationTimer.purge();
            this.refreshOperationTimer = null;
        }
    }

    @Override
    public Node getLocalNode() {
        return this.localNode;
    }

    @Override
    public Node getPublicNode() {
        return publicNode;
    }


    @Override
    public KadServer getServer() {
        return this.server;
    }

    @Override
    public KademliaDHT getDHT() {
        return this.dht;
    }

    @Override
    public KadConfiguration getCurrentConfiguration() {
        return this.config;
    }

    @Override
    public final synchronized void bootstrap(Node n) throws IOException, RoutingException {
        long startTime = System.nanoTime();
        Operation op = new ConnectOperation(this.server, this, n, this.config);
        op.execute();
        long endTime = System.nanoTime();
        this.statistician.setBootstrapTime(endTime - startTime);
    }

    @Override
    public int put(KadContent content) throws IOException {
        return this.put(new JKademliaStorageEntry(content));
    }

    @Override
    public int put(JKademliaStorageEntry entry) throws IOException {
        StoreOperation sop = new StoreOperation(this.server, this, entry, this.dht, this.config);
        sop.execute();

        /* Return how many nodes the content was stored on */
        return sop.numNodesStoredAt();
    }

    @Override
    public void putLocally(KadContent content) throws IOException {
        this.dht.store(new JKademliaStorageEntry(content));
    }

    @Override
    public JKademliaStorageEntry get(GetParameter param) throws NoSuchElementException, IOException, ContentNotFoundException {
        if (this.dht.contains(param)) {
            /* If the content exist in our own DHT, then return it. */
            return this.dht.get(param);
        }

        /* Seems like it doesn't exist in our DHT, get it from other Nodes */
        long startTime = System.nanoTime();
        ContentLookupOperation clo = new ContentLookupOperation(server, this, param, this.config);
        clo.execute();
        long endTime = System.nanoTime();
        this.statistician.addContentLookup(endTime - startTime, clo.routeLength(), clo.isContentFound());
        return clo.getContentFound();
    }

    @Override
    public void refresh() throws IOException {
        new KadRefreshOperation(this.server, this, this.dht, this.config).execute();
    }

    @Override
    public String getOwnerId() {
        return this.ownerId;
    }

    @Override
    public int getPort() {
        return this.udpPort;
    }

    @Override
    public void shutdown(final boolean saveState) throws IOException {
        /* Shut down the server */
        this.server.shutdown();

        this.stopRefreshOperation();

        /* Save this Kademlia instance's state if required */
        if (saveState) {
            /* Save the system state */
            this.saveKadState();
        }
    }

    @Override
    public void saveKadState() throws IOException {
        DataOutputStream dout;

        /**
         * @section Store Basic Kad data
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(this.ownerId, this.config) + File.separator + "kad.kns"));
        new JsonSerializer<JKademliaNode>().write(this, dout);

        /**
         * @section Save the node state
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(this.ownerId, this.config) + File.separator + "node.kns"));
        new JsonSerializer<Node>().write(this.localNode, dout);

        /**
         * @section Save the routing table
         * We need to save the routing table separate from the node since the routing table will contain the node and the node will contain the routing table
         * This will cause a serialization recursion, and in turn a Stack Overflow
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(this.ownerId, this.config) + File.separator + "routingtable.kns"));
        new JsonRoutingTableSerializer(this.config).write(this.getRoutingTable(), dout);

        /**
         * @section Save the DHT
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(this.ownerId, this.config) + File.separator + "dht.kns"));
        new JsonDHTSerializer().write(this.dht, dout);

    }

    /**
     * Get the name of the folder for which a content should be stored
     *
     * @return String The name of the folder to store node states
     */
    private static String getStateStorageFolderName(String ownerId, KadConfiguration iconfig) {
        /* Setup the nodes storage folder if it doesn't exist */
        String path = iconfig.getNodeDataFolder(ownerId) + File.separator + "nodeState";
        File nodeStateFolder = new File(path);
        if (!nodeStateFolder.isDirectory()) {
            nodeStateFolder.mkdir();
        }
        return nodeStateFolder.toString();
    }

    @Override
    public KademliaRoutingTable getRoutingTable() {
        return this.routingTable;
    }

    @Override
    public KadStatistician getStatistician() {
        return this.statistician;
    }

    /**
     * Creates a string containing all data about this Kademlia instance
     *
     * @return The string representation of this Kad instance
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n\nPrinting Kad State for instance with owner: ");
        sb.append(this.ownerId);
        sb.append("\n\n");

        sb.append("\n");
        sb.append("Local Node");
        sb.append(this.localNode);
        sb.append("\n");

        sb.append("\n");
        sb.append("Routing Table: ");
        sb.append(this.getRoutingTable());
        sb.append("\n");

        sb.append("\n");
        sb.append("DHT: ");
        sb.append(this.dht);
        sb.append("\n");

        sb.append("\n\n\n");

        return sb.toString();
    }
}
