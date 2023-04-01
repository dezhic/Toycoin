package network;

import protocol.Message;
import datatype.Block;
import datatype.Header;
import datatype.InventoryItem;
import protocol.message.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static datatype.InventoryType.MSG_BLOCK;
import static datatype.InventoryType.MSG_TX;

/**
 * This is the class that will be <b>used by the server</b> to handle the client
 */
public class RemoteClient extends Thread {
    private Socket socket;
    private LocalClient localClient;
    private LocalServer localServer;

    /**
     * @param socket the socket of the client, for messaging
     * @param localServer the localServer object, for removing the client
     */
    public RemoteClient(Socket socket, LocalServer localServer, LocalClient localClient) {
        this.socket = socket;
        this.localServer = localServer;
        this.localClient = localClient;
    }

    @Override
    public void run() {
        listen();
    }

    private void listen() {
//        ObjectInputStream ois = null;
        DataInputStream dis = null;
        try {
//            ois = new ObjectInputStream(socket.getInputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
//                Message message = (Message) ois.readObject();
                int length = dis.readInt();
                byte[] bytes = dis.readNBytes(length);
                String json = new String(bytes);
                System.out.println("Received: " + json + " from " + socket.getInetAddress() + ":" + socket.getPort());
                Message message = Message.fromJson(json);
                switch (message.getCommand()) {
                    case INV:
                        handleInv(message.getInv());
                        break;
                    case VERSION:
                        handleVersion(message.getVersion());
                        break;
                    case VERACK:
                        break;
                    case GETADDR:
                        handleGetAddr();
                        break;
                    case ADDR:
                        handleAddr(message.getAddr());
                        break;
                    case GETDATA:
                        handleGetData(message.getGetData());
                        break;
                    case BLOCK:
                        handleBlock(message.getBlock());
                        break;
                    case GETBLOCKS:
                        handleGetBlocks(message.getGetBlocks());
                        break;
                    case GETHEADERS:
                        handleGetHeaders(message.getGetHeaders());
                        break;
                    case HEADERS:
                        handleHeaders(message.getHeaders());
                        break;
                    default:
                        throw new Exception("Unknown command: " + message.getCommand());
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Bad client, f*** off!");
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                localServer.removeClient(this);
                break;
            }

        }
    }

    private void handleBlock(Object payload) {
        Block block = (Block) payload;
        // check if this is the genesis block
        if (block.getIndex() == 0) {
            System.out.println("Genesis block received");
            if (localClient.hasBlock(block.getHash())) {
                System.out.println("Already have genesis block");
                return;
            }
            localClient.addBlock(block);
            return;
        } else {
            // check if genesis block exists in the local blockchain
            if (localClient.getLastBlock() == null) {
                System.out.println("Genesis block not found. Please sync first");
                return;
            }
        }

        // check if block already exists
        if (localClient.hasBlock(block.getHash())) {
            System.out.println("Already have block " + block.getHash());
            return;
        }
        // check if block is valid
        if (localClient.getLastBlock() == null ||
                block.getPreviousHash().equals(localClient.getLastBlock().getHash())) {
            // add block to blockchain
            localClient.addBlock(block);
        } else {
            System.out.println("datatype.Block is not valid");
        }
    }


    private void handleGetData(Object payload) {
        GetData getData = (GetData) payload;
        List<InventoryItem> inventory = getData.getInventory();
        for (InventoryItem item : inventory) {
            if (item.getType() == MSG_BLOCK) {
                // check if we have the block
                if (localClient.hasBlock(item.getHash())) {
                    System.out.println("Sending block " + item.getHash());
                    localClient.sendBlock(localClient.getRemoteServer(socket.getInetAddress().getHostAddress(), socket.getPort()),
                            localClient.getBlock(item.getHash()));
                } else {
                    System.out.println("Don't have block " + item.getHash());
                }
            } else if (item.getType() == MSG_TX) {
                // TODO
            }
        }
    }

