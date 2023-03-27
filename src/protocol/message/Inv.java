package protocol.message;

import protocol.datatype.InventoryItem;

import java.io.Serializable;
import java.util.List;

/**
 * Notifies peers about the existence of some information (block or transaction).
 * @see <a href="https://reference.cash/protocol/network/messages/inv">Announcement: Inventory (“inv”)</a>
 */
public class Inv implements Serializable {
    List<InventoryItem> inventory;
    public Inv(List<InventoryItem> inventory) {
        this.inventory = inventory;
    }
}
