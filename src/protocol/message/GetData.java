package protocol.message;

import protocol.datatype.InventoryItem;

import java.io.Serializable;
import java.util.List;

public class GetData implements Serializable {
    private List<InventoryItem> inventory;
}
