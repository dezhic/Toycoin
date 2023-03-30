package protocol.datatype;

import java.io.Serializable;

public class InventoryItem implements Serializable {
    private InventoryType type;
    private String hash;

    public InventoryItem(InventoryType type, String hash) {
        this.type = type;
        this.hash = hash;
    }

    public InventoryType getType() {
        return type;
    }

    public String getHash() {
        return hash;
    }
}
