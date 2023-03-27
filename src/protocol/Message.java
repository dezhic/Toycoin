package protocol;

import protocol.Command;

import java.io.Serializable;

public class Message implements Serializable {
    public Command getCommand() {
        return command;
    }

    public Serializable getPayload() {
        return payload;
    }

    private Command command;
    private Serializable payload;

    public Message(Command command, Serializable payload) {
        this.command = command;
        this.payload = payload;
    }
}