    private void handleInv(Object payload) {
        Inv inv = (Inv) payload;
//        Gson gson = new Gson();
//        System.out.println(gson.toJson(inv));
        List<InventoryItem> inventory = inv.getInventory();
        Iterator<InventoryItem> iterator = inventory.iterator();
        // skip already known blocks
        while (iterator.hasNext()) {
            InventoryItem item = iterator.next();
            if (item.getType() == MSG_BLOCK) {
                // check if we have the block
                if (localClient.hasBlock(item.getHash())) {
                    System.out.println("Already have block " + item.getHash());
                } else {
                    // request block headers for the rest
                    List<String> locator = new LinkedList<>();
                    locator.add(item.getHash());
                    while (iterator.hasNext()) {
                        item = iterator.next();
                        if (item.getType() == MSG_BLOCK) {
                            locator.add(item.getHash());
                        }
                    }
                    GetHeaders getHeaders = new GetHeaders(locator, null);
                    localClient.sendGetHeaders(localClient.getRemoteServer(socket.getInetAddress().getHostAddress(), socket.getPort()), getHeaders);

                }

            } else if (item.getType() == MSG_TX) {
                // TODO
//                localClient.sendGetData(socket.getInetAddress().getHostAddress(), item.getHash());
            }
        }

    }

    private void handleVersion(Object payload) {
        Version version = (Version) payload;
        String[] address = version.getLocalAddress().split(":");
        try {
            RemoteServer rs = localClient.addServer(address[0], Integer.parseInt(address[1]), socket.getPort());
            localClient.sendVerAck(rs);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot connect to " + version.getLocalAddress() + " when sending verack");
        }
    }

    private void handleGetAddr() {
        RemoteServer rs = localClient.getRemoteServer(socket.getInetAddress().getHostAddress(), socket.getPort());
        localClient.sendAddr(rs);
    }

