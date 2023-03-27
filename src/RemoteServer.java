import protocol.Command;
import protocol.Message;
import protocol.datatype.InventoryItem;
import protocol.datatype.InventoryType;
import protocol.message.Inv;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;

public class RemoteServer {
    private Socket socket;

    public RemoteServer(Socket socket) {
        this.socket = socket;
    }

    public void sendBlock(Block block) throws IOException {
        InventoryItem item = new InventoryItem(InventoryType.MSG_BLOCK, block.getHash());
        Inv inv = new Inv(Collections.singletonList(item));
        Message msg = new Message(Command.INV, inv);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(msg);
    }
}
