import com.google.gson.Gson;
import protocol.Message;
import protocol.message.Addr;
import protocol.message.Inv;
import protocol.message.Version;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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

    private void handleInv(Object payload) {
        Inv inv = (Inv) payload;
        Gson gson = new Gson();
        System.out.println(gson.toJson(inv));
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
