package network;

import com.google.gson.Gson;
import datatype.Transaction;
import protocol.Command;
import protocol.Message;
import datatype.Block;
import protocol.message.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RemoteServer {
    private Socket socket;

//    private ObjectOutputStream oos;

    private DataOutputStream dos;
    public RemoteServer(Socket socket) {
        this.socket = socket;
        try {
//            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(Message message) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(message);
        byte[] bytes = json.getBytes();
        dos.writeInt(bytes.length);
        dos.write(bytes);
        dos.flush();
    }

    public void sendInv(Inv inv) throws IOException {
        Message msg = Message.builder()
                .command(Command.INV)
                .inv(inv)
                .build();
//        oos.writeObject(msg);
//        oos.flush();
        send(msg);
    }

    public void sendVersion(Version version) throws IOException {
        Message message = Message.builder()
                        .command(Command.VERSION)
                        .version(version)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent version to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendVerAck() throws IOException {
        Message message = Message.builder()
                        .command(Command.VERACK)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent verack to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetAddr() throws IOException {
        Message message = Message.builder()
                        .command(Command.GETADDR)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent getaddr to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendAddr(Addr addr) throws IOException {
        Message message = Message.builder()
                        .command(Command.ADDR)
                        .addr(addr)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent addr to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetData(GetData getData) throws IOException {
        Message message = Message.builder()
                        .command(Command.GETDATA)
                        .getData(getData)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent getdata to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendBlock(Block block) throws IOException {
        Message message = Message.builder()
                        .command(Command.BLOCK)
                        .block(block)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent block to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetBlocks(GetBlocks getBlocks) throws IOException {
        Message message = Message.builder()
                        .command(Command.GETBLOCKS)
                        .getBlocks(getBlocks)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent getblocks to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetHeaders(GetHeaders getHeaders) throws IOException {
        Message message = Message.builder()
                        .command(Command.GETHEADERS)
                        .getHeaders(getHeaders)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent getheaders to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendHeaders(Headers headers) throws IOException {
//        Message message = new Message(Command.HEADERS, headers);
        Message message = Message.builder()
                        .command(Command.HEADERS)
                        .headers(headers)
                        .build();
//        oos.writeObject(message);
//        oos.flush();
        send(message);
        System.out.println("Sent headers to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendTransaction(Transaction tx) {
        Message message = Message.builder()
                        .command(Command.TX)
                        .transaction(tx)
                        .build();
        try {
            send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
