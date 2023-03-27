import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class LocalServer extends Thread {
    private int port;
    ServerSocket server;

    private LocalClient localClient;
    private List<RemoteClient> remoteClients;

    public LocalServer(LocalClient localClient) throws IOException {
        this.port = Integer.parseInt(System.getenv("PORT"));
        this.server = new ServerSocket(this.port);
        this.remoteClients = new LinkedList<>();
        this.localClient = localClient;
    }

    @Override
    public void run() {
        try {
            accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void accept() throws IOException {
        while (true) {
            Socket clientSocket = server.accept();
            RemoteClient newClient = new RemoteClient(clientSocket, this, this.localClient);
            newClient.start();
            this.remoteClients.add(newClient);
        }
    }

    public synchronized void removeClient(RemoteClient client) {
        this.remoteClients.remove(client);
    }

}
