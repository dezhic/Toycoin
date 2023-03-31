package protocol.message;

import datatype.InventoryItem;

import java.io.Serializable;
import java.util.List;

public class GetData implements Serializable {
    private List<InventoryItem> inventory;

    public GetData(List<InventoryItem> inventory) {
        this.inventory = inventory;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }
}
