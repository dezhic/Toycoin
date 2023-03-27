import com.google.gson.Gson;
import protocol.Message;
import protocol.message.Inv;

import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * This is the class that will be <b>used by the server</b> to handle the client
 */
public class RemoteClient extends Thread {
    private Socket socket;
    private LocalServer server;

    /**
     * @param socket the socket of the client, for messaging
     * @param server the server object, for removing the client
     */
    public RemoteClient(Socket socket, LocalServer server) {
        this.socket = socket;
        this.server = server;
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
                    default:
                        throw new Exception("Unknown command: " + message.getCommand());
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Bad client, f*** off!");
                server.removeClient(this);
                break;
            }

        }
    }

    private void handleInv(Object payload) {
        Inv inv = (Inv) payload;
        Gson gson = new Gson();
        System.out.println(gson.toJson(inv));
    }

}
