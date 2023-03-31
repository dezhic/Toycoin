package protocol.message;

import com.google.gson.Gson;
import datatype.InventoryItem;
import protocol.Payload;

import java.io.*;
import java.util.List;

/**
 * Notifies peers about the existence of some information (block or transaction).
 * @see <a href="https://reference.cash/protocol/network/messages/inv">Announcement: Inventory (“inv”)</a>
 */
public class Inv extends Payload implements Externalizable {
    private List<InventoryItem> inventory;
    public Inv(List<InventoryItem> inventory) {
        this.inventory = inventory;
    }


    public List<InventoryItem> getInventory() {
        return inventory;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        out.writeUTF(json);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String json = in.readUTF();
        Gson gson = new Gson();
        Inv inv = gson.fromJson(json, Inv.class);
        this.inventory = inv.getInventory();
    }
}
