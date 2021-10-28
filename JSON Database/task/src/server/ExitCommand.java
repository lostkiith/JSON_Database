package server;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExitCommand implements Command{

    @Override
    public void execute() {
    }

    @Override
    public List<JsonElement> getResolveCode() {
        List<JsonElement> maps = new ArrayList<>();
        return maps;
    }
}