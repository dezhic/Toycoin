import com.google.gson.Gson;
import protocol.Message;
import protocol.datatype.InventoryItem;
import protocol.message.Addr;
import protocol.message.GetData;
import protocol.message.Inv;
import protocol.message.Version;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

import static protocol.datatype.InventoryType.MSG_BLOCK;
import static protocol.datatype.InventoryType.MSG_TX;

/**
 * This is the class that will be <b>used by the server</b> to handle the client
 */
public class RemoteClient extends Thread {
    private Socket socket;
    private LocalClient localClient;
    private LocalServer localServer;

    /**
     * @param socket the socket of the client, for messaging
     * @param localServer the localServer object, for removing the client
     */
    public RemoteClient(Socket socket, LocalServer localServer, LocalClient localClient) {
        this.socket = socket;
        this.localServer = localServer;
        this.localClient = localClient;
    }

    @Override
    public void run() {
        listen();
    }

    private void listen() {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                switch (message.getCommand()) {
                    case INV:
                        handleInv(message.getPayload());
                        break;
                    case VERSION:
                        handleVersion(message.getPayload());
                        break;
                    case VERACK:
                        break;
                    case GETADDR:
                        handleGetAddr(message.getPayload());
                        break;
                    case ADDR:
                        handleAddr(message.getPayload());
                        break;
                    case GETDATA:
                        handleGetData(message.getPayload());
                        break;
                    case BLOCK:
                        handleBlock(message.getPayload());
                        break;
                    default:
                        throw new Exception("Unknown command: " + message.getCommand());
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Bad client, f*** off!");
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                localServer.removeClient(this);
                break;
            }

        }
    }

    private void handleBlock(Object payload) {
        Block block = (Block) payload;
//        localClient.addBlock(block);
        // TODO: pause mining to compare
        //   if block is better, stop mining and start mining on the new block
    }

    private void handleGetData(Object payload) {
        GetData getData = (GetData) payload;
        List<InventoryItem> inventory = getData.getInventory();
        for (InventoryItem item : inventory) {
            if (item.getType() == MSG_BLOCK) {
                // check if we have the block
                if (localClient.hasBlock(item.getHash())) {
                    System.out.println("Sending block " + item.getHash());
                    localClient.sendBlock(localClient.getRemoteServer(socket.getInetAddress().getHostAddress()),
                            localClient.getBlock(item.getHash()));
                } else {
                    System.out.println("Don't have block " + item.getHash());
                }
            } else if (item.getType() == MSG_TX) {
                // TODO
            }
        }
    }

    private void handleInv(Object payload) {
        Inv inv = (Inv) payload;
//        Gson gson = new Gson();
//        System.out.println(gson.toJson(inv));
        List<InventoryItem> inventory = inv.getInventory();
        for (InventoryItem item : inventory) {
            if (item.getType() == MSG_BLOCK) {
                // check if we have the block
                if (localClient.hasBlock(item.getHash())) {
                    System.out.println("Already have block " + item.getHash());
                    continue;
                }
                System.out.println("Requesting block " + item.getHash());
                localClient.sendGetData(localClient.getRemoteServer(socket.getInetAddress().getHostAddress()),
                        new GetData(Collections.singletonList(item)));
            } else if (item.getType() == MSG_TX) {
                // TODO
//                localClient.sendGetData(socket.getInetAddress().getHostAddress(), item.getHash());
            }
        }

    }

    private void handleVersion(Object payload) {
        Version version = (Version) payload;
        String[] address = version.getLocalAddress().split(":");
        try {
            RemoteServer rs = localClient.addServer(address[0], Integer.parseInt(address[1]));
            localClient.sendVerAck(rs);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot connect to " + version.getLocalAddress() + " when sending verack");
        }
    }

    private void handleGetAddr(Object payload) {
        RemoteServer rs = localClient.getRemoteServer(socket.getInetAddress().getHostAddress());
        localClient.sendAddr(rs);
    }

    private void handleAddr(Object payload) {
        Addr addr = (Addr) payload;
        System.out.println("Received " + addr.getAddresses().size() + " addresses from " +
                socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        for (String address : addr.getAddresses()) {

            System.out.println("Connecting to " + address);
            String[] addressSplit = address.split(":");
            String myPort = System.getenv("PORT");
            if ((addressSplit[0].equals("localhost")) || (addressSplit[0].equals("127.0.0.1")) && (addressSplit[1].equals(myPort))) {
                System.out.println("Skipping myself");
                continue; // skip myself
            }
            try {
                localClient.addServer(addressSplit[0], Integer.parseInt(addressSplit[1]));
            } catch (IOException e) {
                System.out.println("Cannot connect to " + address + " when sending addr");
            }
        }
    }

}
