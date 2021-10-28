package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.*;

public class DeleteCommand implements Command{

    DataBase dataBase;
    JsonElement message;
    private final List<JsonElement> resolveCode;

    public DeleteCommand(DataBase database, JsonElement message) {
        this.dataBase = database;
        this.message = message;
        resolveCode = new ArrayList<>();
    }

    @Override
    public void execute() {

        JsonElement errorEl = getJsonElement("{'response': 'ERROR'}");
        JsonElement reasonErrorEl = getJsonElement("{'reason': 'ERROR'}");
        JsonElement okEl = getJsonElement("{'response': 'OK'}");

        if (message.getAsJsonObject().get("key").isJsonArray()) {
            JsonArray delArr = message.getAsJsonObject().get("key").getAsJsonArray();
            JsonElement MainKey = dataBase.getEntry(delArr.get(0).getAsString());

            JsonElement chainMessage = null;
            for (int i = 0 ; i < delArr.size() - 1; i++) {
                chainMessage = MainKey.getAsJsonObject().get(delArr.get(i).getAsString());
                System.out.println(chainMessage);
            }
            Objects.requireNonNull(chainMessage).
                    getAsJsonObject().remove(delArr.get(delArr.size() - 1).getAsString());
            dataBase.setEntry(delArr.get(0).getAsString(), MainKey);

            resolveCode.add(okEl);

        } else {
            JsonElement returnMessage = dataBase.deleteEntry(message.getAsJsonObject().get("key").getAsString());

            if (returnMessage == null) {
                resolveCode.add(errorEl);
                resolveCode.add(reasonErrorEl);
            } else {
                resolveCode.add(okEl);
            }
        }
    }

    private JsonElement getJsonElement(String s) {
        return JsonParser.parseString(s);
    }

    @Override
    public List<JsonElement> getResolveCode() {
        return resolveCode;
    }
}
