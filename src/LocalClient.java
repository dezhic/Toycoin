import protocol.Command;
import protocol.Message;
import protocol.message.Version;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
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
        initialize();
    }

    private void initialize() throws IOException {
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

    public void broadcastNewBlock(Block block) throws IOException {
        Message message = new Message(Command.BLOCK, block);
        for (RemoteServer server : servers) {
            server.sendBlock(block);
        }
    }


}
