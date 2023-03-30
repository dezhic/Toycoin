import protocol.Command;
import protocol.Message;
import protocol.datatype.InventoryItem;
import protocol.datatype.InventoryType;
import protocol.message.Addr;
import protocol.message.GetData;
import protocol.message.Inv;
import protocol.message.Version;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;

public class RemoteServer {
    private Socket socket;

    public RemoteServer(Socket socket) {
        this.socket = socket;
    }

    public void sendBlockInv(Block block) throws IOException {
        InventoryItem item = new InventoryItem(InventoryType.MSG_BLOCK, block.getHash());
        Inv inv = new Inv(Collections.singletonList(item));
        Message msg = new Message(Command.INV, inv);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(msg);
    }

    public void sendVersion(Version version) throws IOException {
        Message message = new Message(Command.VERSION, version);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent version to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendVerAck() throws IOException {
        Message message = new Message(Command.VERACK, null);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent verack to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetAddr() throws IOException {
        Message message = new Message(Command.GETADDR, null);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent getaddr to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendAddr(Addr addr) throws IOException {
        Message message = new Message(Command.ADDR, addr);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent addr to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetData(GetData getData) throws IOException {
        Message message = new Message(Command.GETDATA, getData);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent getdata to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendBlock(Block block) throws IOException {
        Message message = new Message(Command.BLOCK, block);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent block to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public Socket getSocket() {
        return socket;
    }
}
