package server;

import com.google.gson.JsonElement;

import java.util.List;

public interface Command {

    void execute();

    List<JsonElement> getResolveCode();

}
