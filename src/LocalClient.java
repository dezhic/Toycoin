import protocol.Command;
import protocol.Message;
import protocol.datatype.Block;
import protocol.datatype.InventoryItem;
import protocol.datatype.InventoryType;
import protocol.message.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * This is the class that the local client will use to communicate with remote servers
 */
public class LocalClient {
    Blockchain blockchain;
    List<RemoteServer> servers;

    public LocalClient(Blockchain blockchain) throws IOException {
        this.blockchain = blockchain;
        servers = new LinkedList<>();
    }

    public void initialize() throws IOException {
        // get my own outbound server info
        String port = System.getenv("PORT");
        // get servers from DNS seeds
        String seedFilename = System.getenv("DNS_SEEDS_FILENAME");
        seedFilename = seedFilename == null ? "dns_seeds.txt" : seedFilename;
        File dnsSeeds = new File(seedFilename);
        Scanner sc = new Scanner(dnsSeeds);
        while (sc.hasNextLine()) {
            String[] server = sc.nextLine().split(":");

            if ((server[0].equals("localhost")) || (server[0].equals("127.0.0.1")) && (server[1].equals(port))) {
                continue; // don't connect to myself
            }

            try {
                addServer(server[0], Integer.parseInt(server[1]));
            } catch (IOException e) {
                System.out.println("Could not connect to server " + server[0] + ":" + server[1]);
            }
        }
    }

    public RemoteServer addServer(String host, int port) throws IOException {
        // Check if server already exists
        for (RemoteServer server : servers) {
            if (server.getSocket().getInetAddress().getHostAddress().equals(host) &&
                    server.getSocket().getPort() == port) {
                System.out.println("Server " + host + ":" + port + " already exists");
                return server;
            }
        }
        RemoteServer rs = new RemoteServer(new Socket(host, port));
        System.out.println("Connected to server " + host + ":" + port);
        sendVersion(rs);
        servers.add(rs);
        return rs;
    }
    private void sendVersion(RemoteServer server) throws IOException {
        Version version = new Version("127.0.0.1:" + System.getenv("PORT"));
        server.sendVersion(version);
    }

    void sendVerAck(RemoteServer server) throws IOException {
        server.sendVerAck();
    }

    public void broadcastNewBlock(Block block) {
        Message message = new Message(Command.BLOCK, block);
        for (RemoteServer server : servers) {
            try {
                InventoryItem item = new InventoryItem(InventoryType.MSG_BLOCK, block.getHash());
                Inv inv = new Inv(Collections.singletonList(item));
                server.sendInv(inv);
            } catch (IOException e) {
                System.out.println("Could not send block to " + server.getSocket().getInetAddress().getHostAddress() + ":" + server.getSocket().getPort());
                servers.remove(server);
                System.out.println("Removed server " + server.getSocket().getInetAddress().getHostAddress() + ":" + server.getSocket().getPort());
            }
        }
    }

    public void broadcastGetAddr() throws IOException {
        for (RemoteServer server : servers) {
            server.sendGetAddr();
        }
        System.out.println("Sent getaddr to all servers");
    }

    public void sendAddr(RemoteServer rs) {
        List<String> addresses = new LinkedList<>();
        for (RemoteServer server : servers) {
            addresses.add(server.getSocket().getInetAddress().getHostAddress() + ":" + server.getSocket().getPort());
        }
        Addr addr = new Addr(addresses);
        try {
            rs.sendAddr(addr);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not send addr to " + rs.getSocket().getInetAddress().getHostAddress() + ":" + rs.getSocket().getPort());
        }
    }

    public void sendGetData(RemoteServer rs, GetData getData) {
        try {
            rs.sendGetData(getData);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not send getdata to " + rs.getSocket().getInetAddress().getHostAddress() + ":" + rs.getSocket().getPort());
        }
    }

    public void sendBlock(RemoteServer rs, Block block) {
        try {
            rs.sendBlock(block);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not send block to " + rs.getSocket().getInetAddress().getHostAddress() + ":" + rs.getSocket().getPort());
        }
    }

    public void broadcastGetBlocks(GetBlocks getBlocks) {
        for (RemoteServer server : servers) {
            try {
                server.sendGetBlocks(getBlocks);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Could not send getblocks to " + server.getSocket().getInetAddress().getHostAddress() + ":" + server.getSocket().getPort());
            }
        }
    }

    public void sendInv(RemoteServer rs, Inv inv) {
        try {
            rs.sendInv(inv);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not send inv to " + rs.getSocket().getInetAddress().getHostAddress() + ":" + rs.getSocket().getPort());
        }
    }

    public void sendGetHeaders(RemoteServer rs, GetHeaders getHeaders) {
        try {
            rs.sendGetHeaders(getHeaders);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not send getheaders to " + rs.getSocket().getInetAddress().getHostAddress() + ":" + rs.getSocket().getPort());
        }
    }

    public void sendHeaders(RemoteServer rs, Headers headers) {
        try {
            rs.sendHeaders(headers);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not send headers to " + rs.getSocket().getInetAddress().getHostAddress() + ":" + rs.getSocket().getPort());
        }
    }

    public boolean hasBlock(String hash) {
        return blockchain.hasBlock(hash);
    }

    /**
     * Get block by its hash
     * @param hash hash of the block
     * @return the target block, or null
     */
    public Block getBlock(String hash) {
        return blockchain.getBlock(hash);
    }

    /**
     * Get block by its height
     * @param height height of the block
     * @return the target block, or null
     */
    public Block getBlock(int height) {
        return blockchain.getBlock(height);
    }

    RemoteServer getRemoteServer(String host) {
        for (RemoteServer server : servers) {
            if (server.getSocket().getInetAddress().getHostAddress().equals(host)) {
                return server;
            }
        }
        return null;
    }

    public void addBlock(Block block) {
        blockchain.add(block);
    }

    public void prune(String hash) {
        blockchain.prune(hash);
    }

    public Block getLastBlock() {
        return blockchain.getLastBlock();
    }

}
