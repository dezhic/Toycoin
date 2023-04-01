package protocol.message;

import com.google.gson.Gson;
import datatype.InventoryItem;

import java.io.*;
import java.util.List;

public class GetData {
    private List<InventoryItem> inventory;

    public GetData(List<InventoryItem> inventory) {
        this.inventory = inventory;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

}
