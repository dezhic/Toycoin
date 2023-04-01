package protocol.message;

import com.google.gson.Gson;
import datatype.InventoryItem;

import java.io.*;
import java.util.List;

/**
 * Notifies peers about the existence of some information (block or transaction).
 * @see <a href="https://reference.cash/protocol/network/messages/inv">Announcement: Inventory (“inv”)</a>
 */
public class Inv {
    private List<InventoryItem> inventory;
    public Inv(List<InventoryItem> inventory) {
        this.inventory = inventory;
    }


    public List<InventoryItem> getInventory() {
        return inventory;
    }

}
