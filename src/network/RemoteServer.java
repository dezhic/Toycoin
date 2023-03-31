package network;

import protocol.Command;
import protocol.Message;
import datatype.Block;
import protocol.message.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RemoteServer {
    private Socket socket;

    public RemoteServer(Socket socket) {
        this.socket = socket;
    }

    public void sendInv(Inv inv) throws IOException {
        Message msg = new Message(Command.INV, inv);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(msg);
    }

    public void sendVersion(Version version) throws IOException {
        Message message = new Message(Command.VERSION, version);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent version to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendVerAck() throws IOException {
        Message message = new Message(Command.VERACK, null);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent verack to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetAddr() throws IOException {
        Message message = new Message(Command.GETADDR, null);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent getaddr to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendAddr(Addr addr) throws IOException {
        Message message = new Message(Command.ADDR, addr);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent addr to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetData(GetData getData) throws IOException {
        Message message = new Message(Command.GETDATA, getData);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent getdata to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendBlock(Block block) throws IOException {
        Message message = new Message(Command.BLOCK, block);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent block to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetBlocks(GetBlocks getBlocks) throws IOException {
        Message message = new Message(Command.GETBLOCKS, getBlocks);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent getblocks to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendGetHeaders(GetHeaders getHeaders) throws IOException {
        Message message = new Message(Command.GETHEADERS, getHeaders);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent getheaders to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void sendHeaders(Headers headers) throws IOException {
        Message message = new Message(Command.HEADERS, headers);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        System.out.println("Sent headers to " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public Socket getSocket() {
        return socket;
    }
}