    private void handleAddr(Object payload) {
        Addr addr = (Addr) payload;
        System.out.println("Received " + addr.getAddresses().size() + " addresses from " +
                socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        for (String address : addr.getAddresses()) {

            System.out.println("Connecting to " + address);
            String[] addressSplit = address.split(":");
            String myPort = System.getenv("PORT");
            if ((addressSplit[0].equals("localhost")) || (addressSplit[0].equals("127.0.0.1")) && (addressSplit[1].equals(myPort))) {
                System.out.println("Skipping myself");
                continue; // skip myself
            }
            try {
                localClient.addServer(addressSplit[0], Integer.parseInt(addressSplit[1]), -1);
            } catch (IOException e) {
                System.out.println("Cannot connect to " + address + " when sending addr");
            }
        }
    }

    /**
     * Return an inv packet containing the list of blocks starting right after the last known hash in the block locator object, up to hash_stop or 500 blocks, whichever comes first.
     * The locator hashes are processed by a node in the order as they appear in the message. If a block hash is found in the node's main chain, the list of its children is returned back via the inv message and the remaining locators are ignored, no matter if the requested limit was reached, or not.
     * To receive the next blocks hashes, one needs to issue getblocks again with a new block locator object. Keep in mind that some clients may provide blocks which are invalid if the block locator object contains a hash on the invalid branch.
     *
     * @see <a href="https://en.bitcoin.it/wiki/Protocol_documentation#getblocks">https://en.bitcoin.it/wiki/Protocol_documentation#getblocks</a>
     */
    private void handleGetBlocks(Object payload) {
        GetBlocks getBlocks = (GetBlocks) payload;
        List<String> locator = getBlocks.getLocator();
        String hashStop = getBlocks.getHashStop();
        List<InventoryItem> inventory = new LinkedList<>();

        // return the list of blocks starting right after the last known hash in the block locator object,
        // up to hash_stop or 500 blocks, whichever comes first.
        for (String hash : locator) {
            if (localClient.hasBlock(hash)) {
                Block block = localClient.getBlock(hash);
                while (block != null && !block.getHash().equals(hashStop) && inventory.size() < 500) {
                    inventory.add(new InventoryItem(MSG_BLOCK, block.getHash()));
                    block = localClient.getBlock(block.getIndex() + 1);
                }
            }
            break;
        }
        // locator not found, return the genesis block
        if (inventory.isEmpty() && localClient.getBlock(0) != null) {
            inventory.add(new InventoryItem(MSG_BLOCK, localClient.getBlock(0).getHash()));
        }

        localClient.sendInv(localClient.getRemoteServer(socket.getInetAddress().getHostAddress(), socket.getPort()), new Inv(inventory));

    }

    private void handleGetHeaders(Object payload) {
        GetHeaders getHeaders = (GetHeaders) payload;
        List<String> locator = getHeaders.getLocator();
        String hashStop = getHeaders.getHashStop();
        List<Block> blocks = new LinkedList<>();

        // return the list of blocks starting right after the last known hash in the block locator object,
        // up to hash_stop or 2000 blocks, whichever comes first.
        for (String hash : locator) {
            if (localClient.hasBlock(hash)) {
                Block block = localClient.getBlock(hash);
                while (block != null && !block.getHash().equals(hashStop) && blocks.size() < 2000) {
                    blocks.add(block);
                    block = localClient.getBlock(block.getIndex() + 1);
                }
            }
            break;
        }
        // locator not found, return the genesis block
        if (blocks.isEmpty() && localClient.getBlock(0) != null) {
            blocks.add(localClient.getBlock(0));
        }

        List<Header> headers = blocks.stream().map((block ->
            new Header(
                    block.getIndex(),
                    block.getHash(),
                    block.getPreviousHash(),
                    block.getTimestamp(),
                    block.getData(),
                    block.getDifficulty(),
                    block.getNonce()
            )
        )).collect(Collectors.toList());

        localClient.sendHeaders(localClient.getRemoteServer(socket.getInetAddress().getHostAddress(), socket.getPort()), new Headers(headers));

    }

    public void handleHeaders(Object payload) {
        System.out.println("Received headers from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        Headers headers = (Headers) payload;
        // skip headers that are already known
        LinkedList<Header> newHeaders = headers.getHeaders().stream()
                .filter((header) -> (!localClient.hasBlock(header.getHash()))).collect(Collectors.toCollection(LinkedList::new));

        if (newHeaders.isEmpty()) {
            return;
        }

        // Longest chain rule
        if (localClient.getLastBlock() == null) {  // new client
            // request the new blocks
            localClient.sendGetData(
                    localClient.getRemoteServer(socket.getInetAddress().getHostAddress(), socket.getPort()),
                    new GetData(newHeaders.stream()
                            .map((header) -> new InventoryItem(MSG_BLOCK, header.getHash()))
                            .collect(Collectors.toList())
                    )
            );

        } else if (newHeaders.getLast().getIndex() > localClient.getLastBlock().getIndex()) {
            // when the new headers are longer than the current chain
            // check if the new headers are connected to the current chain
            if (newHeaders.getFirst().getIndex() == 0 || localClient.hasBlock(newHeaders.getFirst().getPreviousHash())) {
                // prune the current chain and request the new blocks
                localClient.prune(newHeaders.getFirst().getPreviousHash());
                localClient.sendGetData(
                        localClient.getRemoteServer(socket.getInetAddress().getHostAddress(), socket.getPort()),
                        new GetData(newHeaders.stream()
                                .map((header) -> new InventoryItem(MSG_BLOCK, header.getHash()))
                                .collect(Collectors.toList())
                        )
                );
            } else {
                // request the headers from the last known block
                localClient.sendGetHeaders(localClient.getRemoteServer(socket.getInetAddress().getHostAddress(), socket.getPort()),
                        new GetHeaders(Collections.singletonList(localClient.getLastBlock().getHash()), null));
            }
        }

    }

}
