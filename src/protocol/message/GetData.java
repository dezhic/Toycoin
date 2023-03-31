package protocol.message;

import com.google.gson.Gson;
import datatype.InventoryItem;
import protocol.Payload;

import java.io.*;
import java.util.List;

public class GetData extends Payload implements Externalizable {
    private List<InventoryItem> inventory;

    public GetData(List<InventoryItem> inventory) {
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
        GetData getData = gson.fromJson(json, GetData.class);
        this.inventory = getData.getInventory();
    }
}
