package server;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

public class MessageBroker {

    Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        command.execute();
    }

    public List<JsonElement> getResolveCode() {
        return command.getResolveCode();
    }

}
