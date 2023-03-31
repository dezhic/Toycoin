package protocol.message;

import com.google.gson.Gson;
import datatype.InventoryItem;
import protocol.Payload;

import java.io.*;
import java.util.List;

public class NotFound extends Payload implements Externalizable {
    private List<InventoryItem> inventory;

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
        NotFound notFound = gson.fromJson(json, NotFound.class);
        this.inventory = notFound.getInventory();

    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

}
