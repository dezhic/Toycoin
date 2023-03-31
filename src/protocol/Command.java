package protocol;

import com.google.gson.Gson;

import java.io.*;

/**
 * The command field in a message is a string that identifies the type of message.
 * Referencing the Bitcoin protocol
 * @see <a href="https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure">Protocol Documentation - Message Structure</a>
 */
public enum Command {
    VERSION, VERACK,
    GETADDR, ADDR,
    GETHEADERS, HEADERS,
    GETBLOCKS, INV,
    GETDATA, BLOCK, TX, NOTFOUND;

}
