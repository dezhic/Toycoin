package protocol;

import com.google.gson.Gson;
import datatype.Block;
import protocol.Command;
import protocol.message.*;

import java.io.*;

public class Message implements Externalizable {

    private Command command;
    private Inv inv;
    private Version version;
    private Addr addr;
    private GetData getData;
    private Block block;
    private GetBlocks getBlocks;
    private GetHeaders getHeaders;
    private Headers headers;

    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private Command command;
        private Inv inv;
        private Version version;
        private Addr addr;
        private GetData getData;
        private Block block;
        private GetBlocks getBlocks;
        private GetHeaders getHeaders;
        private Headers headers;

        public Builder() {
        }

        public Builder command(Command command) {
            this.command = command;
            return this;
        }

        public Builder inv(Inv inv) {
            this.inv = inv;
            return this;
        }

        public Builder version(Version version) {
            this.version = version;
            return this;
        }

        public Builder addr(Addr addr) {
            this.addr = addr;
            return this;
        }

        public Builder getData(GetData getData) {
            this.getData = getData;
            return this;
        }

        public Builder block(Block block) {
            this.block = block;
            return this;
        }

        public Builder getBlocks(GetBlocks getBlocks) {
            this.getBlocks = getBlocks;
            return this;
        }

        public Builder getHeaders(GetHeaders getHeaders) {
            this.getHeaders = getHeaders;
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

    private Message(Builder builder) {
        this.command = builder.command;
        this.inv = builder.inv;
        this.version = builder.version;
        this.addr = builder.addr;
        this.getData = builder.getData;
        this.block = builder.block;
        this.getBlocks = builder.getBlocks;
        this.getHeaders = builder.getHeaders;
        this.headers = builder.headers;
    }

    public Message() {
        // Empty
    }
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        out.writeObject(json);
        System.out.println("write json: " + json);
//        out.writeInt(json.length());
//        out.flush();
//        System.out.println("write length: " + json.length());
//        out.writeBytes(json);
//        out.flush();
//        System.out.println("write json: " + json);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        int length;
//        while (true) {
//            try {
//                length = in.readInt();
//                System.out.println("read length: " + length);
//                break;
//            } catch (EOFException e) {
////                System.out.print("i");
//                // Ignore
//            }
//        }
//        byte[] bytes = new byte[length];
//        // Read until filling the buffer
//        int read = 0;
//        while (read < length) {
//            read += in.read(bytes, read, length - read);
//        }
        Object obj = in.readObject();
        if (obj instanceof Message) {
            Message message = (Message) obj;
            this.command = message.getCommand();
            this.inv = message.getInv();
            this.version = message.getVersion();
            this.addr = message.getAddr();
            this.getData = message.getGetData();
            this.block = message.getBlock();
            this.getBlocks = message.getGetBlocks();
            this.getHeaders = message.getGetHeaders();
            this.headers = message.getHeaders();
            return;
        }
        String json = (String) obj;
        Gson gson = new Gson();
//        String json = new String(bytes);
        System.out.println("read json: " + json);
        Message message = gson.fromJson(json, Message.class);
        this.command = message.getCommand();
        this.inv = message.getInv();
        this.version = message.getVersion();
        this.addr = message.getAddr();
        this.getData = message.getGetData();
        this.block = message.getBlock();
        this.getBlocks = message.getGetBlocks();
        this.getHeaders = message.getGetHeaders();
        this.headers = message.getHeaders();
    }

    public static Message fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Inv getInv() {
        return inv;
    }

    public void setInv(Inv inv) {
        this.inv = inv;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Addr getAddr() {
        return addr;
    }

    public void setAddr(Addr addr) {
        this.addr = addr;
    }

    public GetData getGetData() {
        return getData;
    }

    public void setGetData(GetData getData) {
        this.getData = getData;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public GetBlocks getGetBlocks() {
        return getBlocks;
    }

    public void setGetBlocks(GetBlocks getBlocks) {
        this.getBlocks = getBlocks;
    }

    public GetHeaders getGetHeaders() {
        return getHeaders;
    }

    public void setGetHeaders(GetHeaders getHeaders) {
        this.getHeaders = getHeaders;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }
}
