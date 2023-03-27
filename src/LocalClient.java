import protocol.Command;
import protocol.Message;

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
        File dnsSeeds = new File("dns_seeds.txt");
        Scanner sc = new Scanner(dnsSeeds);
        while (sc.hasNextLine()) {
            String[] server = sc.nextLine().split(":");

            if ((server[0].equals("localhost")) || (server[0].equals("127.0.0.1")) && (server[1].equals(port))) {
                continue; // don't connect to myself
            }

            try {
                RemoteServer rs = new RemoteServer(new Socket(server[0], Integer.parseInt(server[1])));
                servers.add(rs);
            } catch (IOException e) {
                System.out.println("Could not connect to server " + server[0] + ":" + server[1]);
            }
        }
    }

    public void broadcastNewBlock(Block block) throws IOException {
        Message message = new Message(Command.BLOCK, block);
        for (RemoteServer server : servers) {
            server.sendBlock(block);
        }
    }


}
