package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

public class SetCommand implements Command{

    DataBase database;
    JsonElement message;


    public SetCommand(DataBase dataBase, JsonObject message) {
        this.database = dataBase;
        this.message = message;
    }

    @Override
    public void execute() {

        if (message.getAsJsonObject().get("key").isJsonArray()) {
            JsonArray setArr = message.getAsJsonObject().get("key").getAsJsonArray();
            JsonElement MainKey = database.getEntry(setArr.get(0).getAsString());
            JsonElement chainMessage = null;
            for (int i = 0 ; i < setArr.size() - 1; i++) {
                chainMessage = MainKey.getAsJsonObject().get(setArr.get(i).getAsString());

            }
            Objects.requireNonNull(chainMessage).getAsJsonObject().addProperty(setArr.get(setArr.size() - 1).getAsString()
                    ,message.getAsJsonObject().get("value").getAsString());

            database.setEntry(setArr.get(0).getAsString(), MainKey);
        } else {

            if (message.getAsJsonObject().get("value").isJsonObject()) {
                System.out.println(message.getAsJsonObject().get("value"));

                database.setEntry(message.getAsJsonObject().get("key").getAsString(),
                        message.getAsJsonObject().get("value").getAsJsonObject());
            } else {
                System.out.println(message.getAsJsonObject().get("value"));

                database.setEntry(message.getAsJsonObject().get("key").getAsString(),
                        message.getAsJsonObject().get("value"));
            }
        }
    }

    @Override
    public List<JsonElement> getResolveCode() {
        List<JsonElement> returnMessage = new ArrayList<>();
        String ok = "{'response': 'OK'}";
        JsonElement okEl = JsonParser.parseString(ok);
        returnMessage.add(okEl);
        return returnMessage;
    }


}
