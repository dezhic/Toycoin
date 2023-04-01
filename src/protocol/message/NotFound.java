package protocol.message;

import com.google.gson.Gson;
import datatype.InventoryItem;

import java.io.*;
import java.util.List;

public class NotFound {
    private List<InventoryItem> inventory;

    public List<InventoryItem> getInventory() {
        return inventory;
    }

}
