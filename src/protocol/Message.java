package protocol;

import protocol.Command;

import java.io.Serializable;

public class Message implements Serializable {
    private Command command;
    private Serializable payload;
}
